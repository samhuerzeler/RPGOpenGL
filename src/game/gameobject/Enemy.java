package game.gameobject;

import game.Sprite;
import game.Delay;
import game.Game;
import game.Time;
import game.Util;
import java.util.ArrayList;

public class Enemy extends StatObject {

    public static final float DAMPING = 0.5f;
    protected float spawnX;
    protected float spawnY;
    protected float spawnZ;
    private float sightRange;
    private float chaseRange;
    private float attackRange;
    private int attackDamage;
    private Delay attackDelay;
    private StatObject target;

    public Enemy(int level) {
        stats = new Stats(level, false);
        target = null;
        attackDelay = new Delay(500);
        attackRange = 42f;
        attackDamage = 1;
        sightRange = 120f;
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
                if (Util.dist(x, z, getTarget().getX(), getTarget().getZ()) <= chaseRange) {
                    chase();
                } else {
                    if (Math.abs(x - spawnX) > (getStats().getSpeed() * DAMPING) || Math.abs(z - spawnZ) > (getStats().getSpeed() * DAMPING)) {
                        resetPosition();
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
        System.out.println("We're hit : " + getTarget().getCurrentHealth() + "/" + getTarget().getMaxHealth());
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
        float speedX = (getTarget().getX() - x);
        float speedZ = (getTarget().getY() - z);

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
        float speedX = (spawnX - x);
        float speedZ = (spawnZ - z);

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

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackRange(int range) {
        attackRange = range;
    }

    public void setAttackDelay(int time) {
        attackDelay = new Delay(time);
        attackDelay.terminate();
    }

    public void setAttackDamage(int amt) {
        attackDamage = amt;
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
