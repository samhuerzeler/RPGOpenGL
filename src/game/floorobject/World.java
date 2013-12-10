package game.floorobject;

import game.FloorObject;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static org.lwjgl.opengl.GL11.*;

public class World extends FloorObject {

    public World() {
        super();
        setMapTranslate(0.0f, 0.0f, 0.0f);
        setMapScale(48.0f, 48.0f / 6, 48.0f);
        setUpHeightMap();
        //setUpShaders();
    }

    private void setUpHeightMap() {
        try {
            BufferedImage heightmapImage = ImageIO.read(new File("res/images/heightmap_small.png"));
            width = heightmapImage.getWidth();
            height = heightmapImage.getHeight();
            translate.x = -(width * scale.x / 2);
            translate.z = -(height * scale.z / 2);
            data = new float[width][height];
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
        glTranslatef(translate.x, translate.y, translate.z);
        glScalef(scale.x, scale.y, scale.z);
        super.setUpTexture();
        texture.bind();
        for (int z = 0; z < data.length - 1; z++) {
            glBegin(GL_TRIANGLE_STRIP);
            for (int x = 0; x < data[z].length; x++) {
                // texture and vertex coordinates
                glTexCoord2f(x, 1);
                glVertex3f(x, data[z][x], z);
                glTexCoord2f(x, 0);
                glVertex3f(x, data[z + 1][x], z + 1);
                // TODO normal

            }
            glEnd();
        }
        glEndList();
        texture.release();
    }
}
