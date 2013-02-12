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
    private float patrolX;
    private float patrolZ;
    private boolean calculated = false;
    private Patrol patrol = new Patrol();
    private PatrolWaiting patrolWaiting = new PatrolWaiting();

    public Mob(int level) {
        stats = new Stats(level, false);
        target = null;
        resetting = false;
        attackDamage = 1;
        sightRange = 300.0f;
        basicFleeRange = 900.0f;
        currentFleeRange = basicFleeRange;
        chaseRange = sightRange * 1.5f;
        patrolRange = 30;
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
        System.err.println(name + " attacking " + target.getName() + " : " + target.getCurrentHealth() + "/" + target.getMaxHealth());
        attackDelay.restart();
    }

    protected void idle() {
        // patrol
        patrolling = true;
        if (!waiting) {
            if (patrolX == 0 || patrolZ == 0) {
                calculateRandomPatrolPoint();
            }
            moveTo(patrolX, 0, patrolZ);
            calculated = false;
            if (!patrol.running) {
                new Thread(patrol).start();
            }
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
        int maxDistance = 5000;
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
        moveTo(spawnX, spawnY, spawnZ);
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
        attackDelay.start();
    }

    public void setSightRange(float dist) {
        sightRange = dist;
    }

    private class Patrol implements Runnable {

        private boolean running = false;

        public boolean isRunning() {
            return running;
        }

        @Override
        public void run() {
            try {
                running = true;
                // move between 1.5 and 3.5 sec
                int sleepTime = random.nextInt(2000) + 1500;
                Thread.sleep(sleepTime);
                running = false;
                waiting = true;
                if (!patrolWaiting.running) {
                    new Thread(patrolWaiting).start();
                }
            } catch (Exception e) {
            }
        }
    }

    private class PatrolWaiting implements Runnable {

        private boolean running = false;

        public boolean isRunning() {
            return running;
        }

        @Override
        public void run() {
            try {
                running = true;
                // wait between 2 and 6 sec
                int sleepTime = random.nextInt(6000) + 2000;
                Thread.sleep(sleepTime);
                running = false;
                waiting = false;
            } catch (Exception e) {
            }
        }
    }
}
