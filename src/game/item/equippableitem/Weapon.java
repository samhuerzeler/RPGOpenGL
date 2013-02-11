package game.item.equippableitem;

import game.item.EquippableItem;

public abstract class Weapon extends EquippableItem {

    protected static final int MELEE_RANGE = 42;
    protected static final int CAST_RANGE = 300;
    protected int damage;
    protected int range;
    protected int speed;

    public int getDamage() {
        return damage;
    }

    public int getRange() {
        return range;
    }

    public int getSpeed() {
        return speed;
    }
}
