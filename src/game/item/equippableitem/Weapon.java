package game.item.equippableitem;

import game.item.EquippableItem;

public abstract class Weapon extends EquippableItem {

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
