package engine;

import game.GameObject;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class Camera {

    public static Camera camera;
    public static final int MOUSEB_LEFT = 0;
    private GameObject target;
    // distance
    private float zDistance = 250.0f;
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

    public Camera(float fov, float aspect, float near, float far, GameObject target) {
        x = 0;
        y = 0;
        z = 0;
        rx = 20;
        ry = 0;//20
        rz = 0;
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        this.target = target;
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

    public void rotateY(float amt) {
        ry += amt;
    }

    public void update() {
        glTranslatef(0.0f, -zDistance / 2, -zDistance);
        glRotatef(-target.getRX(), 1.0f, 0.0f, 0.0f);
        glRotatef(target.getRY(), 0.0f, 1.0f, 0.0f);
        glTranslatef(-target.getX(), -target.getY(), -target.getZ());
    }
}
