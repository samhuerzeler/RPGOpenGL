package engine;

import game.Game;
import game.Time;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;

public class Main {

    public static void main(String[] args) {
        initDisplay();
        initCamera();
        initGame();
        gameLoop();
        cleanUp();
    }

    private static void initGame() {
        Game.game = new Game();
    }

    private static void initCamera() {
        Camera.camera = new Camera(70, (float) Display.getWidth() / (float) Display.getHeight(), 0.3f, 5000f);
    }

    private static void getInput() {
        Game.game.getInput();
        Camera.camera.getInput();
    }

    private static void update() {
        Game.game.update();
    }

    private static void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();
        Camera.camera.useView();
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
        Display.destroy();
        Keyboard.destroy();
    }

    private static void initDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
            Keyboard.create();
            Display.setVSyncEnabled(true);
        } catch (LWJGLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
