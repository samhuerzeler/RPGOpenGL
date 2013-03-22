package engine;

import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import org.lwjgl.util.vector.Vector3f;

public abstract class Camera {

    protected float currentDistance;
    protected float minDistance = 100.0f;
    protected float maxDistance = 2000.0f;
    protected float zoomingSpeed = 25.0f;
    protected int zoomingStep = 80;
    protected Vector3f position = new Vector3f();
    protected Vector3f rotation = new Vector3f();
    // field of view
    protected float fov;
    // aspect ratio
    protected float aspect;
    // flipping planes
    protected float near;
    protected float far;

    protected void rotateY(float amt) {
        rotation.y += amt;
    }

    protected void zoomIn() {
        if (currentDistance > minDistance) {
            currentDistance -= zoomingSpeed;
        }
    }

    protected void zoomIn(int amt) {
        if (currentDistance - amt >= minDistance) {
            currentDistance -= amt;
        }
    }

    protected void zoomOut() {
        if (currentDistance < maxDistance) {
            currentDistance += zoomingSpeed;
        }
    }

    protected void zoomOut(int amt) {
        if (currentDistance + amt <= maxDistance) {
            currentDistance += amt;
        }
    }

    protected void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            zoomOut(zoomingStep);
        } else if (dWheel > 0) {
            zoomIn(zoomingStep);
        }
    }

    public void useView() {
        glRotatef(rotation.x, 1, 0, 0);
        glRotatef(rotation.y, 0, 1, 0);
        glRotatef(rotation.z, 0, 0, 1);
        glTranslatef(position.x, position.y, position.z);
    }

    protected void initProjection() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(fov, aspect, near, far);
        glMatrixMode(GL_MODELVIEW);
        glEnable(GL_DEPTH_TEST);
    }

    protected Vector3f getPosition() {
        return position;
    }

    protected void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    protected Vector3f getRotation() {
        return rotation;
    }

    protected void setRotation(float rx, float ry, float rz) {
        rotation.x = rx;
        rotation.y = ry;
        rotation.z = rz;
    }

    public float getZoomingSpeed() {
        return zoomingSpeed;
    }

    public void setZoomingSpeed(float amt) {
        zoomingSpeed = amt;
    }
}
