package game;

import game.gameobject.statobject.Player;

public final class Stats {

    public enum resource {

        RAGE, MANA, ENERGY
    }
    //public static final double LEVEL_CONST = (double) MAX_XP / ((double) MAX_LEVEL * (double) MAX_LEVEL);
    public static final double LEVEL_CONST = 25.0 * Math.pow(3.0, (3.0 / 2.0));
    public static final double MAX_XP = 636529; // max level 60
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
    private int maxHealth;
    private int resource;
    private int maxResource;
    private boolean levelable;
    private StatScale scale = new StatScale();

    public Stats(double xp, boolean levelable) {
        this.levelable = levelable;
        // REMOVE !!
        //scale.generateScale();
        // =====
        if (this.levelable) {
            if (xp >= MAX_XP) {
                this.xp = MAX_XP;
            } else {
                this.xp = xp;
            }
            level = (int) Math.sqrt(this.xp / LEVEL_CONST);
        } else {
            this.xp = -1;
            this.level = (int) xp;
        }
        health = getMaxHealth();
        maxHealth = getMaxHealth();
        resource = 0;
        maxResource = 100;
    }

    public int getCurrentHealth() {
        int max = getMaxHealth();
        if (health > max) {
            health = max;
        }
        return health;
    }

    public int getCurrentResource() {
        int max = getMaxResource();
        if (resource > max) {
            resource = max;
        }
        return resource;
    }

    public void replenishHealth() {
        int amt = 3;
        int healthBefore = health;
        health += amt;
        if (health > maxHealth) {
            health = maxHealth;
        }
        System.out.println("health gained: " + health + "/" + maxHealth + " (+" + (health - healthBefore) + ")");
    }

    public void replenishResource(Player.playerClass playerClass) {
        int amt = 0;
        int resourceBefore = resource;

        if (playerClass == Player.playerClass.WARRIOR) {
            amt = -1;
        } else if (playerClass == Player.playerClass.PRIEST) {
            amt = 20;
        }
        resource += amt;
        if (resource > maxResource) {
            resource = maxResource;
        }
        if (resource < 0) {
            resource = 0;
        }
        System.out.println("resource update: " + resource + "/" + maxResource + " (" + (resource - resourceBefore) + ")");
    }

    /**
     * getLevel method. calculates the current level of a character based on
     * current xp
     *
     * calculate xp from level = ax^2 calculate level from xp = sqrt(xp/a)
     *
     * @return int Level
     */
    public int getLevel() {
        return level;

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

    public int getMaxResource() {
        return maxResource;
    }

    public void addXp(float amt) {
        xp += amt;
        if (xp > MAX_XP) {
            xp = MAX_XP;
        }
    }

    public void addHealth(int amt) {
        health += amt;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public void addResource(int amt) {
        resource += amt;
        if (resource > maxResource) {
            resource = maxResource;
        }
    }

    public void subtractResource(int amt) {
        resource -= amt;
        if (resource < 0) {
            resource = 0;
        }
    }

    public void removeHealth(int amt) {
        health -= amt;
        if (health < 0) {
            health = 0;
        }
    }
}
