package game;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public abstract class GameObject {

    protected World world = Game.world;
    protected World currentFloor = world;
    protected String name;
    protected int type;
    protected float size;
    protected Sprite sprite;
    protected Stats stats;
    protected Vector3f position = new Vector3f();
    protected Vector3f spawnPosition = new Vector3f();
    protected Vector3f direction = new Vector3f();
    protected Vector3f rotation = new Vector3f();
    protected boolean[] flags = new boolean[1];

    public void update() {
    }

    public void render() {
        // render sprite/texture
        glPushMatrix();
        {
            glTranslatef(position.x, position.y, position.z);
            glRotatef(-rotation.y, 0.0f, 1.0f, 0.0f);
            sprite.render();
        }
        glPopMatrix();
    }

    public void remove() {
        flags[0] = true;
    }

    public boolean getFlag(int index) {
        return flags[index];
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

    public int getType() {
        return type;
    }

    public float getSpawnX() {
        return spawnPosition.x;
    }

    public float getSpawnZ() {
        return spawnPosition.z;
    }

    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz) {
        position.x = x;
        position.y = y;
        position.z = z;
        rotation.x = -33;
        sprite = new Sprite(r, g, b, sx, sy, sz);
    }
}
