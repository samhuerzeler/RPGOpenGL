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

    public World() {
        setUpStates();
        setUpHeightMap();
        //setUpShaders();
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

    private void setUpHeightMap() {
        int mapWidth = 1;
        int mapHeight = 1;
        int mapScaleX = 10;
        int mapScaleY = 3;
        int mapScaleZ = 10;

        try {
            BufferedImage heightmapImage = ImageIO.read(new File("res/images/heightmap.png"));
            mapWidth = heightmapImage.getWidth();
            mapHeight = heightmapImage.getHeight();
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
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // LINEAR (smooth) OR NEAREST
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        heightmapDisplayList = glGenLists(1);
        glNewList(heightmapDisplayList, GL_COMPILE);
        glScalef(mapScaleX, mapScaleY, mapScaleZ);
        //glTranslatef(-mapWidth / 2, -0.0f, -mapHeight / 2);
        glTranslatef(200.0f, -100.0f, -200.0f);
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
