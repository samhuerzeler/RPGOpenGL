package game.gameobject.statobject;

import game.Delay;
import game.Game;
import game.GameObject;
import game.Sprite;
import game.Stats;
import game.Time;
import game.Util;
import game.gameobject.StatObject;
import java.util.ArrayList;

public class Enemy extends StatObject {

    public static final float DAMPING = 0.5f;
    private float basicFleeRange;
    private float currentFleeRange;
    private float chaseRange;
    private boolean resetting = false;
    private StatObject target;

    public Enemy(int level) {
        stats = new Stats(level, false);
        target = null;
        attackDamage = 1;
        sightRange = 150.0f;
        basicFleeRange = 300.0f;
        currentFleeRange = basicFleeRange;
        chaseRange = sightRange * 1.5f;
        attackDelay.terminate();
    }

    @Override
    public void update() {
        if (target == null) {
            idle();
        } else {
            if (Util.lineOfSight(this, target) && Util.dist(x, z, getTarget().getX(), getTarget().getZ()) <= attackRange) {
                if (attackDelay.isOver()) {
                    attack();
                }
            } else {
                if (Util.dist(x, z, spawnX, spawnZ) > currentFleeRange) {
                    resetting = true;
                    resetPosition();
                } else if (!resetting && Util.dist(x, z, getTarget().getX(), getTarget().getZ()) <= chaseRange) {
                    chase();
                } else {
                    if (Math.abs(x - spawnX) > (getStats().getSpeed()) || Math.abs(z - spawnZ) > (getStats().getSpeed())) {
                        resetPosition();
                    } else {
                        resetting = false;
                        idle();
                    }
                }
            }
        }

        if (stats.getCurrentHealth() <= 0) {
            die();
        }
    }

    protected void attack() {
        getTarget().damage(getAttackDamage());
        System.err.println(name + " attacking " + getTarget().getName() + " : " + getTarget().getCurrentHealth() + "/" + getTarget().getMaxHealth());
        attackDelay.restart();
    }

    protected void idle() {
        ArrayList<GameObject> objects = Game.sphereCollide(x, z, sightRange);

        for (GameObject go : objects) {
            if (go.getType() == PLAYER_ID) {
                setTarget((StatObject) go);
            }
        }
    }

    protected void chase() {
        float startX = x;
        float startZ = z;
        float endX = target.getX();
        float endZ = target.getZ();
        float distance = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endZ - startZ, 2));
        dx = (endX - startX) / distance * DAMPING;
        dz = (endZ - startZ) / distance * DAMPING;
        rotate();
        move();
    }

    protected void resetPosition() {
        resetFleeRange();
        float startX = x;
        float startZ = z;
        float endX = spawnX;
        float endZ = spawnZ;
        float distance = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endZ - startZ, 2));
        dx = (endX - startX) / distance;
        dz = (endZ - startZ) / distance;
        rotate();
        move();
    }

    private void move() {
        x += dx * getStats().getSpeed() * Time.getDelta();
        z += dz * getStats().getSpeed() * Time.getDelta();
    }

    private void rotate() {
        ry = (float) -Math.toDegrees(Math.atan2(dx, dz));
    }

    protected void die() {
        remove();
    }

    public void setTarget(StatObject target) {
        this.target = target;
    }

    public StatObject getTarget() {
        return target;
    }

    public Stats getStats() {
        return stats;
    }

    public void extendFleeRange() {
        currentFleeRange = Util.dist(x, z, spawnX, spawnZ) + basicFleeRange;
    }

    public void resetFleeRange() {
        currentFleeRange = basicFleeRange;
    }

    public void setAttackDelay(int time) {
        attackDelay = new Delay(time);
        attackDelay.terminate();
    }

    public void setSightRange(float dist) {
        sightRange = dist;
    }

    @Override
    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz, int type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.spawnX = x;
        this.spawnY = y;
        this.spawnZ = z;
        this.type = ENEMY_ID;
        this.spr = new Sprite(r, g, b, sx, sy, sz);
    }
}
