package com.example.opengles_load_obj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private MyGLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a GLSurfaceView instance and set it as the child view
        // of the linear layout.
        Mesh suzanne = new Mesh(this, "suzanne.obj");
        mGLView = new MyGLSurfaceView(this, suzanne);
        ((LinearLayout) findViewById(R.id.surface_view)).addView(mGLView);
    }
}
