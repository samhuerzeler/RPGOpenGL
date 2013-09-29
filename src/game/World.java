package game;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import util.ShaderLoader;

public class World {

    private float[][] data;
    private int lookupTexture;
    private int heightmapDisplayList;
    private int shaderProgram;
    private int mapWidth;
    private int mapHeight;
    private Vector3f mapScale = new Vector3f();
    private Vector3f mapTranslate = new Vector3f();
    Texture texture;

    public World() {
        setMapTranslate(0.0f, 0.0f, 0.0f);
        setMapScale(48.0f, 48.0f / 6, 48.0f);
        setUpStates();
        setUpTexture();
        setUpHeightMap();
        //setUpShaders();
    }

    private void setMapTranslate(float x, float y, float z) {
        mapTranslate.x = x;
        mapTranslate.y = y;
        mapTranslate.z = z;
    }

    private void setMapScale(float x, float y, float z) {
        mapScale.x = x;
        mapScale.y = y;
        mapScale.z = z;
    }

    public void render() {
        glPushMatrix();
        {
            //glUseProgram(shaderProgram);
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
            x = (x - mapTranslate.x) / mapScale.x;
            z = (z - mapTranslate.z) / mapScale.z;
            return data[(int) z][(int) x] * mapScale.y;
        } catch (Exception e) {
            return 0;
        }
    }

    private void setUpHeightMap() {
        try {
            BufferedImage heightmapImage = ImageIO.read(new File("res/images/heightmap.png"));
            mapWidth = heightmapImage.getWidth();
            mapHeight = heightmapImage.getHeight();
            mapTranslate.x = -(mapWidth * mapScale.x / 2);
            mapTranslate.z = -(mapHeight * mapScale.z / 2);
            data = new float[mapWidth][mapHeight];
            Color color;
            for (int z = 0; z < data.length; z++) {
                for (int x = 0; x < data[z].length; x++) {
                    color = new Color(heightmapImage.getRGB(z, x));
                    data[z][x] = color.getRed();
                }
            }
            /*
             * heightmap coloring according to pixel height...
             */
//            FileInputStream heightmapLookupInputStream = new FileInputStream("res/images/heightmap_lookup.png");
//            PNGDecoder decoder = new PNGDecoder(heightmapLookupInputStream);
//            ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
//            decoder.decode(buffer, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
//            buffer.flip();
//            heightmapLookupInputStream.close();
//            lookupTexture = glGenTextures();
//            glBindTexture(GL_TEXTURE_2D, lookupTexture);
//            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        } catch (IOException e) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, e);
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // GL_LINEAR (smooth) OR GL_NEAREST
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        heightmapDisplayList = glGenLists(1);
        glNewList(heightmapDisplayList, GL_COMPILE);
        glTranslatef(mapTranslate.x, mapTranslate.y, mapTranslate.z);
        glScalef(mapScale.x, mapScale.y, mapScale.z);
        texture.bind();
        texture.release();
        for (int z = 0; z < data.length - 1; z++) {
            glBegin(GL_TRIANGLE_STRIP);
            for (int x = 0; x < data[z].length; x++) {
                glTexCoord2f(x, 1);
                glVertex3f(x, data[z][x], z);
                glTexCoord2f(x, 0);
                glVertex3f(x, data[z + 1][x], z + 1);
            }
            glEnd();
        }
        glEndList();
    }

    private void setUpTexture() {
        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/textures/grass.png"));

        } catch (IOException ex) {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
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
