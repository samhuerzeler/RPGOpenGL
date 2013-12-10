package game;

import engine.Animation;
import engine.Model;
import engine.ModelLoader;
import game.gameobject.statobject.Player;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.util.vector.Vector3f;

public abstract class GameObject {

    protected FloorObject world = Game.world;
    protected FloorObject sky = Game.sky;
    protected FloorObject currentFloor = world;
    protected FloorObject currentCeiling = sky;
    protected String name;
    protected int type;
    protected float size;
    protected ArrayList<Model> keyFrames;
    protected Animation animation;
    protected boolean moving;
    protected Sprite sprite;
    protected Model model;
    protected Stats stats;
    protected Vector3f position = new Vector3f();
    protected Vector3f spawnPosition = new Vector3f();
    protected Vector3f direction = new Vector3f();
    protected Vector3f rotation = new Vector3f();
    protected boolean[] flags = new boolean[1]; // 0 = remove object

    public void update() {
    }

    protected void loadModel(String path) {
        model = null;
        try {
            model = ModelLoader.loadModel(new File(path));
            model.init();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void loadModel() {
        model = keyFrames.get(0);
        model.init();
    }

    public void remove() {
        flags[0] = true;
    }

    public boolean getFlag(int index) {
        return flags[index];
    }

    public Model getModel() {
        return model;
    }

    public Animation getAnimation() {
        return animation;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }

    public float getRX() {
        return rotation.x;
    }

    public float getRY() {
        return rotation.y;
    }

    public float getRZ() {
        return rotation.z;
    }

    public float getSX() {
        return sprite.getSizeX();
    }

    public float getSY() {
        return sprite.getSizeY();
    }

    public float getSZ() {
        return sprite.getSizeZ();
    }
    
    public String getName() {
        return name;
    }
    
    public Vector3f getPosition() {
        return position;
    }
    
    public Vector3f getRotation() {
        return rotation;
    }
    
    public Stats getStats() {
        return stats;
    }

    public int getType() {
        return type;
    }

    public float getSpawnX() {
        return spawnPosition.x;
    }

    public float getSpawnZ() {
        return spawnPosition.z;
    }

    public boolean isMoving() {
        return moving;
    }

    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz) {
        position.x = x;
        position.y = y;
        position.z = z;
        rotation.x = -33;
        sprite = new Sprite(r, g, b, sx, sy, sz);
    }
}
