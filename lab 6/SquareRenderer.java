package com.example.lab5textures;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SquareRenderer implements GLSurfaceView.Renderer {

    private Context context;
    private Square mSquare;

    // Constructorul primeste contextul aplicatiei
    public SquareRenderer(Context context) {
        this.context = context;
        this.mSquare = new Square();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Culoarea de fundal: negru
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Activam testul de adancime
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        // Obtinem ID-ul resursei imaginii hedly.png din /res/drawable
        int resid = R.drawable.hedly;

        // Cream textura folosind imaginea hedly.png
        mSquare.createTexture(gl, this.context, resid);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Setam viewport-ul la dimensiunile ecranului
        gl.glViewport(0, 0, width, height);

        // Setam matricea de proiectie
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        float ratio = (float) width / height;
        // Proiectie ortografica simpla
        gl.glOrthof(-ratio, ratio, -1, 1, -1, 1);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Stergem bufferul de culoare si adancime
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Setam matricea model-view
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Desenam patratul cu textura
        mSquare.draw(gl);
    }
}