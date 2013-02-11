package game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import org.lwjgl.util.glu.Sphere;

public abstract class GameObject {

    protected int type;
    protected Sprite spr;
    protected boolean[] flags = new boolean[1];
    protected int size;
    // object IDs
    public static final int NULL_ID = 0;
    public static final int PLAYER_ID = 1;
    public static final int ENEMY_ID = 2;
    public static final int NPC_ID = 3;
    // position
    protected float x;
    protected float y;
    protected float z;
    // spawn position
    protected float spawnX;
    protected float spawnY;
    protected float spawnZ;
    // directions
    protected float dx;
    protected float dy;
    protected float dz;
    // rotation
    protected float rx;
    protected float ry;
    protected float rz;
    protected Stats stats;
    protected String name;
    protected int attackDamage;
    protected int attackRange;
    protected float sightRange;
    protected float basicFleeRange;
    protected float currentFleeRange;
    protected Delay attackDelay = new Delay(1000);

    public void update() {
    }

    public void render() {
        glPushMatrix();
        {
            glTranslatef(x, y, z);
            glRotatef(-ry, 0.0f, 1.0f, 0.0f);
            spr.render();
            glColor3f(1.0f, 1.0f, 1.0f);
            renderCircle(0.0f, 0.0f, sightRange);
            glColor3f(0.3f, 0.3f, 0.3f);
            renderSphere(0.0f, 0.0f, 0.0f);
        }
        glPopMatrix();
    }

    private void renderSphere(float x, float y, float z) {
        glTranslatef(x, y, z);
        Sphere s = new Sphere();
        s.setDrawStyle(GLU_POINT);
        s.draw(sightRange, 50, 50);
    }

    private void renderCircle(float cx, float cz, float r) {
        int numSegments = 50;
        float theta = (float) (2 * 3.1415926 / numSegments);
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float t;
        float x = r;
        float z = 0;
        glBegin(GL_LINE_LOOP);
        for (int i = 0; i < numSegments; i++) {
            glVertex3f(x + cx, 0.0f, z + cz);
            t = x;
            x = c * x - s * z;
            z = s * t + c * z;
        }
        glEnd();
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
