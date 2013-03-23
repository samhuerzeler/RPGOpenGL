package game.gameobject.statobject;

import engine.Physics;
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
    private Physics physics;
    private float chaseRange;
    protected int enemyTypeId;
    private Random random = new Random();
    private float patrolX;
    private float patrolZ;
    private float lastPatrolX;
    private float lastPatrolZ;
    private boolean calculated = false;
    private boolean patrolling = false;
    private boolean waiting = true;
    private Delay patrolWaitingDelay = new Delay(2000, 13000);
    private Delay patrolMovingDelay = new Delay(2000, 2500);

    public Mob(int level) {
        physics = new Physics();
        stats = new Stats(level, false);
        target = null;
        resetting = false;
        attackDamage = 1;
        sightRange = 300.0f;
        basicFleeRange = 800.0f;
        currentFleeRange = basicFleeRange;
        chaseRange = sightRange * 1.5f;
        patrolRange = 150.0f;
        attackDelay.start();
        patrolMovingDelay.start();
    }

    @Override
    public void update() {
        if (stats.getCurrentHealth() <= 0) {
            setOutOfCombat(this, target);
            die();
            resetThreatMap();
        }
        if (position.y > currentFloor.getHeight(position.x, position.z)) {
            applyGravity();
        } else {
            position.y = currentFloor.getHeight(position.x, position.z);
            physics.resetFallingVelocity();
        }

        if (isInCombat()) {
            setTarget(getHighestTreatTarget());
        }
        if (target == null) {
            if (Util.dist(position.x, position.z, lastPatrolX, lastPatrolZ) > (stats.get(Stats.SPEED))) {
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
            if (!target.isResetting() && target.isAlive() && Util.isInLineOfSight(this, target) && Util.dist(position.x, position.z, target.getX(), target.getZ()) <= attackRange) {
                lookAt(target.getX(), target.getY(), target.getZ());
                if (attackDelay.isOver()) {
                    attack();
                }
            } else {
                if (Util.dist(position.x, position.z, spawnPosition.x, spawnPosition.z) > currentFleeRange || !target.isAlive()) {
                    setOutOfCombat(this, target);
                    removeFromThreatMap(target);
                    setTarget(null);
                } else if (isInCombat() || (!resetting && Util.dist(position.x, position.z, target.getX(), target.getZ()) <= chaseRange)) {
                    chase();
                }
            }
        }
    }

    private void attack() {
        attackDelay.restart();
        target.damage(getAttackDamage());
        target.addToThreatMap(this, attackDamage);
        Log.p(name + " attacking " + target.getName() + " : " + target.getCurrentHealth() + "/" + target.getMaxHealth());
    }

    protected void idle() {
        // patrol
        if (patrolMovingDelay.isOver()) {
            patrolMovingDelay.stop();
            patrolWaitingDelay.restart();
            waiting = true;
        }
        if (patrolWaitingDelay.isOver()) {
            waiting = false;
            patrolWaitingDelay.stop();
            patrolMovingDelay.restart();
        }
        patrolling = true;
        if (!waiting) {
            moveTo(patrolX, 0, patrolZ);
            lastPatrolX = position.x;
            lastPatrolZ = position.z;
            calculated = false;
        } else {
            if (!calculated) {
                calculateRandomPatrolPoint();
            }
        }
        // attack
        if (enemyTypeId != NULL) {
            ArrayList<GameObject> objects = Game.sphereCollide(position.x, position.z, sightRange);
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

    private void calculateRandomPatrolPoint() {
        int maxDistance = 2000;
        float newX = random.nextInt(maxDistance) - maxDistance / 2;
        float newZ = random.nextInt(maxDistance) - maxDistance / 2;
        if (spawnPosition.x - position.x < (float) -Math.sin(Math.toRadians(45)) * patrolRange) {
            // too far to the east (positive x-axis)
            if (newX > 0) {
                newX *= -1;
            }
        }
        if (spawnPosition.x - position.x > (float) Math.sin(Math.toRadians(45)) * patrolRange) {
            // too far to the west (negative x-axis)
            if (newX < 0) {
                newX *= -1;
            }
        }
        if (spawnPosition.z - position.z < (float) -Math.sin(Math.toRadians(45)) * patrolRange) {
            // too far to the south (positive z-axis)
            if (newZ > 0) {
                newZ *= -1;
            }
        }
        if (spawnPosition.z - position.z > (float) Math.sin(Math.toRadians(45)) * patrolRange) {
            // too far to the north (negative z-axis)
            if (newZ < 0) {
                newZ *= -1;
            }
        }

        patrolX = position.x + newX;
        patrolZ = position.z + newZ;
        calculated = true;
    }

    private void chase() {
        if (Util.isInLineOfSight(this, target)) {
            moveTo(target);
        }
    }

    private void resetPosition() {
        resetFleeRange();
        resetThreatMap();
        moveTo(lastPatrolX, 0, lastPatrolZ);
    }

    private void moveTo(GameObject go) {
        moveTo(go.getX(), go.getY(), go.getZ());
    }

    private void moveTo(float x, float y, float z) {
        float distance = Util.dist(position.x, position.z, x, z);
        if (distance == 0) {
            return;
        }
        float dirX = (x - position.x) / distance;
        //float dirY = (y - position.y) / distance;
        float dirZ = (z - position.z) / distance;
        float speedMultiplier = 1.0f;
        if (patrolling) {
            speedMultiplier = 0.5f;
        } else if (resetting) {
            speedMultiplier = 2.0f;
        }
        if (Math.abs(x - this.position.x) > Math.abs(dirX * 2) && Math.abs(z - this.position.z) > Math.abs(dirZ * 2)) {
            // TODO add speed based scaling
            this.position.x += dirX * MOVEMENT_SPEED * Time.getDelta() * speedMultiplier;
            //this.y += dirY * MOVEMENT_SPEED * Time.getDelta() * speedMultiplier;
            this.position.z += dirZ * MOVEMENT_SPEED * Time.getDelta() * speedMultiplier;
            lookAt(x, y, z);
        }
    }

    private void lookAt(float x, float y, float z) {
        float dirX = (x - position.x);
        float dirY = (y - position.y);
        float dirZ = (z - position.z);
        rotation.y = (float) -Math.toDegrees(Math.atan2(dirX, dirZ)) - 180;
        rotation.z = (float) -Math.toDegrees(Math.atan2(dirY, dirZ)) - 180;
    }

    private void applyGravity() {
        position.y -= physics.getFallingDistance();
    }

    public void extendFleeRange() {
        currentFleeRange = Util.dist(position.x, position.z, spawnPosition.x, spawnPosition.z) + basicFleeRange;
    }

    public void resetFleeRange() {
        currentFleeRange = basicFleeRange;
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
}
