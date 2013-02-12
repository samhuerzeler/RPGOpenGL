package game.gameobject.statobject;

import game.Delay;
import game.Game;
import game.GameObject;
import game.Stats;
import game.Time;
import game.Util;
import game.gameobject.StatObject;
import java.util.ArrayList;
import java.util.Random;
import util.Log;

public abstract class Mob extends StatObject {

    public static final float DAMPING = 0.5f;
    protected float chaseRange;
    protected int enemyTypeId;
    private Random random = new Random();
    private float patrolX;
    private float patrolZ;
    private float lastPatrolX;
    private float lastPatrolZ;
    private boolean calculated = false;
    private boolean patrolling = false;
    private boolean waiting = true;
    protected Delay patrolWaitingDelay = new Delay(2000, 13000);
    protected Delay patrolMovingDelay = new Delay(2000, 2500);

    public Mob(int level) {
        stats = new Stats(level, false);
        target = null;
        resetting = false;
        attackDamage = 1;
        sightRange = 300.0f;
        basicFleeRange = 800.0f;
        currentFleeRange = basicFleeRange;
        chaseRange = sightRange * 1.5f;
        patrolRange = 150.0f;
        attackDelay.init();
        patrolWaitingDelay.init();
        patrolWaitingDelay.start();
    }

    @Override
    public void update() {
        if (isInCombat()) {
            setTarget(getHighestTreatTarget());
        }
        if (target == null) {
            if (Util.dist(x, z, lastPatrolX, lastPatrolZ) > (getStats().getSpeed())) {
                if (!patrolling && lastPatrolX != 0 && lastPatrolZ != 0) {
                    resetPosition();
                    resetting = true;
                } else {
                    resetting = false;
                    idle();
                }
            } else {
                resetting = false;
                idle();
            }
        } else {
            patrolling = false;
            if (!target.isResetting() && target.isAlive() && Util.lineOfSight(this, target) && Util.dist(x, z, target.getX(), target.getZ()) <= attackRange) {
                lookAt(target.getX(), target.getY(), target.getZ());
                if (attackDelay.isOver()) {
                    attack();
                }
            } else {
                if (Util.dist(x, z, spawnX, spawnZ) > currentFleeRange || !target.isAlive()) {
                    setOutOfCombat(this, target);
                    removeFromThreatMap(target);
                    setTarget(null);
                } else if (isInCombat() || (!resetting && Util.dist(x, z, target.getX(), target.getZ()) <= chaseRange)) {
                    chase();
                }
            }
        }

        if (stats.getCurrentHealth() <= 0) {
            die();
            resetThreatMap();
        }
    }

    protected void attack() {
        target.damage(getAttackDamage());
        target.addToThreatMap(this, attackDamage);
        Log.p(name + " attacking " + target.getName() + " : " + target.getCurrentHealth() + "/" + target.getMaxHealth());
        attackDelay.start();
    }

    protected void idle() {
        // patrol
        if (patrolMovingDelay.isOver()) {
            patrolMovingDelay.stop();
            patrolWaitingDelay.start();
            waiting = true;
        }
        if (patrolWaitingDelay.isOver()) {
            waiting = false;
            patrolWaitingDelay.stop();
            patrolMovingDelay.start();
        }
        patrolling = true;
        if (!waiting) {
            moveTo(patrolX, 0, patrolZ);
            lastPatrolX = x;
            lastPatrolZ = z;
            calculated = false;
        } else {
            if (!calculated) {
                calculateRandomPatrolPoint();
            }
        }
        // attack
        if (enemyTypeId != NULL) {
            ArrayList<GameObject> objects = Game.sphereCollide(x, z, sightRange);
            for (GameObject go : objects) {
                if (go.getType() == enemyTypeId) {
                    setTarget((StatObject) go);
                    setInCombat(this, target);
                    addToThreatMap(target, 0);
                    patrolling = false;
                }
            }
        }
    }

    protected void calculateRandomPatrolPoint() {
        int maxDistance = 2000;
        float newX = random.nextInt(maxDistance) - maxDistance / 2;
        float newZ = random.nextInt(maxDistance) - maxDistance / 2;
        if (spawnX - x < (float) -Math.sin(Math.toRadians(45)) * patrolRange) {
            // too far to the east
            if (newX > 0) {
                newX *= -1;
            }
        }
        if (spawnX - x > (float) Math.sin(Math.toRadians(45)) * patrolRange) {
            // too far to the west
            if (newX < 0) {
                newX *= -1;
            }
        }
        if (spawnZ - z < (float) -Math.sin(Math.toRadians(45)) * patrolRange) {
            // too far to the south
            if (newZ > 0) {
                newZ *= -1;
            }
        }
        if (spawnZ - z > (float) Math.sin(Math.toRadians(45)) * patrolRange) {
            // too far to the north
            if (newZ < 0) {
                newZ *= -1;
            }
        }

        patrolX = x + newX;
        patrolZ = z + newZ;
        calculated = true;
    }

    protected void chase() {
        moveToTarget();
    }

    protected void resetPosition() {
        resetFleeRange();
        resetThreatMap();
        moveTo(lastPatrolX, 0, lastPatrolZ);
    }

    protected void moveToTarget() {
        moveTo(target.getX(), target.getY(), target.getZ());
    }

    protected void moveTo(float x, float y, float z) {
        float distance = Util.dist(this.x, this.z, x, z);
        if (distance == 0) {
            return;
        }
        float dirX = (x - this.x) / distance;
        float dirY = (y - this.y) / distance;
        float dirZ = (z - this.z) / distance;
        float speedMultiplier = 1.0f;
        if (resetting) {
            speedMultiplier = 2.0f;
        }
        if (patrolling) {
            speedMultiplier = 0.5f;
        }
        if (Math.abs(x - this.x) > Math.abs(dirX * 2) && Math.abs(z - this.z) > Math.abs(dirZ * 2)) {
            this.x += dirX * getStats().getSpeed() * Time.getDelta() * speedMultiplier;
            this.y += dirY * getStats().getSpeed() * Time.getDelta() * speedMultiplier;
            this.z += dirZ * getStats().getSpeed() * Time.getDelta() * speedMultiplier;
            lookAt(x, y, z);
        }
    }

    protected void lookAt(float x, float y, float z) {
        float dirX = (x - this.x);
        float dirY = (y - this.y);
        float dirZ = (z - this.z);
        ry = (float) -Math.toDegrees(Math.atan2(dirX, dirZ)) - 180;
    }

    public void extendFleeRange() {
        currentFleeRange = Util.dist(x, z, spawnX, spawnZ) + basicFleeRange;
    }

    public void resetFleeRange() {
        currentFleeRange = basicFleeRange;
    }

    public Stats getStats() {
        return stats;
    }

    public void setTarget(StatObject target) {
        this.target = target;
    }

    public void setAttackDelay(int time) {
        attackDelay = new Delay(time);
        attackDelay.init();
    }

    public void setSightRange(float dist) {
        sightRange = dist;
    }
}
