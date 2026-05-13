package com.example.lab5textures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Square {

    // ---------------------------------------------------------------
    // VARIABILE INSTANTA
    // ---------------------------------------------------------------

    /** Buffer pentru coordonatele geometrice ale patratului */
    private FloatBuffer mFVertexBuffer;

    /** Buffer pentru culorile celor 4 varfuri */
    private ByteBuffer mColorBuffer;

    /** Buffer pentru indicii fetelor (doua triunghiuri) */
    private ByteBuffer mIndexBuffer;

    /** Buffer pentru coordonatele texturii */
    public FloatBuffer mTextureBuffer;

    /** Array-ul de texturi OpenGL (pastram ID-ul texturii) */
    private int[] textures = new int[1];

    // ---------------------------------------------------------------
    // ASSIGNMENT 1 – Textura de baza (coordonate 0..1)
    // Comenteaza / decommenteaza blocurile de mai jos pentru a testa
    // diferitele scenarii din laborator.
    // ---------------------------------------------------------------

    /**
     * Coordonatele geometrice ale patratului.
     * Sunt 4 varfuri in 2D: (x, y).
     *
     * Varianta initiala: patrat drept.
     * Varianta distorsionata (Assignment 4): dreapta e stramtata.
     */
    float vertices[] = {
            -0.5f, -0.5f,   // colt stanga-jos  (vertex 0)
            0.5f, -0.5f,   // colt dreapta-jos  (vertex 1)
            -0.5f,  0.5f,   // colt stanga-sus   (vertex 2)
            0.5f,  0.5f,   // colt dreapta-sus  (vertex 3)
    };
    /*float vertices[] = {
            -1.0f, -0.7f,
            1.0f, -0.30f,
            -1.0f,  0.70f,
            1.0f,  0.30f,
    };*/

    /*
     * ASSIGNMENT 4 – Distorsionare geometrie (pinch la dreapta):
     * Decommenteaza blocul de mai jos si comenteaza cel de sus.
     *
    float vertices[] = {
            -1.0f, -0.7f,
             1.0f, -0.30f,
            -1.0f,  0.70f,
             1.0f,  0.30f,
    };
    */

    /**
     * Culorile RGBA ale celor 4 varfuri (format byte, 0-255).
     * Daca vrei sa vezi textura curata, comenteaza glColorPointer
     * din draw() sau seteaza alb (255,255,255,255) la toate.
     */
    private byte[] colors = {
            // R    G    B    A
            -128,    0,    0,  -128,   // vertex 0 – rosu
            0, -128,    0,  -128,   // vertex 1 – verde
            0,    0, -128,  -128,   // vertex 2 – albastru
            -128, -128,    0,  -128,   // vertex 3 – galben
    };

    /**
     * Indicii celor doua triunghiuri care formeaza patratul.
     * Triunghi 1: 0,1,2  |  Triunghi 2: 1,3,2
     */
    private byte[] indices = { 0, 1, 2, 1, 3, 2 };

    // ---------------------------------------------------------------
    // ASSIGNMENT 1 – Coordonate textura standard (intreaga textura)
    // ---------------------------------------------------------------
    float[] textureCoords = {
            0.0f, 0.0f,   // vertex 0
            1.0f, 0.0f,   // vertex 1
            0.0f, 1.0f,   // vertex 2
            1.0f, 1.0f,   // vertex 3
    };



     //* ASSIGNMENT 2 – Afisarea doar a coltului stanga-sus (1/4 din textura):
     //* Decommenteaza si comenteaza textureCoords de mai sus.
     //*
   /* float[] textureCoords = {
            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
    };*/


    /*
     * ASSIGNMENT 3 – Tiling (repetare de 2x2 ori):
     * Coordonatele depasesc 1.0, declansand repetarea texturii.
     *
    float[] textureCoords = {
            0.0f, 2.0f,
            2.0f, 2.0f,
            0.0f, 0.0f,
            2.0f, 0.0f,
    };
    */

    // ---------------------------------------------------------------
    // ASSIGNMENT 5 – Animatie: viteza de scroll a texturii
    // ---------------------------------------------------------------
    /** Cat de mult se modifica coordonatele texturii per frame */
    float texIncrease = 0.01f;

    // ---------------------------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------------------------
    public Square() {
        // --- Buffer geometrie ---
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(vertices);
        mFVertexBuffer.position(0);

        // --- Buffer culori ---
        mColorBuffer = ByteBuffer.allocateDirect(colors.length);
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        // --- Buffer indici ---
        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);

        // --- Buffer coordonate textura ---
        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);
    }

    // ---------------------------------------------------------------
    // METODA: createTexture
    // Incarca o imagine din resurse si o trimite catre OpenGL ES.
    // ---------------------------------------------------------------
    public void createTexture(GL10 gl, Context contextRegf, int resource) {
        // 1. Incarcam imaginea ca Bitmap Android
        Bitmap image = BitmapFactory.decodeResource(contextRegf.getResources(), resource);

        // 2. Obtinem un ID unic de textura de la OpenGL
        gl.glGenTextures(1, textures, 0);

        // 3. Activam (bind) textura ca textura 2D curenta
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // 4. Trimitem datele bitmap-ului catre OpenGL
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);

        // 5. Setam filtrele de micsurare si marire
        //    GL_LINEAR = interpolare lina (calitate mai buna)
        //    GL_NEAREST = cel mai apropiat pixel (aspect pixelat)
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        // 6. Eliberam memoria bitmap-ului (nu mai avem nevoie de el)
        image.recycle();
    }

    // ---------------------------------------------------------------
    // METODA: draw
    // Deseneaza patratul texturat in fiecare frame.
    // ---------------------------------------------------------------
    public void draw(GL10 gl) {

        // ---- ASSIGNMENT 5: Animatie scroll textura ----
        // Decommenteaza blocul urmator pentru a vedea animatia.
        // Coordonatele texturii cresc usor la fiecare frame,
        // creand iluzia ca textura se deplaseaza.

        textureCoords[0] += texIncrease;
        textureCoords[1] += texIncrease;
        textureCoords[2] += texIncrease;
        textureCoords[3] += texIncrease;
        textureCoords[4] += texIncrease;
        textureCoords[5] += texIncrease;
        textureCoords[6] += texIncrease;
        textureCoords[7] += texIncrease;

        // Reincarcam bufferul cu noile coordonate
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0);
        

        // --- Geometrie ---
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // --- Culori (comenteaza urmatoarele 2 linii pentru textura curata) ---
        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, mColorBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        // --- Activam texturarea 2D ---
        gl.glEnable(GL10.GL_TEXTURE_2D);

        // --- Blending (amestecarea culorii texturii cu culoarea geometriei) ---
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR);

        // --- Activam textura noastra ---
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // --- Trimitem coordonatele texturii ---
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // --- Desenam cele doua triunghiuri ---
        gl.glDrawElements(
                GL10.GL_TRIANGLES,
                indices.length,
                GL10.GL_UNSIGNED_BYTE,
                mIndexBuffer
        );

        // --- Dezactivam starile pentru urmatorul obiect ---
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glDisable(GL10.GL_BLEND);
    }
}