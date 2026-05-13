package com.example.lab4lights;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLSurfaceView;

public class BouncyCubeRenderer implements GLSurfaceView.Renderer {

    public final static int SS_SUNLIGHT = GL10.GL_LIGHT0;

    private Cube mCube;
    private float mAngle = 0f;

    // Bounce
    private float mY        = 0f;
    private float mVelocity = 0.05f;
    private final float GRAVITY = -0.003f;
    private final float FLOOR   = -3.0f;
    private final float CEILING =  3.0f;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        mCube = new Cube();
        initLighting(gl);
    }

    private void initLighting(GL10 gl) {

        // Pozitia luminii
        float[] position = { 10.0f, 0.0f, 3.0f, 1.0f };
        gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(position));

        // --- DIFFUSE: lumina alba, material verde ---
        float[] diffuseLight     = { 1.0f, 1.0f, 1.0f, 1.0f }; // alba
        float[] diffuseMaterial  = { 0.0f, 1.0f, 0.0f, 1.0f }; // verde
        gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(diffuseLight));
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(diffuseMaterial));

        // --- SPECULAR ---
        float[] specularLight    = { 1.0f, 1.0f, 1.0f, 1.0f };
        float[] specularMaterial = { 1.0f, 1.0f, 1.0f, 1.0f };
        gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(specularLight));
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(specularMaterial));
        // Shininess: 5-10 = plastic, >25 = metal, max 128
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25.0f);

        // --- AMBIENT ---
        float[] ambientLight    = { 0.2f, 0.2f, 0.2f, 1.0f };
        float[] ambientMaterial = { 0.2f, 0.2f, 0.2f, 1.0f };
        gl.glLightfv(SS_SUNLIGHT, GL10.GL_AMBIENT, makeFloatBuffer(ambientLight));
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, makeFloatBuffer(ambientMaterial));

        // World ambient
        float[] worldAmbient = { 0.2f, 0.2f, 0.2f, 1.0f };
        gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, makeFloatBuffer(worldAmbient));
        gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);

        // --- EMISSIVE: cubul are o stralucire galbena proprie ---
        float[] emissive = { 0.1f, 0.1f, 0.0f, 1.0f }; // galben slab
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, makeFloatBuffer(emissive));

        // --- ATTENUATION: linear falloff ---
        gl.glLightf(SS_SUNLIGHT, GL10.GL_CONSTANT_ATTENUATION,  1.0f);
        gl.glLightf(SS_SUNLIGHT, GL10.GL_LINEAR_ATTENUATION,    0.025f);
        gl.glLightf(SS_SUNLIGHT, GL10.GL_QUADRATIC_ATTENUATION, 0.0f);

        // --- SPOTLIGHT ---
        float[] spotDir = { 0.0f, 0.0f, -1.0f };
        gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPOT_DIRECTION, makeFloatBuffer(spotDir));
        gl.glLightf(SS_SUNLIGHT, GL10.GL_SPOT_CUTOFF,   45.0f);
        gl.glLightf(SS_SUNLIGHT, GL10.GL_SPOT_EXPONENT,  8.0f);

        // --- SHADING ---
        gl.glShadeModel(GL10.GL_SMOOTH);

        // --- Activeaza lighting ---
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(SS_SUNLIGHT);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -8.0f);

        // Bounce pe Y
        mY        += mVelocity;
        mVelocity += GRAVITY;
        if (mY <= FLOOR)   { mY = FLOOR;   mVelocity = -mVelocity * 0.85f; }
        if (mY >= CEILING) { mY = CEILING; mVelocity = -mVelocity * 0.85f; }
        gl.glTranslatef(0.0f, mY, 0.0f);

        // Rotatie continua
        mAngle += 1.2f;
        gl.glRotatef(mAngle,       1.0f, 0.0f, 0.0f);
        gl.glRotatef(mAngle * 0.7f, 0.0f, 1.0f, 0.0f);

        mCube.draw(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;
        float ratio = (float) width / height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 25);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    protected static FloatBuffer makeFloatBuffer(float[] array) {
        ByteBuffer bb = ByteBuffer.allocateDirect(array.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(array);
        fb.position(0);
        return fb;
    }
}