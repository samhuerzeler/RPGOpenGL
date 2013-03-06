package game;

import de.matthiasmann.twl.utils.PNGDecoder;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import util.ShaderLoader;

public class World {

    private float[][] data;
    private int lookupTexture;
    private int heightmapDisplayList;
    private int shaderProgram;
    private int mapWidth;
    private int mapHeight;
    private int mapScaleX = 24;
    private int mapScaleY = mapScaleX / 8;
    private int mapScaleZ = mapScaleX;
    private float mapTranslateX = 0.0f;
    private float mapTranslateY = 0.0f;
    private float mapTranslateZ = 0.0f;

    public World() {
        setUpStates();
        setUpHeightMap();
        setUpShaders();
    }

    public void render() {
        glPushMatrix();
        {
            glUseProgram(shaderProgram);
            glCallList(heightmapDisplayList);
        }
        glPopMatrix();
    }

    private void setUpStates() {
        glPointSize(1);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public float getHeight(float x, float z) {
        try {
            x -= mapTranslateX;
            z -= mapTranslateZ;
            x /= mapScaleX;
            z /= mapScaleZ;
            return data[(int) z][(int) x] * mapScaleY;
        } catch (Exception e) {
            return 0;
        }
    }

    private void setUpHeightMap() {

        try {
            BufferedImage heightmapImage = ImageIO.read(new File("res/images/heightmap.png"));
            mapWidth = heightmapImage.getWidth();
            mapHeight = heightmapImage.getHeight();
            mapTranslateX = -(mapWidth * mapScaleX / 2);
            mapTranslateZ = -(mapHeight * mapScaleZ / 2);
            data = new float[mapWidth][mapHeight];
            Color color;
            for (int z = 0; z < data.length; z++) {
                for (int x = 0; x < data[z].length; x++) {
                    color = new Color(heightmapImage.getRGB(z, x));
                    data[z][x] = color.getRed();
                }
            }
            FileInputStream heightmapLookupInputStream = new FileInputStream("res/images/heightmap_lookup.png");
            PNGDecoder decoder = new PNGDecoder(heightmapLookupInputStream);
            ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
            buffer.flip();
            heightmapLookupInputStream.close();
            lookupTexture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, lookupTexture);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } catch (IOException e) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, e);
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // GL_LINEAR (smooth) OR GL_NEAREST
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        heightmapDisplayList = glGenLists(1);
        glNewList(heightmapDisplayList, GL_COMPILE);
        glTranslatef(mapTranslateX, mapTranslateY, mapTranslateZ);
        glScalef(mapScaleX, mapScaleY, mapScaleZ);
        for (int z = 0; z < data.length - 1; z++) {
            glBegin(GL_TRIANGLE_STRIP);
            for (int x = 0; x < data[z].length; x++) {
                glVertex3f(x, data[z][x], z);
                glVertex3f(x, data[z + 1][x], z + 1);
            }
            glEnd();
        }
        glEndList();
    }

    private void setUpShaders() {
        shaderProgram = ShaderLoader.loadShaderPair("res/shaders/landscape.vs", "res/shaders/landscape.fs");
        glUseProgram(shaderProgram);
        glUniform1i(glGetUniformLocation(shaderProgram, "lookup"), 0);
    }

    public void cleanUp() {
        glDeleteProgram(shaderProgram);
        glDeleteLists(heightmapDisplayList, 1);
        glBindTexture(GL_TEXTURE_2D, 0);
        glDeleteTextures(lookupTexture);
    }
}
