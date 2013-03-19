package game.gameobject;

import game.Delay;
import game.GameObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.opengl.GL11.*;

public abstract class StatObject extends GameObject {

    protected ArrayList<StatObject> combatTargets = new ArrayList<StatObject>();
    protected Map<StatObject, Integer> threatMap = new HashMap<StatObject, Integer>();
    protected boolean resetting;
    protected StatObject target;
    protected int attackDamage;
    protected float attackRange;
    protected float sightRange;
    protected float basicFleeRange;
    protected float currentFleeRange;
    protected float patrolRange;
    protected Delay attackDelay = new Delay(1500);
    protected Delay tick = new Delay(3000);

    @Override
    public void render() {
        // render sprite/texture
        glPushMatrix();
        {
            glTranslatef(position.x, position.y, position.z);
            glRotatef(-rotation.y, 0.0f, 1.0f, 0.0f);
            sprite.render();
        }
        glPopMatrix();

        // render healthbar
        glPushMatrix();
        {
            glTranslatef(position.x, position.y, position.z);
            // TODO rotate healthbar with player camera and fix size
            glRotatef(-rotation.y, 0.0f, 1.0f, 0.0f);
            renderHealthBar();
        }
        glPopMatrix();
    }

    private void renderHealthBar() {
        int currentHealth = stats.getCurrentHealth();
        int maxHealth = stats.getMaxHealth();
        float healthPercentage = (float) currentHealth / (float) maxHealth * 100.0f;
        glTranslatef(-50, 0, 0);
        glBegin(GL_QUADS);
        {
            glVertex2f(0, 40);
            glVertex2f(healthPercentage, 40);
            glVertex2f(healthPercentage, 30);
            glVertex2f(0, 30);
        }
        glEnd();
        glTranslatef(50, 0, 0);
    }

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
        if (!isInCombat() && isAlive() && getCurrentHealth() < getMaxHealth()) {
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
