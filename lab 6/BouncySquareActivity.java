package com.example.lab5textures;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class BouncySquareActivity extends Activity {

    private GLSurfaceView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cream un GLSurfaceView ca suprafata de randare OpenGL
        view = new GLSurfaceView(this);

        // Trimitem contextul aplicatiei catre renderer
        // (necesar pentru incarcarea texturii din resurse)
        view.setRenderer(new SquareRenderer(this.getApplicationContext()));

        setContentView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }
}