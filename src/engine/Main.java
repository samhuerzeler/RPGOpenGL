package engine;

import game.Game;
import game.GameObject;
import game.Time;
import game.World;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    private static final int DISPLAY_WIDTH = 600;
    private static final int DISPLAY_HEIGHT = DISPLAY_WIDTH / 16 * 9;
    private static final float RENDER_DISTANCE = 10000.0f;

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", System.getProperty("user.dir") + "/natives/");
        initDisplay();
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

    private static void getInput() {
        // standard / debug input
        if (Keyboard.next()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                cleanUp();
                System.exit(0);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_P)) {
                int polygonMode = glGetInteger(GL_POLYGON_MODE);
                if (polygonMode == GL_LINE) {
                    glPolygonMode(GL_FRONT, GL_POINT);
                } else if (polygonMode == GL_POINT) {
                    glPolygonMode(GL_FRONT, GL_FILL);
                } else if (polygonMode == GL_FILL) {
                    glPolygonMode(GL_FRONT, GL_LINE);
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
        Display.update();
        Display.sync(60);
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
    }

    private static void initDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
            //Display.setFullscreen(true);
            Display.create();
            Keyboard.create();
            Mouse.create();
            Display.setVSyncEnabled(true);
        } catch (LWJGLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
