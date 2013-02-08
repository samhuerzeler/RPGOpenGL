package game.gameobject.item.equippableitem.weapon;

import game.gameobject.item.equippableitem.Weapon;

public class Sword extends Weapon {

    public Sword(float x, float y, float z) {
        id = 0;
        damage = 3;
        speed = 1000;
        range = MELEE_RANGE;
        size = 32;
        init(x, y, z, 1.0f, 0.5f, 0, size, size, size, "Sword", WEAPON_SLOT);
    }
}
