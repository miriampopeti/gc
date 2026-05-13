package com.example.lab6blending;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Square {

    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mColorBuffer;
    private ByteBuffer  mIndexBuffer;
    private FloatBuffer mTextureCoords;

    private int mTexture0;
    private int mTexture1;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------
    public Square() {
        // Vertices (x, y) pentru un patrat centrat in origine
        float vertices[] = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f,  1.0f,
                1.0f,  1.0f
        };

        // Culori RGBA pentru fiecare vertex – Yellow, Cyan, Black, Magenta
        float squareColorsYMCA[] = {
                1.0f, 1.0f, 0.0f, 1.0f,   // galben
                0.0f, 1.0f, 1.0f, 1.0f,   // cyan
                0.0f, 0.0f, 0.0f, 1.0f,   // negru
                1.0f, 0.0f, 1.0f, 1.0f    // magenta
        };

        // Coordonate textura
        float textureCoords[] = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };

        // Indici pentru doua triunghiuri care formeaza patratul
        byte indices[] = { 0, 1, 2, 1, 3, 2 };

        // --- Vertex buffer ---
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(vertices);
        mFVertexBuffer.position(0);

        // --- Color buffer ---
        ByteBuffer cbb = ByteBuffer.allocateDirect(squareColorsYMCA.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(squareColorsYMCA);
        mColorBuffer.position(0);

        // --- Texture coords buffer ---
        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureCoords = tbb.asFloatBuffer();
        mTextureCoords.put(textureCoords);
        mTextureCoords.position(0);

        // --- Index buffer ---
        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }

    // -----------------------------------------------------------------------
    // Incarca doua texturi pentru multitexturare
    // -----------------------------------------------------------------------
    public void setTextures(GL10 gl, Context context, int resourceID0, int resourceID1) {
        mTexture0 = createTexture(gl, context, resourceID0);
        mTexture1 = createTexture(gl, context, resourceID1);
    }

    // -----------------------------------------------------------------------
    // Creaza o textura OpenGL dintr-un resource drawable
    // -----------------------------------------------------------------------
    private int createTexture(GL10 gl, Context context, int resourceID) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resourceID);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();

        return textures[0];
    }

    // -----------------------------------------------------------------------
    // Draw – varianta simpla fara textura (folosita in sectiunea Alpha Blending)
    // -----------------------------------------------------------------------
    public void draw(GL10 gl) {
        gl.glFrontFace(GL11.GL_CW);

        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glFrontFace(GL11.GL_CCW);
    }

    // -----------------------------------------------------------------------
    // Draw – cu culori per vertex
    // -----------------------------------------------------------------------
    public void drawColored(GL10 gl) {
        gl.glFrontFace(GL11.GL_CW);

        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glColorPointer(4, GL11.GL_FLOAT, 0, mColorBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glFrontFace(GL11.GL_CCW);
    }

    // -----------------------------------------------------------------------
    // Draw – cu o singura textura + culoare globala glColor4f
    // -----------------------------------------------------------------------
    public void drawTextured(GL10 gl) {
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture0);

        gl.glFrontFace(GL11.GL_CW);

        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureCoords);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glFrontFace(GL11.GL_CCW);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }

    // -----------------------------------------------------------------------
    // Draw – multitexturare
    // -----------------------------------------------------------------------
    public void drawMultiTextured(GL10 gl) {
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture0);

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glFrontFace(GL11.GL_CW);

        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glColorPointer(4, GL11.GL_FLOAT, 0, mColorBuffer);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        // Seteaza coordonate textura pentru ambele unitati
        gl.glClientActiveTexture(GL10.GL_TEXTURE0);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureCoords);

        gl.glClientActiveTexture(GL10.GL_TEXTURE1);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureCoords);

        // Apeleaza metoda de multitexturare
        multiTexture(gl, mTexture0, mTexture1);

        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glFrontFace(GL11.GL_CCW);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }

    // -----------------------------------------------------------------------
    // Multitexturare – combina doua texturi
    // Incearca GL_MODULATE, GL_ADD, GL_BLEND, GL_DECAL
    // -----------------------------------------------------------------------
    private void multiTexture(GL10 gl, int tex0, int tex1) {
        // Alege modul de combinare – modifica aceasta valoare pentru experimente:
        // GL10.GL_MODULATE  → inmulteste cele doua texturi
        // GL10.GL_ADD       → aduna cele doua texturi
        // GL10.GL_BLEND     → amesteca
        // GL10.GL_DECAL     → aplica textura ca un decal
        float combineParameter = GL10.GL_ADD;

        // Activeaza si leaga prima textura la unitatea 0
        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tex0);

        // Activeaza si leaga a doua textura la unitatea 1
        gl.glActiveTexture(GL10.GL_TEXTURE1);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, tex1);

        // Seteaza modul de combinare pentru unitatea 1
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, combineParameter);
    }
}