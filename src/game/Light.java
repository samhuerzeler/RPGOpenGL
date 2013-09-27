package game;

import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL11.*;

public class Light {

    /**
     * Location is transformed by the modelview matrix
     *
     * if 4th parameter = 0 --> x, y, z = direction of the light beam
     *
     * if 4th parameter = 1 --> x, y, z = position of the light source
     */
    private FloatBuffer lightPosition;
    private FloatBuffer matSpecular;
    private FloatBuffer matAmbient;
    private FloatBuffer matDiffuse;
    private FloatBuffer whiteLight;
    private FloatBuffer lModelAmbient;

    public void update() {
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    public void initLightArrays() {
        matSpecular = Util.asFloatBuffer(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        matAmbient = Util.asFloatBuffer(new float[]{0.1f, 0.1f, 0.1f, 0.1f});
        matDiffuse = Util.asFloatBuffer(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        lightPosition = Util.asFloatBuffer(new float[]{100.0f, -500.0f, 100.0f, 1.0f});
        whiteLight = Util.asFloatBuffer(new float[]{2.0f, 2.0f, 4.0f, 1.0f});
        lModelAmbient = Util.asFloatBuffer(new float[]{0.1f, 0.1f, 0.1f, 0.1f});
    }

    public void setUpStates() {
        glShadeModel(GL_SMOOTH);
        glMaterial(GL_FRONT_AND_BACK, GL_SPECULAR, matSpecular);
        glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 50.0f);
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, matDiffuse);
        glLight(GL_LIGHT0, GL_AMBIENT, matAmbient);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, lModelAmbient);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
    }
    
    public void tearDownStates() {
        glDisable(GL_LIGHTING);
        glDisable(GL_LIGHT0);
        glDisable(GL_COLOR_MATERIAL);
    }
}
