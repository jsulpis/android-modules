package com.example.opengles_load_obj;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

    public MyGLSurfaceView(Context context, Mesh obj){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        MyGLRenderer renderer = new MyGLRenderer(obj);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}