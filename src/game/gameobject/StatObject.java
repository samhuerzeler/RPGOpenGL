package game.gameobject;

import engine.OrbitCamera;
import game.Delay;
import game.GameObject;
import game.Stats;
import game.Util;
import game.gameobject.statobject.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import util.Log;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public abstract class StatObject extends GameObject {

    protected static final float MOVEMENT_SPEED = 5.0f;
    protected ArrayList<StatObject> combatTargets = new ArrayList<>();
    protected Map<StatObject, Integer> threatMap = new HashMap<>();
    protected boolean resetting;
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
    public static final int ENEMY = 2;
    public static final int NPC = 3;

    @Override
    public void render() {
        // render gameobjects
        super.render();

        // render range circles
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

        if (type != PLAYER) {
            // render health bar
            int currentHealth = stats.getCurrentHealth();
            int maxHealth = stats.getMaxHealth();
            glPushMatrix();
            {
                glTranslatef(position.x, position.y, position.z);
                if (type == ENEMY) {
                    glColor3f(1.0f, 0.0f, 0.0f);
                } else if (type == NPC) {
                    glColor3f(1.0f, 1.0f, 0.0f);
                }
                renderBar(currentHealth, maxHealth, 40, 5);
            }
            glPopMatrix();

            // render resource bar
            int currentResource = stats.getCurrentResource();
            int maxResource = stats.getMaxResource();
            glPushMatrix();
            {
                glTranslatef(position.x, position.y, position.z);
                switch (type) {
                    case ENEMY:
                        glColor3f(1.0f, 0.0f, 0.0f);
                        break;
                    case NPC:
                        glColor3f(1.0f, 1.0f, 0.0f);
                        break;
                }
                renderBar(currentResource, maxResource, 35, 5);
            }
            glPopMatrix();
        }
    }

    private void renderBar(int current, int max, float y, float height) {
        float percentage = (float) current / (float) max * 100.0f;
        Vector3f cameraRotation = OrbitCamera.camera.getRotation();
        glRotatef(-cameraRotation.y + 180, 0, 1, 0);
        glRotatef(-cameraRotation.x, 1, 0, 0);
        glScalef(-1, 1, 1);
        glTranslatef(-20, 0, 0);
        glBegin(GL_QUADS);
        {
            glVertex2f(0, y);
            glVertex2f(percentage * 20 / 50, y);
            glVertex2f(percentage * 20 / 50, y - height);
            glVertex2f(0, y - height);
        }
        glEnd();
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
        Log.p(this.getName() + ": combat target added (" + so.getName() + ")");
    }

    protected void removeCombatTarget(StatObject so) {
        combatTargets.remove(so);
        Log.p(this.getName() + ": combat target removed (" + so.getName() + ")");
    }

    protected boolean isInCombat() {
        return combatTargets.size() > 0;
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
        Log.p(name + ": Threatmap cleared.");
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
