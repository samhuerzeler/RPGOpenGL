package engine;

import game.Game;
import game.GameObject;
import game.Time;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import util.Log;

public class Main {

    private static final int DISPLAY_WIDTH = 1024;
    private static final int DISPLAY_HEIGHT = DISPLAY_WIDTH / 16 * 9;
    private static final DisplayMode DISPLAY_MODE = new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT);
    private static final boolean FULLSCREEN = false;
    private static final boolean LIGHTING = true;
    private static final int FPS = 60;
    private static final float RENDER_DISTANCE = 2000000.0f;
    private static FloatBuffer matSpecular;
    private static FloatBuffer lightPosition;
    private static FloatBuffer whiteLight;
    private static FloatBuffer lModelAmbient;

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/natives/");
        initDisplay();
        if (LIGHTING) {
            initLighting();
        }
        initGame();
        initCamera();
        gameLoop();
        cleanUp();
    }

    private static void initGame() {
        Game.game = new Game();
    }

    private static void initCamera() {
        GameObject cameraTarget = Game.game.player;
        OrbitCamera.camera = new OrbitCamera(70, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, RENDER_DISTANCE, cameraTarget);
    }

    private static void initLighting() {
        initLightArrays();
        glShadeModel(GL_SMOOTH);
        glMaterial(GL_FRONT, GL_SPECULAR, matSpecular);
        glMaterialf(GL_FRONT, GL_SHININESS, 50.0f);
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, lModelAmbient);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_COLOR_MATERIAL);
        glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
    }

    private static void initLightArrays() {
        matSpecular = BufferUtils.createFloatBuffer(4);
        matSpecular.put(1.0f)
                .put(1.0f)
                .put(1.0f)
                .put(20.0f);
        matSpecular.flip();

        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(1.0f)
                .put(1.0f)
                .put(1.0f)
                .put(1.0f);
        lightPosition.flip();

        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(2.0f)
                .put(2.0f)
                .put(4.0f)
                .put(1.0f);
        whiteLight.flip();

        lModelAmbient = BufferUtils.createFloatBuffer(4);
        lModelAmbient.put(0.1f)
                .put(0.1f)
                .put(0.1f)
                .put(0.1f);
        lModelAmbient.flip();
    }

    private static void getInput() {
        // standard / debug input
        if (Keyboard.next()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                cleanUp();
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
                int polygonMode = glGetInteger(GL_POLYGON_MODE);
                if (polygonMode == GL_LINE) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
                } else if (polygonMode == GL_POINT) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                } else if (polygonMode == GL_FILL) {
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                }
            }
        }
        // game (player) and camera input
        Game.game.getInput();
        OrbitCamera.camera.getInput();
    }

    private static void update() {
        Game.game.update();
    }

    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        OrbitCamera.camera.useView();
        OrbitCamera.camera.update();
        // TODO Use VertexBufferObjects for faster rendering
        Game.game.render();
        if (LIGHTING) {
            glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        }
        Display.update();
        Display.sync(FPS);
    }

    private static void gameLoop() {
        Time.init();
        while (!Display.isCloseRequested()) {
            Time.update();
            getInput();
            update();
            render();
        }
    }

    private static void cleanUp() {
        Game.cleanUp();
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
        System.exit(0);
    }

    private static void initDisplay() {
        try {
            if (FULLSCREEN) {
                Display.setFullscreen(true);
            } else {
                Display.setDisplayMode(DISPLAY_MODE);
            }
            Display.create();
            Keyboard.create();
            Mouse.create();
            Display.setVSyncEnabled(true);
            glClearColor(0.1f, 0.1f, 0.2f, 0.0f);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        } catch (LWJGLException ex) {
            Log.err("Could not set up the display!");
            System.exit(1);
        }
    }
}
