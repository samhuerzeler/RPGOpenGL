package game;

import engine.Physics;
import game.gameobject.statobject.Player;
import game.gameobject.statobject.mob.normal.Goblin;
import game.gameobject.statobject.mob.normal.Guard;
import game.gameobject.statobject.mob.normal.Orc;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Game {

    public static Game game;
    private ArrayList<GameObject> objects;
    private ArrayList<GameObject> objectsToRemove;
    public Player player;

    public Game() {
        objects = new ArrayList<GameObject>();
        objectsToRemove = new ArrayList<GameObject>();
        player = new Player(0, 0, 0);
        objects.add(player);
        objects.add(new Guard(0, 0, 0, 1));
        objects.add(new Goblin(1500, 0, -1000, 2));
        objects.add(new Goblin(-1500, 0, -1000, 1));
        objects.add(new Orc(0, 0, -1000, 5));
    }

    public void update() {
        for (GameObject go : objects) {
            if (!go.getRemove()) {
                go.update();
            } else {
                objectsToRemove.add(go);
            }
        }
        for (GameObject go : objectsToRemove) {
            objects.remove(go);
        }
    }

    public void render() {
        for (GameObject go : objects) {
            go.render();
        }
    }

    public void getInput() {
        player.getInput();
    }

    public ArrayList<GameObject> getObjects() {
        return objects;
    }

    public static ArrayList<GameObject> sphereCollide(float x, float z, float radius) {
        ArrayList<GameObject> res = new ArrayList<GameObject>();
        for (GameObject go : game.getObjects()) {
            if (Util.dist(go.getX(), go.getZ(), x, z) < radius) {
                res.add(go);
            }
        }
        return res;
    }

    public static ArrayList<GameObject> rectangleCollide(float x1, float z1, float x2, float z2) {
        ArrayList<GameObject> res = new ArrayList<GameObject>();
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
