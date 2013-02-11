package game.gameobject;

import game.GameObject;
import java.util.ArrayList;

public abstract class StatObject extends GameObject {

    protected ArrayList<StatObject> combatTargets = new ArrayList<StatObject>();

    public abstract boolean isResetting();

    public abstract void addToThreatMap(StatObject so, int amt);

    public abstract void removeFromThreatMap(StatObject so);

    protected abstract void die();

    public void damage(int amt) {
        stats.damage(amt);
    }

    public float getSpeed() {
        return stats.getSpeed();
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

    public String getName() {
        return name;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int amt) {
        attackRange = amt;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int amt) {
        attackDamage = amt;
    }

    public boolean isAlive() {
        return stats.getCurrentHealth() > 0;
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
}
