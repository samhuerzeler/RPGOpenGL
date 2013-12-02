package game.floor;

import game.Floor;
import static org.lwjgl.opengl.GL11.*;

public class Square extends Floor {

    /**
     * for testing purposes (multiple floors & ceiling collision)
     */
    public Square(float x, float y, float z, int width, int height) {
        this.width = width;
        this.height = height;

        setMapTranslate(x, y, z);
        setMapScale(1.0f, 1.0f, 1.0f);

        data = new float[width][height];
        for (int zz = 0; zz < data.length; zz++) {
            for (int xx = 0; xx < data[zz].length; xx++) {
                data[zz][xx] = y;
            }
        }
        heightmapDisplayList = glGenLists(2);
        glNewList(heightmapDisplayList, GL_COMPILE);
        glPushMatrix();
        glTranslatef(translate.x, translate.y, translate.z);
        glScalef(scale.x, scale.y, scale.z);
        glBegin(GL_POLYGON);
        {
            glVertex3d(0, 0, height);
            glVertex3d(width, 0, height);
            glVertex3d(width, 0, 0);
            glVertex3d(0, 0, 0);
        }
        glEnd();
        glEndList();
        glPopMatrix();
    }

    public Square(float x, float y, float z) {
        this(x, y, z, 500, 500);
    }

    public Square() {
        this(0, 0, 0);
    }
}
