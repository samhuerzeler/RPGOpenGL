package game;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class Sprite {

    // texture (slick)
    private Texture texture; // TODO render texture
    // color
    private float r;
    private float g;
    private float b;
    // size
    private Vector3f size = new Vector3f();

    public Sprite(float r, float g, float b, float sx, float sy, float sz) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.size.x = sx;
        this.size.y = sy;
        this.size.z = sz;
    }

    public void render() {
        glColor3f(r, g, b);
        glBegin(GL_QUADS);
        {
            // back
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(-size.x / 2, -size.y / 2, size.z / 2);
            glVertex3f(-size.x / 2, size.y / 2, size.z / 2);
            glVertex3f(size.x / 2, size.y / 2, size.z / 2);
            glVertex3f(size.x / 2, -size.y / 2, size.z / 2);
            // front
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(-size.x / 2, -size.y / 2, -size.z / 2);
            glVertex3f(-size.x / 2, size.y / 2, -size.z / 2);
            glVertex3f(size.x / 2, size.y / 2, -size.z / 2);
            glVertex3f(size.x / 2, -size.y / 2, -size.z / 2);
            // left
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(-size.x / 2, -size.y / 2, -size.z / 2);
            glVertex3f(-size.x / 2, -size.y / 2, size.z / 2);
            glVertex3f(-size.x / 2, size.y / 2, size.z / 2);
            glVertex3f(-size.x / 2, size.y / 2, -size.z / 2);
            // right
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(size.x / 2, -size.y / 2, -size.z / 2);
            glVertex3f(size.x / 2, -size.y / 2, size.z / 2);
            glVertex3f(size.x / 2, size.y / 2, size.z / 2);
            glVertex3f(size.x / 2, size.y / 2, -size.z / 2);
            // top
            glColor3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-size.x / 2, size.y / 2, -size.z / 2);
            glVertex3f(size.x / 2, size.y / 2, -size.z / 2);
            glVertex3f(size.x / 2, size.y / 2, size.z / 2);
            glVertex3f(-size.x / 2, size.y / 2, size.z / 2);
            // bottom
            glColor3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-size.x / 2, -size.y / 2, -size.z / 2);
            glVertex3f(size.x / 2, -size.y / 2, -size.z / 2);
            glVertex3f(size.x / 2, -size.y / 2, size.z / 2);
            glVertex3f(-size.x / 2, -size.y / 2, size.z / 2);
        }
        glEnd();
    }

    public float getSX() {
        return size.x;
    }

    public float getSY() {
        return size.y;
    }

    public float getSZ() {
        return size.z;
    }

    public void setSX(float sx) {
        this.size.x = sx;
    }

    public void setSY(float sy) {
        this.size.y = sy;
    }

    public void setSZ(float sz) {
        this.size.z = sz;
    }
}
