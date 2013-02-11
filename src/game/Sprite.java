package game;

import static org.lwjgl.opengl.GL11.*;

public class Sprite {

    private float r;
    private float g;
    private float b;
    private float sx;
    private float sy;
    private float sz;

    public Sprite(float r, float g, float b, float sx, float sy, float sz) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
    }

    public void render() {
        glColor3f(r, g, b);
        glBegin(GL_QUADS);
        {
            // back
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(-sx / 2, -sy / 2, sz / 2);
            glVertex3f(-sx / 2, sy / 2, sz / 2);
            glVertex3f(sx / 2, sy / 2, sz / 2);
            glVertex3f(sx / 2, -sy / 2, sz / 2);
            // front
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(-sx / 2, -sy / 2, -sz / 2);
            glVertex3f(-sx / 2, sy / 2, -sz / 2);
            glVertex3f(sx / 2, sy / 2, -sz / 2);
            glVertex3f(sx / 2, -sy / 2, -sz / 2);
            // left
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(-sx / 2, -sy / 2, -sz / 2);
            glVertex3f(-sx / 2, -sy / 2, sz / 2);
            glVertex3f(-sx / 2, sy / 2, sz / 2);
            glVertex3f(-sx / 2, sy / 2, -sz / 2);
            // right
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(sx / 2, -sy / 2, -sz / 2);
            glVertex3f(sx / 2, -sy / 2, sz / 2);
            glVertex3f(sx / 2, sy / 2, sz / 2);
            glVertex3f(sx / 2, sy / 2, -sz / 2);
            // top
            glColor3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-sx / 2, sy / 2, -sz / 2);
            glVertex3f(sx / 2, sy / 2, -sz / 2);
            glVertex3f(sx / 2, sy / 2, sz / 2);
            glVertex3f(-sx / 2, sy / 2, sz / 2);
            // bottom
            glColor3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-sx / 2, -sy / 2, -sz / 2);
            glVertex3f(sx / 2, -sy / 2, -sz / 2);
            glVertex3f(sx / 2, -sy / 2, sz / 2);
            glVertex3f(-sx / 2, -sy / 2, sz / 2);
        }
        glEnd();
    }

    public float getSX() {
        return sx;
    }

    public float getSY() {
        return sy;
    }

    public float getSZ() {
        return sz;
    }

    public void setSX(float sx) {
        this.sx = sx;
    }

    public void setSY(float sy) {
        this.sy = sy;
    }

    public void setSZ(float sz) {
        this.sz = sz;
    }
}
