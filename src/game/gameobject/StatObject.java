package game.gameobject;

import game.Delay;
import game.GameObject;
import game.Stats;
import game.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.opengl.GL11.*;

public abstract class StatObject extends GameObject {

    protected static final float MOVEMENT_SPEED = 0.2f;
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
    // object IDs
    protected static final int NULL = 0;
    protected static final int PLAYER = 1;
    protected static final int ENEMY = 2;
    protected static final int NPC = 3;

    @Override
    public void render() {
        super.render();
        // render sprite and range circles
        glPushMatrix();
        {
            glTranslatef(position.x, position.y, position.z);
            glRotatef(-rotation.y, 0.0f, 1.0f, 0.0f);
            glColor3f(1.0f, 1.0f, 1.0f);
            if (type == ENEMY || type == NPC) {
                Util.renderCircle(0.0f, 0.0f, sightRange);
                Util.renderCircle(0.0f, 0.0f, attackRange);
            } else if (type == PLAYER) {
                Util.renderCircle(0.0f, 0.0f, attackRange);
            }
        }
        glPopMatrix();
        // render healthbar
        glPushMatrix();
        {
            glTranslatef(position.x, position.y, position.z);
            // TODO rotate healthbar with player camera and fix size
            glRotatef(-rotation.y, 0.0f, 1.0f, 0.0f);
            if (type == ENEMY) {
                glColor3f(1.0f, 0.0f, 0.0f);
            } else if (type == NPC) {
                glColor3f(1.0f, 1.0f, 0.0f);
            } else if (type == PLAYER) {
                glColor3f(0.0f, 1.0f, 0.0f);
            }
            renderHealthBar();
            glColor3f(1.0f, 1.0f, 1.0f);
        }
        glPopMatrix();
    }

    private void renderHealthBar() {
        int currentHealth = stats.getCurrentHealth();
        int maxHealth = stats.getMaxHealth();
        float healthPercentage = (float) currentHealth / (float) maxHealth * 100.0f;
        glTranslatef(-5, 0, 0);
        glBegin(GL_QUADS);
        {
            glVertex2f(0, 6);
            glVertex2f(healthPercentage / 10, 6);
            glVertex2f(healthPercentage / 10, 5);
            glVertex2f(0, 5);
        }
        glEnd();
        glTranslatef(5, 0, 0);
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
