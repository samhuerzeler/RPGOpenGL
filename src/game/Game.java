package game;

import engine.FontHandler;
import engine.OrbitCamera;
import engine.Physics;
import game.gameobject.StatObject;
import game.gameobject.statobject.Player;
import game.gameobject.statobject.mob.normal.Guard;
import game.gameobject.statobject.mob.normal.Monkey;
import game.gameobject.statobject.mob.normal.Tiger;
import game.gameobject.statobject.player.Warrior;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

public class Game {

    // flag ids
    public static final int REMOVE = 0;
    public static Game game;
    public static World world;
    public static Light light;
    private Map<GameObject, Vector3f> objects;
    private ArrayList<GameObject> objectsToRemove;
    private FontHandler fontHandler = new FontHandler(20);
    public Player player;

    public Game() {
        world = new World();
        light = new Light();
        objects = new HashMap<GameObject, Vector3f>();
        objectsToRemove = new ArrayList<GameObject>();
        RPGRandom.initRandom();

        player = new Warrior(0, 0, 0);
        Monkey monkey = new Monkey(0, 0, -300, 20);
        Tiger tiger = new Tiger(300, 0, -300, 2);
        Guard guard = new Guard(300, 0, 0, 20);

        objects.put(player, player.position);
        objects.put(monkey, monkey.position);
        objects.put(tiger, tiger.position);
        objects.put(guard, guard.position);
    }

    public void update() {
        for (GameObject go : objects.keySet()) {
            if (!go.getFlag(REMOVE)) {
                go.update();
            } else {
                objectsToRemove.add(go);
            }
        }
        for (GameObject go : objectsToRemove) {
            objects.remove(go);
            go = null;
        }
        objectsToRemove.clear();
    }

    public void render() {
        renderWorld();
        renderGameObjects();
        renderText();
        renderHud();
    }

    private void renderWorld() {
        glEnable(GL_CULL_FACE);
        world.render();
        glDisable(GL_CULL_FACE);
    }

    private void renderGameObjects() {
        // render gameobjects
        // use vertexbufferobjects for faster rendering
        glUseProgram(0);
        for (GameObject go : objects.keySet()) {
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

    private void renderText() {
        for (Map.Entry<GameObject, Vector3f> map : objects.entrySet()) {
            glPushMatrix();
            {
                GameObject go = map.getKey();
                Vector3f position = map.getValue();
                if (go.type != StatObject.PLAYER) {
                    glTranslatef(position.x, position.y + 60, position.z);
                    Vector3f cameraRotation = OrbitCamera.camera.getRotation();
                    glRotatef(-cameraRotation.y + 180, 0, 1, 0);
                    glRotatef(-cameraRotation.x, 1, 0, 0);
                    glScalef(-1, -1, 1);
                    fontHandler.drawString(0, 0, go.name, Color.orange);
                }
            }
            glPopMatrix();
        }
    }

    private void renderHud() {
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0.0, Display.getWidth(), Display.getHeight(), 0.0, -1.0, 10.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glClear(GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);  // disable depth-testing


        // player
        // render health bar
        float stretch = 3;
        float xOffset = 20;
        float yOffset = 20;
        float height = 20;
        glDisable(GL_LIGHTING);
        fontHandler.drawString(xOffset, yOffset, player.getName() + " (" + player.getLevel() + ")", Color.yellow);
        yOffset = 40;
        float percentage = (float) player.getCurrentHealth() / (float) player.getMaxHealth() * 100.0f;
        glBegin(GL_QUADS);
        {
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex2f(xOffset, yOffset);
            glVertex2f(percentage * stretch + xOffset, yOffset);
            glVertex2f(percentage * stretch + xOffset, yOffset + height);
            glVertex2f(xOffset, yOffset + height);
        }
        glEnd();
        fontHandler.drawString(xOffset, yOffset, player.getCurrentHealth() + "/" + player.getMaxHealth(), Color.yellow);
        // render resource bar
        yOffset = 60;
        percentage = (float) player.getCurrentResource() / (float) player.getMaxResource() * 100.0f;
        glDisable(GL_LIGHTING);
        glBegin(GL_QUADS);
        {
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex2f(xOffset, yOffset);
            glVertex2f(percentage * stretch + xOffset, yOffset);
            glVertex2f(percentage * stretch + xOffset, yOffset + height);
            glVertex2f(xOffset, yOffset + height);
        }
        glEnd();
        fontHandler.drawString(xOffset, yOffset, player.getCurrentResource() + "/" + player.getMaxResource(), Color.yellow);

        // target
        if (player.getTarget() != null) {
            xOffset = 340;
            yOffset = 20;
            fontHandler.drawString(xOffset, yOffset, player.getTarget().getName() + " (" + player.getTarget().getLevel() + ")", Color.yellow);
            // render healt bar
            yOffset = 40;
            percentage = (float) player.getTarget().getCurrentHealth() / (float) player.getTarget().getMaxHealth() * 100.0f;
            glDisable(GL_LIGHTING);
            glBegin(GL_QUADS);
            {
                glColor3f(0.0f, 1.0f, 0.0f);
                glVertex2f(xOffset, yOffset);
                glVertex2f(percentage * stretch + xOffset, yOffset);
                glVertex2f(percentage * stretch + xOffset, yOffset + height);
                glVertex2f(xOffset, yOffset + height);
            }
            glEnd();
            fontHandler.drawString(xOffset, yOffset, player.getTarget().getCurrentHealth() + "/" + player.getTarget().getMaxHealth(), Color.yellow);
            // render resource bar
            yOffset = 60;
            percentage = (float) player.getTarget().getCurrentResource() / (float) player.getTarget().getMaxResource() * 100.0f;
            glDisable(GL_LIGHTING);
            glBegin(GL_QUADS);
            {
                glColor3f(0.0f, 1.0f, 0.0f);
                glVertex2f(xOffset, yOffset);
                glVertex2f(percentage * stretch + xOffset, yOffset);
                glVertex2f(percentage * stretch + xOffset, yOffset + height);
                glVertex2f(xOffset, yOffset + height);
            }
            glEnd();
            fontHandler.drawString(xOffset, yOffset, player.getTarget().getCurrentResource() + "/" + player.getTarget().getMaxResource(), Color.yellow);
        }

        glEnable(GL_LIGHTING);
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glColor3f(1.0f, 1.0f, 1.0f);
        OrbitCamera.camera.initProjection();
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

    public Map<GameObject, Vector3f> getObjects() {
        return objects;
    }

    public static void cleanUp() {
        world.cleanUp();
    }

    public static ArrayList<GameObject> sphereCollide(float x, float z, float radius) {
        ArrayList<GameObject> res = new ArrayList<GameObject>();
        for (GameObject go : game.getObjects().keySet()) {
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
        for (GameObject go : game.getObjects().keySet()) {
            if (Physics.checkCollision(collider, go) != null) {
                res.add(go);
            }
        }
        return res;
    }
}
