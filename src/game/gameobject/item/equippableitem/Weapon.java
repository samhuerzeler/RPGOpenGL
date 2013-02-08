package game.gameobject.item.equippableitem;

import game.gameobject.item.EquippableItem;

public abstract class Weapon extends EquippableItem {

    protected static final int MELEE_RANGE = 42;
    protected static final int CAST_RANGE = 300;
    protected int damage;
    protected int range;
    protected int speed;

    public int getDamage() {
        return damage;
    }

    public void setDamage(int amt) {
        damage = amt;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int amt) {
        range = amt;
    }

    public int getSpeed() {
        return speed;
    }
}
