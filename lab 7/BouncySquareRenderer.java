package com.example.lab6blending;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.opengl.GLSurfaceView;

public class BouncySquareRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    // Doua patrate separate
    private Square mSquare1;   // primul patrat (albastru / YMCA / textura 1)
    private Square mSquare2;   // al doilea patrat (rosu / RGBA / textura 2)

    private float mTransY = 0.0f;

    // -----------------------------------------------------------------------
    // Selecteaza sectiunea laboratorului de rulat:
    //   1 = Alpha Blending simplu (patrate colorate solid)
    //   2 = Blending cu culori per vertex (Multicolor Blending)
    //   3 = Texture Blending (o singura textura + transparenta)
    //   4 = Multitexturare
    // -----------------------------------------------------------------------
    private static final int SECTION = 3;

    public BouncySquareRenderer(Context context) {
        mContext = context;
        mSquare1 = new Square();
        mSquare2 = new Square();
    }

    // -----------------------------------------------------------------------
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Fundal negru pentru a vedea mai bine blending-ul
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Activam testul de profunzime (dezactivat unde e necesar)
        gl.glEnable(GL10.GL_DEPTH_TEST);

        // Incarcam texturile doar pentru sectiunile 3 si 4
        if (SECTION == 3 || SECTION == 4) {
            // Asigura-te ca ai adaugat hedly.png si splash.png in res/drawable
            mSquare1.setTextures(gl, mContext, R.drawable.hedly, R.drawable.splash);
            mSquare2.setTextures(gl, mContext, R.drawable.goldengate, R.drawable.splash);
        }
    }

    // -----------------------------------------------------------------------
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);

        // Setam proiectia perspectiva
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        float ratio = (float) width / height;
        // Frustra simpla
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
    }

    // -----------------------------------------------------------------------
    @Override
    public void onDrawFrame(GL10 gl) {
        // Stergem bufferele
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL11.GL_MODELVIEW);

        switch (SECTION) {
            case 1: drawSection1_AlphaBlending(gl); break;
            case 2: drawSection2_MulticolorBlending(gl); break;
            case 3: drawSection3_TextureBlending(gl); break;
            case 4: drawSection4_Multitexturing(gl); break;
        }

        mTransY += 0.075f;
    }

    // =======================================================================
    // SECTIUNEA 1 – Alpha Blending simplu
    // =======================================================================
    private void drawSection1_AlphaBlending(GL10 gl) {
        // Pentru blending corect dezactivam z-buffering
        gl.glDisable(GL10.GL_DEPTH_TEST);

        // Activam blending
        gl.glEnable(GL10.GL_BLEND);

        // --------------- Experimenta cu aceste doua functii ---------------
        // Transparenta clasica:
        //gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        // Intensitate maxima (fara canal alpha):
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
        // ------------------------------------------------------------------

        // Patrat 1 – ALBASTRU, se misca sus-jos, alpha = 1.0
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, (float) Math.sin(mTransY), -3.0f);
        gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);   // albastru, complet opac
        mSquare1.draw(gl);

        // Patrat 2 – ROSU, se misca stanga-dreapta, alpha = 0.5 (semitransparent)
        gl.glLoadIdentity();
        gl.glTranslatef((float) (Math.sin(mTransY) / 2.0f), 0.0f, -2.9f);
        gl.glColor4f(1.0f, 0.0f, 0.0f, 0.5f);   // rosu, 50% transparent
        mSquare2.draw(gl);

        gl.glDisable(GL10.GL_BLEND);
        gl.glEnable(GL10.GL_DEPTH_TEST);

        // Rezultat asteptat la intersectie:
        //   Red   = 1.0 * 0.5 + (1-0.5) * 0.0 = 0.5
        //   Green = 0.0 * 0.5 + (1-0.5) * 0.0 = 0.0
        //   Blue  = 0.0 * 0.5 + (1-0.5) * 1.0 = 0.5  → MAGENTA
    }

    // =======================================================================
    // SECTIUNEA 2 – Multicolor Blending (culori per vertex)
    // =======================================================================
    private void drawSection2_MulticolorBlending(GL10 gl) {
        gl.glDisable(GL10.GL_DEPTH_TEST);

        gl.glEnable(GL10.GL_BLEND);
        // Incearca si GL_ONE/GL_ONE pentru intensitate maxima
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        // Patrat 1 – culori YMCA (galben, cyan, negru, magenta)
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, (float) Math.sin(mTransY), -3.0f);
        mSquare1.drawColored(gl);

        // Patrat 2 – culori RGBA (rosu, verde, albastru, alb)
        gl.glLoadIdentity();
        gl.glTranslatef((float) (Math.sin(mTransY) / 2.0f), 0.0f, -2.9f);
        mSquare2.drawColored(gl);

        gl.glDisable(GL10.GL_BLEND);
        gl.glEnable(GL10.GL_DEPTH_TEST);
    }

    // =======================================================================
    // SECTIUNEA 3 – Texture Blending
    // =======================================================================
    private void drawSection3_TextureBlending(GL10 gl) {
        gl.glDisable(GL10.GL_DEPTH_TEST);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        // Patrat 1 – textura hedly.png, complet opac, alb (fara tinta)
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, (float) Math.sin(mTransY), -3.0f);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);   // fara colorare
        mSquare1.drawTextured(gl);

        // Patrat 2 – textura goldengate.png, 75% opac
        gl.glLoadIdentity();
        gl.glTranslatef((float) (Math.sin(mTransY) / 2.0f), 0.0f, -2.9f);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);  // usor transparent
        mSquare2.drawTextured(gl);

        gl.glDisable(GL10.GL_BLEND);
        gl.glEnable(GL10.GL_DEPTH_TEST);
    }

    // =======================================================================
    // SECTIUNEA 4 – Multitexturare
    // =======================================================================
    private void drawSection4_Multitexturing(GL10 gl) {
        gl.glDisable(GL10.GL_DEPTH_TEST);

        // Blending-ul este dezactivat pentru multitexturare de baza;
        // combinarea se face prin texture combiners
        gl.glDisable(GL10.GL_BLEND);

        // Patrat cu multitexturare: hedly.png + splash.png combinate
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, (float) Math.sin(mTransY), -3.0f);
        mSquare1.drawMultiTextured(gl);

        gl.glEnable(GL10.GL_DEPTH_TEST);
    }
}