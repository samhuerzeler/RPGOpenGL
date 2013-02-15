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

    public static World world;
    private float size;
    private float[][] data;
    private int lookupTexture;
    private int heightmapDisplayList;
    private int shaderProgram;

    public World() {
        size = 4000.0f;
        setUpStates();
        setUpHeightMap();
        setUpShaders();
    }

    public void render() {
        glPushMatrix();
        {
            glEnable(GL_CULL_FACE);
            glUseProgram(shaderProgram);
            glCallList(heightmapDisplayList);
        }
        glPopMatrix();
    }

    private void setUpStates() {
        glPointSize(1);
    }

    private void setUpHeightMap() {
        int texWidth = 1;
        int texHeight = 1;
        float texScaleX = 10.0f;
        float texScaleZ = 10.0f;

        try {
            BufferedImage heightmapImage = ImageIO.read(new File("res/images/heightmap.png"));
            data = new float[heightmapImage.getWidth()][heightmapImage.getHeight()];
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
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            heightmapLookupInputStream.close();
            lookupTexture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, lookupTexture);
            texWidth = decoder.getWidth();
            texHeight = decoder.getHeight();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } catch (IOException e) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, e);
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        heightmapDisplayList = glGenLists(1);
        glNewList(heightmapDisplayList, GL_COMPILE);
        glScalef(texScaleX, 3.0f, texScaleZ);
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
