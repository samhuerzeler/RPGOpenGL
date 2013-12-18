package game.gameobject;

import game.Delay;
import game.FloorObject;
import game.Game;
import game.GameObject;
import game.Stats;
import game.gameobject.statobject.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class StatObject extends GameObject {

    protected static final float MOVEMENT_SPEED = 3.0f;
    protected ArrayList<StatObject> combatTargets = new ArrayList<>();
    protected Map<StatObject, Integer> threatMap = new HashMap<>();
    protected boolean resetting;
    protected boolean ceilingCollision;
    protected float stepSize = 50;
    protected StatObject target;
    protected int autoAttackDamage;
    protected float attackRange;
    protected float sightRange;
    protected float basicFleeRange;
    protected float currentFleeRange;
    protected float patrolRange;
    protected Delay autoAttackDelay = new Delay(2000);
    protected Delay gcdDelay = new Delay(1500);
    protected Delay nonGcdDelay = new Delay(1500);
    protected Delay tick = new Delay(3000);
    // object IDs
    public static final int NULL = 0;
    public static final int PLAYER = 1;
    public static final int HOSTILE = 2;
    public static final int FRIENDLY = 3;

    protected void searchFloor() {
        ArrayList<FloorObject> floors = Game.game.getFloorObjects();
        currentFloor = Game.voidFloor;
        for (FloorObject floor : floors) {
            float floorHeight = floor.getHeight(position.x, position.z);
            if (floor.inBound(position.x, position.z)
                    && floorHeight <= position.y + stepSize
                    && floorHeight > currentFloor.getHeight(position.x, position.z)) {
                currentFloor = floor;
            }
        }
    }

    protected void searchCeiling() {
        ArrayList<FloorObject> floors = Game.game.getFloorObjects();
        currentCeiling = Game.sky;
        for (FloorObject floor : floors) {
            float floorHeight = floor.getHeight(position.x, position.z);
            if (floor.inBound(position.x, position.z)
                    && floorHeight > position.y
                    && floorHeight < currentCeiling.getHeight(position.x, position.z)) {
                currentCeiling = floor;
            }
        }
    }

    public void removeHealth(int amt) {
        stats.removeHealth(amt);
    }

    protected void setInCombat(StatObject so1, StatObject so2) {
        so1.addCombatTarget(so2);
        so2.addCombatTarget(so1);
    }

    protected void setOutOfCombat(StatObject so1, StatObject so2) {
        so1.removeCombatTarget(so2);
        so2.removeCombatTarget(so1);
    }

    protected void addCombatTarget(StatObject so) {
        combatTargets.add(so);
        System.out.println(this.getName() + ": combat target added (" + so.getName() + ")");
    }

    protected void removeCombatTarget(StatObject so) {
        combatTargets.remove(so);
        System.out.println(this.getName() + ": combat target removed (" + so.getName() + ")");
    }

    protected boolean isInCombat() {
        return combatTargets.size() > 0;
    }

    protected boolean isInCombatWith(StatObject so) {
        return combatTargets.contains(so);
    }

    protected void replenishHealth() {
        if (!isInCombat() && isAlive() && getCurrentHealth() < getMaxHealth()) {
            stats.replenishHealth();
        }
    }

    protected void replenishResource(Player.playerClass playerClass) {
        if (playerClass == Player.playerClass.WARRIOR) {
            if (!isInCombat() && isAlive() && getCurrentResource() > 0) {
                stats.replenishResource(playerClass);
            }
        } else {
            if (!isInCombat() && isAlive() && getCurrentResource() <= getMaxResource()) {
                stats.replenishResource(playerClass);
            }
        }
    }

    public boolean isAlive() {
        return stats.getCurrentHealth() > 0;
    }

    public StatObject getTarget() {
        return target;
    }

    public int getLevel() {
        return stats.getLevel();
    }

    public int getMaxHealth() {
        return stats.getMaxHealth();
    }

    public int getCurrentHealth() {
        return stats.getCurrentHealth();
    }

    public int getMaxResource() {
        return stats.getMaxResource();
    }

    public int getCurrentResource() {
        return stats.getCurrentResource();
    }

    public float getStrength() {
        return stats.get(Stats.STRENGTH);
    }

    public float getIntelligence() {
        return stats.get(Stats.INTELLIGENCE);
    }

    public float getSpeed() {
        return stats.get(Stats.SPEED);
    }

    public float getAttackRange() {
        return attackRange;
    }

    public int getAttackDamage() {
        return autoAttackDamage;
    }

    public void setAttackRange(int amt) {
        attackRange = amt;
    }

    public void setAttackDamage(int amt) {
        autoAttackDamage = amt;
    }

    public boolean isResetting() {
        return resetting;
    }

    public float getSightRange() {
        return sightRange;
    }

    public void addToThreatMap(StatObject so, int amt) {
        if (threatMap.containsKey(so)) {
            amt += threatMap.get(so).intValue();
        }
        threatMap.put(so, amt);
    }

    public void removeFromThreatMap(StatObject so) {
        threatMap.remove(so);
    }

    public void resetThreatMap() {
        for (StatObject so : threatMap.keySet()) {
            so.setOutOfCombat(this, so);
        }
        threatMap.clear();
        System.out.println(name + ": Threatmap cleared.");
    }

    public StatObject getHighestTreatTarget() {
        Map.Entry<StatObject, Integer> maxEntry = null;
        for (Map.Entry<StatObject, Integer> entry : threatMap.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }
        if (maxEntry != null) {
            return maxEntry.getKey();
        }
        return null;
    }

    protected void die() {
        remove();
    }
}
