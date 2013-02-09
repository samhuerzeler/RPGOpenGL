package game.gameobject.statobject;

import game.Delay;
import game.Game;
import game.GameObject;
import game.Stats;
import game.Time;
import game.Util;
import game.gameobject.StatObject;
import java.util.ArrayList;

public abstract class Mob extends StatObject {

    public static final float DAMPING = 0.5f;
    protected float basicFleeRange;
    protected float currentFleeRange;
    protected float chaseRange;
    protected boolean resetting;
    protected StatObject target;
    protected int enemyTypeId;

    public Mob(int level) {
        stats = new Stats(level, false);
        target = null;
        resetting = false;
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
            // if in line of sight
            // if in attack range
            if (!target.isResetting() && target.isAlive() && Util.lineOfSight(this, target) && Util.dist(x, z, target.getX(), target.getZ()) <= attackRange) {
                if (attackDelay.isOver()) {
                    attack();
                }
            } else {
                // if target not alive
                // if not too far away from spawn point
                if (target.isResetting() || !target.isAlive() || Util.dist(x, z, spawnX, spawnZ) > currentFleeRange) {
                    // if not yet at spawn point
                    if (Math.abs(x - spawnX) > (getStats().getSpeed()) || Math.abs(z - spawnZ) > (getStats().getSpeed())) {
                        resetPosition();
                        resetting = true;
                    } else {
                        // if at spawn point
                        resetting = false;
                        idle();
                    }
                } else if (!resetting && Util.dist(x, z, target.getX(), target.getZ()) <= chaseRange) {
                    // if not resetting
                    // if target not too far away
                    chase();
                } else {
                    // if not yet at spawn point
                    if (Math.abs(x - spawnX) > (getStats().getSpeed()) || Math.abs(z - spawnZ) > (getStats().getSpeed())) {
                        resetPosition();
                        resetting = true;
                    } else {
                        // if at spawn point
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
        target.damage(getAttackDamage());
        System.err.println(name + " attacking " + target.getName() + " : " + target.getCurrentHealth() + "/" + target.getMaxHealth());
        attackDelay.restart();
    }

    protected void idle() {
        if (enemyTypeId != NULL_ID) {
            ArrayList<GameObject> objects = Game.sphereCollide(x, z, sightRange);
            for (GameObject go : objects) {
                if (go.getType() == enemyTypeId) {
                    setTarget((StatObject) go);
                }
            }
        }
    }

    protected void chase() {
        float startX = x;
        float startY = y;
        float startZ = z;
        float endX = target.getX();
        float endY = target.getY();
        float endZ = target.getZ();
        float distance = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2) + Math.pow(endZ - startZ, 2));
        dx = (endX - startX) / distance * DAMPING;
        dy = (endY - startY) / distance * DAMPING;
        dz = (endZ - startZ) / distance * DAMPING;
        rotate();
        move();
    }

    protected void resetPosition() {
        resetFleeRange();
        float startX = x;
        float startY = y;
        float startZ = z;
        float endX = spawnX;
        float endY = spawnY;
        float endZ = spawnZ;
        float distance = (float) Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2) + Math.pow(endZ - startZ, 2));
        dx = (endX - startX) / distance;
        dy = (endY - startY) / distance;
        dz = (endZ - startZ) / distance;
        rotate();
        move();
    }

    protected void move() {
        x += dx * getStats().getSpeed() * Time.getDelta();
        y += dy * getStats().getSpeed() * Time.getDelta();
        z += dz * getStats().getSpeed() * Time.getDelta();
    }

    protected void rotate() {
        ry = (float) -Math.toDegrees(Math.atan2(dx, dz));
    }

    protected void die() {
        remove();
    }

    @Override
    public boolean isResetting() {
        return resetting;
    }

    public void setTarget(StatObject target) {
        this.target = target;
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
    }
}
