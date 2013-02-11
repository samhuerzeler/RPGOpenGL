package game.gameobject.item.equippableitem.weapon;

import game.gameobject.item.equippableitem.Weapon;

public class Bow extends Weapon {

    public Bow() {
        id = 1;
        damage = 2;
        speed = 1200;
        range = CAST_RANGE;
        init("Bow", WEAPON_SLOT);
    }
}
