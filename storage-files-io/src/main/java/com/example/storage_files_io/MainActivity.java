package com.example.storage_files_io;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String filename = "myfile";
        String fileContents = "Hello world!";

        // Write the file
        writeFileToInternalStorage(filename, fileContents);

        // Read the file and display its content in the console and in the text view
        Log.i(TAG, "onCreate: " + readFileFromInternalStorage(filename));
        ((TextView) findViewById(R.id.myTextView)).setText(readFileFromInternalStorage(filename));
    }

    private void writeFileToInternalStorage(String filename, String fileContents) {
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, MODE_PRIVATE);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFileFromInternalStorage(String filename) {
        //Get the text file
        File file = new File(getFilesDir(), filename);

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }
}
