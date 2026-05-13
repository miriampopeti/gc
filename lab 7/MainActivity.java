package com.example.lab6blending;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cream un GLSurfaceView si ii setam renderer-ul
        mGLSurfaceView = new GLSurfaceView(this);

        // Specificam versiunea OpenGL ES 1.x
        mGLSurfaceView.setEGLContextClientVersion(1);

        // Setam renderer-ul nostru
        mGLSurfaceView.setRenderer(new BouncySquareRenderer(this));

        // Mod continuu de redare (animatie)
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(mGLSurfaceView);
    }

    // -----------------------------------------------------------------------
    // Gestionam ciclul de viata al aplicatiei
    // -----------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }
}