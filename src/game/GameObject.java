package game;

import static org.lwjgl.opengl.GL11.*;

public abstract class GameObject {

    // Objects IDs
    public static final int ITEM_ID = 1;
    public static final int PLAYER_ID = 2;
    public static final int ENEMY_ID = 3;
    public static final int NPC_ID = 4;
    // position
    protected float x;
    protected float y;
    protected float z;
    // rotation
    protected float rx;
    protected float ry;
    protected float rz;
    protected int type;
    protected Sprite spr;
    protected boolean[] flags = new boolean[1];

    public void update() {
    }

    public void render() {
        glPushMatrix();
        {
            glTranslatef(x, y, z);
            spr.render();
        }
        glPopMatrix();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getRX() {
        return rx;
    }

    public float getRY() {
        return ry;
    }

    public float getRZ() {
        return rz;
    }

    public float getSX() {
        return spr.getSX();
    }

    public float getSY() {
        return spr.getSY();
    }

    public float getSZ() {
        return spr.getSZ();
    }

    public int getType() {
        return type;
    }

    public boolean getRemove() {
        return flags[0];
    }

    public void remove() {
        flags[0] = true;
    }

    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz, int type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.spr = new Sprite(r, g, b, sx, sy, sz);
    }
}
