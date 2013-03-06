package game.gameobject;

import game.Game;
import game.GameObject;
import game.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class StatObject extends GameObject {

    protected World world = Game.world;
    protected ArrayList<StatObject> combatTargets = new ArrayList<StatObject>();
    protected Map<StatObject, Integer> threatMap = new HashMap<StatObject, Integer>();
    protected boolean resetting;
    protected StatObject target;

    public void damage(int amt) {
        stats.damage(amt);
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
    }

    protected void removeCombatTarget(StatObject so) {
        combatTargets.remove(so);
    }

    protected boolean isInCombat() {
        return combatTargets.size() > 0;
    }
    
    protected void replenishHealth() {
        if(!isInCombat() && isAlive() && getCurrentHealth() < getMaxHealth()) {
            stats.replenishHealth();
        }
    }

    public boolean isAlive() {
        return stats.getCurrentHealth() > 0;
    }

    public String getName() {
        return name;
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

    public float getStrength() {
        return stats.getStrength();
    }

    public float getMana() {
        return stats.getMana();
    }

    public float getSpeed() {
        return stats.getSpeed();
    }

    public float getAttackRange() {
        return attackRange;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackRange(int amt) {
        attackRange = amt;
    }

    public void setAttackDamage(int amt) {
        attackDamage = amt;
    }

    public boolean isResetting() {
        return resetting;
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
        threatMap.clear();
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
