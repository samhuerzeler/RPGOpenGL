package game.gameobject;

import game.GameObject;
import game.Stats;

public class StatObject extends GameObject {

    protected Stats stats;
    protected String name;
    protected int attackDamage;
    protected int attackRange;

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
}
