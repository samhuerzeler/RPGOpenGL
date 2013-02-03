package engine;

import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class Camera {

    public static Camera camera;
    // position
    private float x;
    private float y;
    private float z;
    // rotation
    private float rx;
    private float ry;
    private float rz;
    // field of view
    private float fov;
    // aspect ratio
    private float aspect;
    // flipping planes
    private float near;
    private float far;

    public Camera(float fov, float aspect, float near, float far) {
        x = -150;
        y = -250;
        z = -250;
        rx = 33;
        ry = 20;
        rz = 0;
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        initProjection();
    }

    private void initProjection() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, aspect, near, far);
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_DEPTH_TEST);
    }

    public void useView() {
        glRotatef(rx, 1, 0, 0);
        glRotatef(ry, 0, 1, 0);
        glRotatef(rz, 0, 0, 1);
        glTranslatef(x, y, z);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getRX() {
        return rx;
    }

    public float getRY() {
        return ry;
    }

    public float getRZ() {
        return rz;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setRX(float rx) {
        this.rx = rx;
    }

    public void setRY(float ry) {
        this.ry = ry;
    }

    public void setRZ(float rz) {
        this.rz = rz;
    }
    float speed = 0.01f;

    public void move(float amt, float dir) {
        z += amt * Math.sin(Math.toRadians(ry + 90 * dir));
        x += amt * Math.cos(Math.toRadians(ry + 90 * dir));
    }

    public void rotateY(float amt) {
        ry += amt;
    }

    public void getInput() {

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            move(10.0f, 1);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            move(-10.0f, 1);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            rotateY(-2.0f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            rotateY(2.0f);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
            move(10.0f, 0);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            move(-10.0f, 0);
        }
    }
}
