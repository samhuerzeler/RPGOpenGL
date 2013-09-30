package game;

import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.*;

public class Light {

    /**
     * light location is transformed by the modelview matrix
     *
     * if 4th parameter = 0 --> x, y, z = direction of the light beam
     *
     * if 4th parameter = 1 --> x, y, z = position of the light source
     */
    private FloatBuffer whiteLight;
    private FloatBuffer yellowLight;
    private FloatBuffer lModelAmbient;
    private FloatBuffer matSpecular;
    private FloatBuffer torchLightPosition;
    private FloatBuffer torchAmbient;
    private FloatBuffer torchDiffuse;
    private FloatBuffer sunLightPosition;
    private FloatBuffer sunAmbient;
    private FloatBuffer sunDiffuse;

    public void update() {
        glPushMatrix();
        /* 
         * LIGHT0: torch
         * LIGHT1: sun
         */
        torchLightPosition = Util.asFloatBuffer(new float[]{Game.player.getX(), Game.player.getY(), Game.player.getZ(), 1.0f});
        glLight(GL_LIGHT0, GL_POSITION, torchLightPosition);
        glLight(GL_LIGHT1, GL_POSITION, sunLightPosition);
        glPopMatrix();
    }

    public void initLightArrays() {
        whiteLight = Util.asFloatBuffer(new float[]{0.4f, 0.4f, 0.4f, 1.0f});
        yellowLight = Util.asFloatBuffer(new float[]{1.0f, 1.0f, 0.0f, 1.0f});
        lModelAmbient = Util.asFloatBuffer(new float[]{0.2f, 0.2f, 0.2f, 0.1f});
        matSpecular = Util.asFloatBuffer(new float[]{1.0f, 1.0f, 1.0f, 1.0f});

        // torch
        torchLightPosition = Util.asFloatBuffer(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        torchAmbient = Util.asFloatBuffer(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        torchDiffuse = Util.asFloatBuffer(new float[]{0.9f, 0.6f, 0.2f, 1.0f});
        // sun
        sunLightPosition = Util.asFloatBuffer(new float[]{0.0f, 1.0f, 0.5f, 0.0f});
        sunAmbient = Util.asFloatBuffer(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        sunDiffuse = Util.asFloatBuffer(new float[]{0.0f, 1.0f, 1.0f, 1.0f});
    }

    public void setUpStates() {
        glShadeModel(GL_SMOOTH);
        glMaterial(GL_FRONT_AND_BACK, GL_SPECULAR, matSpecular);
        glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 50.0f);
        glLight(GL_LIGHT0, GL_POSITION, torchLightPosition);
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, torchDiffuse);
        glLight(GL_LIGHT0, GL_AMBIENT, torchAmbient);
        glLight(GL_LIGHT1, GL_POSITION, sunLightPosition);
        glLight(GL_LIGHT1, GL_SPECULAR, yellowLight);
        glLight(GL_LIGHT1, GL_DIFFUSE, sunDiffuse);
        glLight(GL_LIGHT1, GL_AMBIENT, sunAmbient);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, lModelAmbient);
        glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 45.0f);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_LIGHT1);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
    }

    public void tearDownStates() {
        glDisable(GL_LIGHTING);
        glDisable(GL_LIGHT0);
        glDisable(GL_LIGHT1);
        glDisable(GL_COLOR_MATERIAL);
    }
}
