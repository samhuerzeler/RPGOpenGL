package game;

import engine.Physics;
import game.gameobject.statobject.Player;
import game.gameobject.statobject.mob.normal.Goblin;
import game.gameobject.statobject.mob.normal.Guard;
import game.gameobject.statobject.mob.normal.Orc;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Game {

    public static Game game;
    private static World world;
    private ArrayList<GameObject> objects;
    private ArrayList<GameObject> objectsToRemove;
    public Player player;

    public Game() {
        world = new World();
        objects = new ArrayList<GameObject>();
        objectsToRemove = new ArrayList<GameObject>();
        player = new Player(-400, 500, 400);
        objects.add(player);
        objects.add(new Guard(0, 0, 0, 1));
        objects.add(new Goblin(1000, 0, -500, 2));
        objects.add(new Goblin(-1000, 0, -500, 1));
        objects.add(new Orc(0, 0, -1000, 5));
    }

    public void update() {
        Iterator it = objects.iterator();
        while (it.hasNext()) {
            GameObject go = (GameObject) it.next();
            if (!go.getRemove()) {
                go.update();
            } else {
                objectsToRemove.add(go);
            }
        }
        it = objectsToRemove.iterator();
        while (it.hasNext()) {
            GameObject go = (GameObject) it.next();
            objects.remove(go);
        }
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
        int numSegments = 50;
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
