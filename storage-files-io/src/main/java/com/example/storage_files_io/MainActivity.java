package com.example.storage_files_io;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_STORAGE_WRITE_PERMS = 100;

    private String content;
    private String fileName;
    private String folderName;

    private enum Method {
        privateExternal, publicExternal, internal, cachedInternal
    }

    private Method method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        folderName = "my_files";
        fileName = "information";
        content = "Hello world!";

        method = Method.cachedInternal;

        switch (method) {
            case privateExternal:
                Log.i(TAG, "onCreate: writing/reading on private external storage.");
                manageExternalStorage();
                break;
            case publicExternal:
                Log.i(TAG, "onCreate: writing/reading on public external storage.");
                manageExternalStorage();
                break;
            case internal:
                Log.i(TAG, "onCreate: writing/reading on internal storage.");
                writeOnInternalStorage(content, fileName, folderName);
                ((TextView) findViewById(R.id.myTextView)).setText(readFromInternalStorage(fileName, folderName));
                break;
            case cachedInternal:
                Log.i(TAG, "onCreate: writing/reading on cached internal storage.");
                writeOnCachedInternalStorage(content, fileName, folderName);
                ((TextView) findViewById(R.id.myTextView)).setText(readFromCachedInternalStorage(fileName, folderName));
                break;
        }
    }

    private void manageExternalStorage() {
        // Check the permission to write on external storage
        Log.i(TAG, "manageExternalStorage: checking permission.");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "manageExternalStorage: Write permission not granted. Requesting permission.");
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RC_STORAGE_WRITE_PERMS);
        } else {
            // Permission has already been granted
            Log.i(TAG, "manageExternalStorage: Write permission granted.");

            if (StorageUtils.isExternalStorageWritable()) {

                if (method == Method.privateExternal) {
                    writeOnPrivateExternalStorage(content, fileName, folderName);
                    ((TextView) findViewById(R.id.myTextView)).setText(readFromPrivateExternalStorage(fileName, folderName));
                } else if (method == Method.publicExternal) {
                    writeOnPublicExternalStorage(content, fileName, folderName);
                    ((TextView) findViewById(R.id.myTextView)).setText(readFromPublicExternalStorage(fileName, folderName));
                }
            } else {
                Log.e(TAG, "manageExternalStorage: External storage not writable");
            }
        }
    }

    @Override
    // After permission granted or refused
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RC_STORAGE_WRITE_PERMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i(TAG, "onRequestPermissionsResult: permission write to external storage granted");

                    if (StorageUtils.isExternalStorageWritable()) {

                        if (method == Method.privateExternal) {
                            writeOnPrivateExternalStorage(content, fileName, folderName);
                            ((TextView) findViewById(R.id.myTextView)).setText(readFromPrivateExternalStorage(fileName, folderName));
                        } else if (method == Method.publicExternal) {
                            writeOnPublicExternalStorage(content, fileName, folderName);
                            ((TextView) findViewById(R.id.myTextView)).setText(readFromPublicExternalStorage(fileName, folderName));
                        }
                    } else {
                        Log.e(TAG, "onRequestPermissionsResult: External storage not writable");
                    }
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

    // ----------------------------------
    // EXTERNAL STORAGE
    // ----------------------------------

    // READ

    private String readFromPublicExternalStorage(String fileName, String folderName){
        if (StorageUtils.isExternalStorageReadable()){
            // External - Public
            return StorageUtils.getTextFromStorage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),this, fileName, folderName);
        } else {
            Toast.makeText(this, "external storage not readable", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private String readFromPrivateExternalStorage(String fileName, String folderName){
        if (StorageUtils.isExternalStorageReadable()){
            // External - Private
            return StorageUtils.getTextFromStorage(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),this, fileName, folderName);
        } else {
            Toast.makeText(this, "external storage not readable", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    // WRITE

    private void writeOnPublicExternalStorage(String content, String fileName, String folderName){
        if (StorageUtils.isExternalStorageWritable()){
            StorageUtils.setTextInStorage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), this, fileName, folderName, content);

        } else {
            Toast.makeText(this, "external storage not writable", Toast.LENGTH_LONG).show();
        }
    }

    private void writeOnPrivateExternalStorage(String content, String fileName, String folderName){
        if (StorageUtils.isExternalStorageWritable()){
            StorageUtils.setTextInStorage(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), this, fileName, folderName, content);
        } else {
            Toast.makeText(this, "external storage not writable", Toast.LENGTH_LONG).show();
        }
    }

    // ----------------------------------
    // INTERNAL STORAGE
    // ----------------------------------

    // READ

    private String readFromInternalStorage(String fileName, String folderName){
        return StorageUtils.getTextFromStorage(getFilesDir(), this, fileName, folderName);
    }

    private String readFromCachedInternalStorage(String fileName, String folderName){
        return StorageUtils.getTextFromStorage(getCacheDir(), this, fileName, folderName);
    }

    // WRITE

    private void writeOnInternalStorage(String content, String fileName, String folderName){
        StorageUtils.setTextInStorage(getFilesDir(), this, fileName, folderName, content);
    }

    private void writeOnCachedInternalStorage(String content, String fileName, String folderName){
        StorageUtils.setTextInStorage(getCacheDir(), this, fileName, folderName, content);
    }

}
