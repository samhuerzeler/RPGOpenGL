package game.gameobject.item.equippableitem;

import game.gameobject.item.EquippableItem;

public class Weapon extends EquippableItem {
    
    protected int damage;
    
    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int amt) {
        damage = amt;
    }
}
