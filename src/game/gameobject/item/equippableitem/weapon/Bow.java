package game.gameobject.item.equippableitem.weapon;

import game.gameobject.item.equippableitem.Weapon;

public class Bow extends Weapon {

    public static final float SIZE = 32;

    public Bow(float x, float y, float z) {
        init(x, y, z, 1.0f, 0.5f, 0, SIZE, SIZE, SIZE, "Bow", WEAPON_SLOT);
        damage = 2;
    }
}
