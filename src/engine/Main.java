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

    private static final int DISPLAY_WIDTH = 800;
    private static final int DISPLAY_HEIGHT = DISPLAY_WIDTH / 16 * 9;

    public static void main(String[] args) {
        initDisplay();
        initGame();
        initCamera();
        gameLoop();
        cleanUp();
    }

    private static void initGame() {
        World.world = new World();
        Game.game = new Game();
    }

    private static void initCamera() {
        GameObject cameraTarget = Game.game.player;
        OrbitCamera.camera = new OrbitCamera(70, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 5000.0f, cameraTarget);
    }

    private static void getInput() {
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            cleanUp();
            System.exit(0);
        }
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
        World.world.render();
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
        World.world.cleanUp();
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
