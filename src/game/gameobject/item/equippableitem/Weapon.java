package game.gameobject.item.equippableitem;

import game.gameobject.item.EquippableItem;

public class Weapon extends EquippableItem {

    protected int damage;
    protected int range;

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
}
