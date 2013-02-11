package game.gameobject.item.equippableitem.weapon;

import game.gameobject.item.equippableitem.Weapon;

public class Sword extends Weapon {

    public Sword() {
        id = 0;
        damage = 3;
        speed = 1000;
        range = MELEE_RANGE;
        init("Sword", WEAPON_SLOT);
    }
}
