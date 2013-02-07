package game.gameobject.item.equippableitem.weapon;

import game.gameobject.item.equippableitem.Weapon;

public class Sword extends Weapon {

    public static final float SIZE = 32;

    public Sword(float x, float y, float z) {
        init(x, y, z, 1.0f, 0.5f, 0, SIZE, SIZE, SIZE, "Sword", WEAPON_SLOT);
        damage = 3;
    }
}
