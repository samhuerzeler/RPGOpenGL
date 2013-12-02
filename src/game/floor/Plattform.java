package game.floor;

import game.Floor;
import static org.lwjgl.opengl.GL11.*;

public class Plattform extends Floor {

    public Plattform() {
        setMapTranslate(-250.0f, 0.0f, 0.0f);
        setMapScale(1.0f, 1.0f, 1.0f);
        width = 500;
        height = 500;

        int h = 1050;

        data = new float[width][height];
        for (int z = 0; z < data.length; z++) {
            for (int x = 0; x < data[z].length; x++) {
                data[z][x] = h;
            }
        }

        heightmapDisplayList = glGenLists(2);
        glNewList(heightmapDisplayList, GL_COMPILE);
        glTranslatef(translate.x, translate.y, translate.z);
        glScalef(scale.x, scale.y, scale.z);
        glBegin(GL_POLYGON);
        {
            glVertex3d(0, h, height);
            glVertex3d(width, h, height);
            glVertex3d(width, h, 0);
            glVertex3d(0, h, 0);
        }
        glEnd();
        glEndList();
    }
}
