package com.example.lab4lights;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new GLSurfaceView(this);

        // Forteaza OpenGL ES 1.1 explicit
        mGLView.setEGLContextClientVersion(1);

        // Configurare explicita EGL pentru compatibilitate emulator
        mGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        mGLView.setRenderer(new BouncyCubeRenderer());

        // Render only when there is a change in the drawing data
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}