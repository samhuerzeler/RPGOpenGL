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

public abstract class Mob extends StatObject {

    public static final float DAMPING = 0.5f;
    protected float chaseRange;
    protected int enemyTypeId;
    private Random random = new Random();
    private boolean patrolling = false;
    private boolean waiting = false;
    private float dirX;
    private float dirZ;
    private float targetX;
    private float targetZ;

    public Mob(int level) {
        stats = new Stats(level, false);
        target = null;
        resetting = false;
        attackDamage = 1;
        sightRange = 500.0f;
        basicFleeRange = 1000.0f;
        currentFleeRange = basicFleeRange;
        chaseRange = sightRange * 1.5f;
        attackDelay.start();
    }

    @Override
    public void update() {
        if (isInCombat()) {
            setTarget(getHighestTreatTarget());
        }
        if (target == null) {
            if (Math.abs(x - spawnX) > (getStats().getSpeed()) || Math.abs(z - spawnZ) > (getStats().getSpeed())) {
                if (!patrolling) {
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
        System.err.println(name + " attacking " + target.getName() + " : " + target.getCurrentHealth() + "/" + target.getMaxHealth());
        attackDelay.restart();
    }

    protected void idle() {
        // patrol
        /**
         * TODO refactor mob patrol
         */
        patrolling = true;
        if (!waiting) {
            moveTo(dirX, 0, dirZ);
            if (targetX != 0 || targetZ != 0) {
                targetX = 0;
                targetZ = 0;
            }
            new Thread(new Mob.Patrol()).start();
        } else {
            if (targetX == 0 && targetZ == 0) {
                targetX = x + random.nextInt(500) - 250;
                targetZ = z + random.nextInt(500) - 250;
                float distance = (float) Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetZ - z, 2));
                dirX = (targetX - x) / distance * DAMPING;
                dirZ = (targetZ - z) / distance * DAMPING;
                int r = 30;
                if (spawnX - x < -Math.sin(Math.toRadians(45)) * r) {
                    if (dirX > 0) {
                        dirX *= -1;
                    }
                }
                if (spawnX - x > Math.sin(Math.toRadians(45)) * r) {
                    if (dirX < 0) {
                        dirX *= -1;
                    }
                }
                if (spawnZ - z < -Math.sin(Math.toRadians(45)) * r) {
                    if (dirZ > 0) {
                        dirZ *= -1;
                    }
                }
                if (spawnZ - z > Math.sin(Math.toRadians(45)) * r) {
                    if (dirZ < 0) {
                        dirZ *= -1;
                    }
                }
            }
        }
        // attack
        if (enemyTypeId != NULL_ID) {
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

    protected void chase() {
        lookAt(target.getX(), target.getY(), target.getZ());
        moveToTarget();
    }

    protected void resetPosition() {
        resetFleeRange();
        resetThreatMap();
        lookAt(spawnX, spawnY, spawnZ);
        moveToTarget();
    }

    protected void moveToTarget() {
        moveTo(dx, dy, dz);
    }

    protected void moveTo(float dx, float dy, float dz) {
        float speed = 1.0f;
        if (resetting) {
            speed = 3.0f;
        }
        x += dx * getStats().getSpeed() * Time.getDelta() * speed;
        y += dy * getStats().getSpeed() * Time.getDelta() * speed;
        z += dz * getStats().getSpeed() * Time.getDelta() * speed;
    }

    protected void lookAt(float targetX, float targetY, float targetZ) {
        float distance = (float) Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2) + Math.pow(targetZ - z, 2));
        dx = (targetX - x) / distance * DAMPING;
        dy = (targetY - y) / distance * DAMPING;
        dz = (targetZ - z) / distance * DAMPING;
        ry = (float) -Math.toDegrees(Math.atan2(dx, dz)) - 180;
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
        attackDelay.start();
    }

    public void setSightRange(float dist) {
        sightRange = dist;
    }

    private class Patrol implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(2500);
                waiting = true;
                new Thread(new Mob.PatrolWaiting()).start();
            } catch (Exception e) {
            }
        }
    }

    private class PatrolWaiting implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(5000);
                waiting = false;
            } catch (Exception e) {
            }
        }
    }
}
