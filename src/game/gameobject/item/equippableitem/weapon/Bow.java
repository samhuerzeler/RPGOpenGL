package game.gameobject.item.equippableitem.weapon;

import game.gameobject.item.equippableitem.Weapon;

public class Bow extends Weapon {

    public Bow(float x, float y, float z) {
        id = 1;
        damage = 2;
        speed = 1200;
        range = CAST_RANGE;
        size = 32;
        init(x, y, z, 1.0f, 0.5f, 0, size, size, size, "Bow", WEAPON_SLOT);
    }
}
