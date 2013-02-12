package game;

import static org.lwjgl.opengl.GL11.*;

public class World {

    public static World world;
    private float size;
    private float r;
    private float g;
    private float b;

    public World() {
        size = 4000.0f;
        r = 0.1f;
        g = 0.1f;
        b = 0.1f;
    }

    public void render() {
        glColor3f(r, g, b);
        glBegin(GL_QUADS);
        {
            glVertex3f(-size / 2, -16, -size / 2);
            glVertex3f(size / 2, -16, -size / 2);
            glVertex3f(size / 2, -16, size / 2);
            glVertex3f(-size / 2, -16, size / 2);
        }
        glEnd();
    }
}
