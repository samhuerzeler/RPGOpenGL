package engine;

import game.GameObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;

public class OrbitCamera extends Camera {

    public static final float DEFAULT_DISTANCE = 40.0f;
    public static OrbitCamera camera;
    // target to follow
    private GameObject target;

    public OrbitCamera(float fov, float aspect, float near, float far, GameObject target) {
        setPosition(0, 0, 0);
        setRotation(0, 0, 0);
        currentDistance = DEFAULT_DISTANCE;
        this.fov = fov;
        this.aspect = aspect;
        this.near = near;
        this.far = far;
        this.target = target;
        initProjection();
    }

    public void update() {
        if (Mouse.hasWheel()) {
            checkMouseWheel();
        }
        glTranslatef(0.0f, -currentDistance / 16, -currentDistance);
        glRotatef(-target.getRX(), 1.0f, 0.0f, 0.0f);
        glRotatef(target.getRY(), 0.0f, 1.0f, 0.0f);
        glTranslatef(-target.getX(), -target.getY(), -target.getZ());
    }

    void getInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_ADD) || Keyboard.isKeyDown(Keyboard.KEY_L)) {
            zoomIn();
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT) || Keyboard.isKeyDown(Keyboard.KEY_K)) {
            zoomOut();
        }
    }
}
