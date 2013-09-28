package engine;

import game.GameObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;

public class OrbitCamera extends Camera {

    public static final float DEFAULT_DISTANCE = 200.0f;
    public static OrbitCamera camera;
    // target to follow
    private GameObject target;
    private InputHandler input = new InputHandler();

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
        rotation.x = target.getRX();
        rotation.y = target.getRY();
        rotation.z = target.getRZ();
        glTranslatef(0.0f, -currentDistance / 16, -currentDistance);
        glRotatef(-rotation.x, 1, 0, 0);
        glRotatef(rotation.y, 0, 1, 0);
        glRotatef(rotation.z, 0, 0, 1);
        glTranslatef(-target.getX(), -target.getY(), -target.getZ());
    }

    void getInput() {
        input.update();
        if (input.keyPressed(Keyboard.KEY_ADD, true) || input.keyPressed(Keyboard.KEY_L, true)) {
            zoomIn();
        }
        if (input.keyPressed(Keyboard.KEY_SUBTRACT, true) || input.keyPressed(Keyboard.KEY_K, true)) {
            zoomOut();
        }
    }
}
