package game;

import game.floorobject.World;
import engine.Physics;
import game.floorobject.Sky;
import game.floorobject.Square;
import game.floorobject.VoidFloor;
import game.gameobject.WorldObject;
import game.gameobject.statobject.Player;
import game.gameobject.statobject.mob.normal.Guard;
import game.gameobject.statobject.mob.normal.Monkey;
import game.gameobject.statobject.mob.normal.Tiger;
import game.gameobject.statobject.player.Warrior;
import java.awt.Rectangle;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL20.*;

public class Game {

    // flag ids
    public static final int REMOVE = 0;
    public static Game game;
    public static Player player;
    public static World world;
    public static Sky sky;
    public static VoidFloor voidFloor;
    public static Square plattform;
    public static Square plattform2;
    public static Light light;
    public FloorObject floors = new FloorObject();
    private ArrayList<WorldObject> worldObjects;
    private ArrayList<GameObject> gameObjects;
    private ArrayList<GameObject> gameObjectsToRemove;
    private Renderer renderer = new Renderer();

    public Game() {
        RPGRandom.initRandom();

        light = new Light();

        // init floors
        world = new World();
        sky = new Sky();
        voidFloor = new VoidFloor();
        plattform = new Square(0, 1050, 0);
        plattform2 = new Square(-200, 1100, 0);

        // add floors to floor list
        floors.add(world);
        floors.add(sky);
        floors.add(voidFloor);
        floors.add(plattform);
        floors.add(plattform2);

        // init object lists
        gameObjects = new ArrayList<>();
        gameObjectsToRemove = new ArrayList<>();

        // init game objects
        player = new Warrior(0, 0, 0);
        Monkey monkey = new Monkey(0, 0, -300, 20);
        Tiger tiger = new Tiger(300, 0, -300, 2);
        Guard guard = new Guard(300, 0, 0, 20);

        // add game objects to objects list
        gameObjects.add(player);
        gameObjects.add(monkey);
        gameObjects.add(tiger);
        gameObjects.add(guard);
    }

    public void update() {
        // add objects to remove to remove list
        for (GameObject go : gameObjects) {
            if (!go.getFlag(REMOVE)) {
                go.update();
            } else {
                gameObjectsToRemove.add(go);
            }
        }
        // remove objects to remove
        for (GameObject go : gameObjectsToRemove) {
            gameObjects.remove(go);
            go = null;
        }
        // clear remove list
        gameObjectsToRemove.clear();
    }

    public void render() {
        glUseProgram(0);
        renderer.renderWorld(worldObjects);
        renderer.renderObjects(gameObjects);
        renderer.renderText(gameObjects);
        renderer.renderHud();
    }

    public void getInput() {
        player.getInput();
    }

    public ArrayList<GameObject> getObjects() {
        return gameObjects;
    }

    public static void cleanUp() {
        world.cleanUp();
    }

    public static ArrayList<GameObject> sphereCollide(float x, float z, float radius) {
        ArrayList<GameObject> res = new ArrayList<>();
        for (GameObject go : Game.game.getObjects()) {
            if (Util.dist(go.getX(), go.getZ(), x, z) < radius) {
                res.add(go);
            }
        }
        return res;
    }

    public static ArrayList<GameObject> rectangleCollide(float x1, float z1, float x2, float z2) {
        ArrayList<GameObject> res = new ArrayList<>();
        float sx = x2 - x1;
        float sz = z2 - z1;
        Rectangle collider = new Rectangle((int) x1, (int) z1, (int) sx, (int) sz);
        for (GameObject go : game.getObjects()) {
            if (Physics.checkCollision(collider, go) != null) {
                res.add(go);
            }
        }
        return res;
    }
}
