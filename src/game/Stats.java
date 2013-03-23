package game;

import util.Log;

public final class Stats {

//    public static final double LEVEL_CONST = 25.0 * Math.pow(3.0, (3.0 / 2.0)); Old level constant
    public static final double MAX_LEVEL = 60;
    public static final double MAX_XP = 60000;
    public static final double LEVEL_CONST = (double) MAX_XP / ((double) MAX_LEVEL * (double) MAX_LEVEL);
    // stats
    public static final int VITALITY = 0;
    public static final int STRENGTH = 1;
    public static final int DEXTERITY = 2;
    public static final int INTELLIGENCE = 3;
    public static final int SPEED = 4;
    public static final int DEFENSE = 5;
    public static final int DODGE = 6;
    public static final int BLOCK = 7;
    private int level;
    private double xp;
    private int health;
    private boolean levelable;
    private StatScale scale = new StatScale();

    public Stats(float xp, boolean levelable) {
        this.levelable = levelable;
        // REMOVE !!
        scale.generateScale();
        // =====
        if (levelable) {
            this.xp = xp;
            this.level = 1;
        } else {
            this.xp = -1;
            this.level = (int) xp;
        }
        health = getMaxHealth();
    }

    public int getCurrentHealth() {
        int max = getMaxHealth();
        if (health > max) {
            health = max;
        }
        return health;
    }

    public void replenishHealth() {
        int amt = 5;
        int healthBefore = health;
        health += amt;
        int maxHealth = getMaxHealth();
        if (health > maxHealth) {
            health = maxHealth;
        }
        Log.p("replenishing health... " + health + " (+" + (health - healthBefore) + ")");
    }

    /**
     * getLevel method. calculates the current level of a character based on current xp
     *
     * calculate xp from level = ax^2 calculate level from xp = sqrt(xp/a)
     *
     * @return int Level
     */
    public int getLevel() {
        if (!levelable) {
            return level;
        }
        return (int) Math.sqrt((double) xp / LEVEL_CONST);

        /*
         * Old leveling formula
         */
//        double x = xp + 105.0;
//
//        double a = Math.sqrt(243.0 * (x * x) + 4050.0 * x + 17500.0);
//        double c = (3.0 * x + 25.0) / 25.0;
//        double d = Math.cbrt(a / LEVEL_CONST + c);
//
//        return (int) (d - 1.0 / d * 3.0) - 1;
    }

    public float get(int stat) {
        return (int) (getLevel() * scale.getScale(stat) * 10);
    }

    public int getMaxHealth() {
        return (int) (getLevel() * scale.getScale(VITALITY) * 10);
    }

    public int getMaxMana() {
        return (int) (getLevel() * scale.getScale(INTELLIGENCE) * 10);
    }

    public void addXp(float amt) {
        xp += amt;
        if (xp > MAX_XP) {
            xp = MAX_XP;
        }
    }

    public void damage(int amt) {
        health -= amt;
    }
}
