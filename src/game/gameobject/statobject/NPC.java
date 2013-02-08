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

public class NPC extends StatObject {

    public static final float DAMPING = 0.5f;
    private float basicFleeRange;
    private float currentFleeRange;
    private float chaseRange;
    private boolean resetting = false;
    private StatObject target;

    public NPC(int level) {
        stats = new Stats(level, false);
        target = null;
        attackRange = 42;
        attackDamage = 1;
        sightRange = 150f;
        basicFleeRange = 300f;
        currentFleeRange = basicFleeRange;
        chaseRange = sightRange * 1.5f;
        attackDelay.terminate();
    }

    @Override
    public void update() {
        if (target != null && target.getCurrentHealth() <= 0) {
            setTarget(null);
        }
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
            if (go.getType() == ENEMY_ID) {
                setTarget((StatObject) go);
            }
        }
    }

    protected void chase() {
        float speedX = (getTarget().getX() - x);
        float speedZ = (getTarget().getZ() - z);

        float maxSpeed = getStats().getSpeed() * DAMPING;

        if (speedX > maxSpeed) {
            speedX = maxSpeed;
        }
        if (speedX < maxSpeed) {
            speedX = -maxSpeed;
        }
        if (speedZ > maxSpeed) {
            speedZ = maxSpeed;
        }
        if (speedZ < maxSpeed) {
            speedZ = -maxSpeed;
        }

        x = x + speedX * Time.getDelta();
        z = z + speedZ * Time.getDelta();
    }

    protected void resetPosition() {
        resetFleeRange();
        float speedX = (spawnX - x);
        float speedZ = (spawnZ - z);

        float maxSpeed = getStats().getSpeed();

        if (speedX > maxSpeed) {
            speedX = maxSpeed;
        }
        if (speedX < maxSpeed) {
            speedX = -maxSpeed;
        }
        if (speedZ > maxSpeed) {
            speedZ = maxSpeed;
        }
        if (speedZ < maxSpeed) {
            speedZ = -maxSpeed;
        }

        if (Math.abs(x - spawnX) > (getStats().getSpeed())) {
            x = x + speedX * Time.getDelta();
        }
        if (Math.abs(z - spawnZ) > (getStats().getSpeed())) {
            z = z + speedZ * Time.getDelta();
        }

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
        this.type = NPC_ID;
        this.spr = new Sprite(r, g, b, sx, sy, sz);
    }
}
