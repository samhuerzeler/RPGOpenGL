package engine;

import game.Game;
import game.GameObject;
import game.Time;
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
    private static final int FPS = 60;
    private static final float RENDER_DISTANCE = 2000000.0f;
    private static final boolean FULLSCREEN = false;
    private static boolean LIGHTING = true;

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/natives/");
        initDisplay();
        initGame();
        if (LIGHTING) {
            initLighting();
        }
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
        Game.light.initLightArrays();
        Game.light.setUpStates();
    }

    private static void getInput() {
        // standard / debug input
        if (Keyboard.next()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
                LIGHTING = false;
                Game.light.tearDownStates();
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
                LIGHTING = true;
                Game.light.setUpStates();
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                cleanUp();
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_F5)) {
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
            Game.light.update();
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
