package game.item.equippableitem.weapon;

import game.item.equippableitem.Weapon;

public class Sword extends Weapon {

    public Sword() {
        id = 0;
        damage = 3;
        speed = 1000;
        init("Sword", WEAPON_SLOT);
    }
}
