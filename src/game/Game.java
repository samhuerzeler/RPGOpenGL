package game;

import engine.Physics;
import game.gameobject.statobject.Player;
import game.gameobject.statobject.mob.normal.Guard;
import game.gameobject.statobject.mob.normal.Monkey;
import game.gameobject.statobject.mob.normal.Tiger;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Game {

    // flag ids
    public static final int REMOVE = 0;
    public static Game game;
    public static World world;
    public static Light light;
    private ArrayList<GameObject> objects;
    private ArrayList<GameObject> objectsToRemove;
    public Player player;

    public Game() {
        world = new World();
        light = new Light();
        objects = new ArrayList<GameObject>();
        objectsToRemove = new ArrayList<GameObject>();
        RPGRandom.initRandom();
        player = new Player(0, 200, 100);
        objects.add(player);
        objects.add(new Guard(100, 0, 100, 7));
        objects.add(new Tiger(-100, 0, 0, 2));
        objects.add(new Tiger(100, 0, 0, 1));
        objects.add(new Monkey(0, 0, 0, 5));
    }

    public void update() {
        Iterator it = objects.iterator();
        while (it.hasNext()) {
            GameObject go = (GameObject) it.next();
            if (!go.getFlag(REMOVE)) {
                go.update();
            } else {
                objectsToRemove.add(go);
            }
        }
        it = objectsToRemove.iterator();
        while (it.hasNext()) {
            GameObject go = (GameObject) it.next();
            objects.remove(go);
            go = null;
        }
        objectsToRemove.clear();
    }

    public void render() {
        renderWorld();
        renderGameObjects();
    }

    public void renderWorld() {
        glEnable(GL_CULL_FACE);
        world.render();
    }

    public void renderGameObjects() {
        // render gameobjects
        // use vertexbufferobjects for faster rendering
        glDisable(GL_CULL_FACE);
        glUseProgram(0);
        Iterator it = objects.iterator();
        while (it.hasNext()) {
            GameObject go = (GameObject) it.next();
            if (go.getType() == 1) {
                glColor3f(0.0f, 1.0f, 0.0f);
            } else if (go.getType() == 3) {
                glColor3f(0.3f, 0.3f, 1.0f);
            } else {
                glColor3f(1.0f, 0.0f, 0.0f);
            }
            renderSpawnPoint(go.getSpawnX(), go.getSpawnZ(), 32.0f);
            go.render();
        }
    }

    private void renderSpawnPoint(float cx, float cz, float r) {
        int numSegments = 100;
        float theta = (float) (2 * 3.1415926 / numSegments);
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float t;
        float xx = r;
        float zz = 0;
        glBegin(GL_LINE_LOOP);
        for (int i = 0; i < numSegments; i++) {
            glVertex3f(xx + cx, 0.0f, zz + cz);
            t = xx;
            xx = c * xx - s * zz;
            zz = s * t + c * zz;
        }
        glEnd();
    }

    public void getInput() {
        player.getInput();
    }

    public ArrayList<GameObject> getObjects() {
        return objects;
    }

    public static void cleanUp() {
        world.cleanUp();
    }

    public static ArrayList<GameObject> sphereCollide(float x, float z, float radius) {
        ArrayList<GameObject> res = new ArrayList<GameObject>();
        Iterator it = game.getObjects().iterator();
        while (it.hasNext()) {
            GameObject go = (GameObject) it.next();
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
        Iterator it = game.getObjects().iterator();
        while (it.hasNext()) {
            GameObject go = (GameObject) it.next();
            if (Physics.checkCollision(collider, go) != null) {
                res.add(go);
            }
        }
        return res;
    }
}
