package com.example.camera_photo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;

    // Identifier for the permission to ask
    private static final int RC_STORAGE_WRITE_PERMS = 100;

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.picture);

        // Check the permission to write on external storage
        Log.i(TAG, "onCreate: checking permission.");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            Log.i(TAG, "onCreate: Write permission not granted. Requesting permission.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RC_STORAGE_WRITE_PERMS);

            // RC_STORAGE_WRITE_PERMS is an app-defined int constant.
            // The callback method gets the result of the request.
        } else {
            // Permission has already been granted
            Log.i(TAG, "onCreate: Write permission granted.");
            dispatchTakePictureIntent();
        }
    }

    /**
     * Send an Intent to take a picture
     */
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.camera_photo.fileprovider",
                        photoFile);
                // Uncomment the following line and replace "REQUEST_TAKE_PHOTO" by "REQUEST_IMAGE_CAPTURE"
                // to get only the thumbnail
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Create a file on external storage
     * @return the new file
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        if (!isExternalStorageWritable()) {
            Log.e(TAG, "createImageFile: External storage not writable");
            return null;
        }

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        /* For public storage (visible in the gallery) */
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        /* For private storage (not visible in the gallery) */
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = new File(
                storageDir,    /* directory */
                imageFileName  /* name */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile() returned: " + mCurrentPhotoPath);
        return image;
    }

    /**
     * Callback to handle the Intent received from the application that took the picture
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            // add the picture to the gallery
            galleryAddPic();

            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    /**
     * Add a picture to the gallery
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Log.i(TAG, "galleryAddPic: picture successfully added to library.");
    }


    /**
     * Callback to handle the response to a permission request
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RC_STORAGE_WRITE_PERMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult: permission write to external storage granted");
                    dispatchTakePictureIntent();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "onRequestPermissionsResult: permission write to external storage denied");
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    /**
     * Checks if external storage is available to read and write
     * @return a boolean
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
