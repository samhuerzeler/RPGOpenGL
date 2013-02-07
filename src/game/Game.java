package game;

import engine.Physics;
import game.gameobject.item.equippableitem.weapon.Bow;
import game.gameobject.item.equippableitem.weapon.Sword;
import game.gameobject.item.equippableitem.weapon.head.HelmOfCommand;
import game.gameobject.statobject.Player;
import game.gameobject.statobject.enemy.Goblin;
import game.gameobject.statobject.npc.Guard;
import java.awt.Rectangle;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;

public class Game {
    
    public static Game game;
    private ArrayList<GameObject> objects;
    private ArrayList<GameObject> remove;
    private Player player;
    
    public Game() {
        objects = new ArrayList<GameObject>();
        remove = new ArrayList<GameObject>();
        player = new Player(Display.getWidth() / 2 - player.SIZE / 2, 0, 0);
        objects.add(player);
        objects.add(new Guard(player.x, player.y, player.z, 1));
        objects.add(new Sword(32, 0, 0));
        objects.add(new Bow(500, 0, 0));
        objects.add(new HelmOfCommand(250, 0, 0));
        objects.add(new Goblin(100, 0, -200, 10));
    }
    
    public void getInput() {
        player.getInput();
    }
    
    public void update() {
        for (GameObject go : objects) {
            if (!go.getRemove()) {
                go.update();
            } else {
                remove.add(go);
            }
        }
        for (GameObject go : remove) {
            objects.remove(go);
        }
    }
    
    public void render() {
        for (GameObject go : objects) {
            go.render();
        }
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
