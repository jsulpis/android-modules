package com.example.opengles_basics;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OpenGLES20Activity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        GLSurfaceView mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }
}