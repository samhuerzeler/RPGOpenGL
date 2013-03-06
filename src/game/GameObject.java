package game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import org.lwjgl.util.glu.Sphere;

public abstract class GameObject {

    protected String name;
    protected int type;
    protected float size;
    protected Sprite spr;
    protected Stats stats;
    protected int attackDamage;
    protected float attackRange;
    protected float sightRange;
    protected float basicFleeRange;
    protected float currentFleeRange;
    protected float patrolRange;
    protected Delay attackDelay = new Delay(1500);
    protected boolean[] flags = new boolean[1];
    // object IDs
    public static final int NULL = 0;
    public static final int PLAYER = 1;
    public static final int ENEMY = 2;
    public static final int NPC = 3;
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
    protected float rx = -33;
    protected float ry;
    protected float rz;

    public void update() {
    }

    public void render() {
        // render sprite and range circles
        glPushMatrix();
        {
            glTranslatef(x, y, z);
            glRotatef(-ry, 0.0f, 1.0f, 0.0f);
            spr.render();
            glColor3f(1.0f, 1.0f, 1.0f);
            if (type == ENEMY || type == NPC) {
                renderCircle(0.0f, 0.0f, sightRange);
            } else if (type == PLAYER) {
                renderCircle(0.0f, 0.0f, attackRange);
            }
        }
        glPopMatrix();

        // render healthbar
        glPushMatrix();
        {
            glTranslatef(x, y, z);
            // TODO rotate healthbar with player camera and fix size
            glRotatef(-ry, 0.0f, 1.0f, 0.0f);
            renderHealthBar();
        }
        glPopMatrix();
    }

    private void renderHealthBar() {
        int currentHealth = stats.getCurrentHealth();
        int maxHealth = stats.getMaxHealth();
        float healthPercentage = (float) currentHealth / (float) maxHealth * 100.0f;
        glTranslatef(-50, 0, 0);
        glBegin(GL_QUADS);
        {
            glVertex2f(0, 40);
            glVertex2f(healthPercentage, 40);
            glVertex2f(healthPercentage, 30);
            glVertex2f(0, 30);
        }
        glEnd();
        glTranslatef(50, 0, 0);
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

    public void remove() {
        flags[0] = true;
    }

    public boolean getRemove() {
        return flags[0];
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

    public float getSpawnX() {
        return spawnX;
    }

    public float getSpawnZ() {
        return spawnZ;
    }

    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.spr = new Sprite(r, g, b, sx, sy, sz);
    }
}
