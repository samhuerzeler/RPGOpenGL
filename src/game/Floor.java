package game;

import game.floor.World;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import util.ShaderLoader;

public class Floor {

    protected float[][] data;
    protected int width;
    protected int height;
    protected Vector3f scale = new Vector3f();
    protected Vector3f translate = new Vector3f();
    protected int heightmapDisplayList;
    protected int lookupTexture;
    protected int shaderProgram;
    protected Texture texture;
    private static ArrayList<Floor> floors = new ArrayList<>();

    public Floor() {
        setUpStates();
        setUpTexture();
    }

    public void render() {
        glPushMatrix();
        {
            //glUseProgram(shaderProgram);
            glCallList(heightmapDisplayList);
        }
        glPopMatrix();
    }

    protected void setMapTranslate(float x, float y, float z) {
        translate.x = x;
        translate.y = y;
        translate.z = z;
    }

    protected void setMapScale(float x, float y, float z) {
        scale.x = x;
        scale.y = y;
        scale.z = z;
    }

    public void add(Floor f) {
        floors.add(f);
    }

    public static ArrayList<Floor> getFloors() {
        return floors;
    }

    public float getHeight(float x, float z) {
        try {
            x = (x - translate.x) / scale.x;
            z = (z - translate.z) / scale.z;
            return data[(int) z][(int) x] * scale.y;
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean inBound(float x, float z) {
        if (x > translate.x
                && x < translate.x + width * scale.x
                && z > translate.z
                && z < translate.z + height * scale.z) {
            return true;
        }
        return false;
    }

    private void setUpStates() {
        glPointSize(1);
    }

    private void setUpShaders() {
        shaderProgram = ShaderLoader.loadShaderPair("res/shaders/landscape.vs", "res/shaders/landscape.fs");
        glUseProgram(shaderProgram);
        glUniform1i(glGetUniformLocation(shaderProgram, "lookup"), 0);
    }

    private void setUpTexture() {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/textures/grass.png"));

        } catch (IOException ex) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cleanUp() {
        glDeleteProgram(shaderProgram);
        glDeleteLists(heightmapDisplayList, 1);
        glBindTexture(GL_TEXTURE_2D, 0);
        glDeleteTextures(lookupTexture);
    }
}
