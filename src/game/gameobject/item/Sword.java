package game.gameobject.item;

public class Sword extends EquippableItem {

    public static final float SIZE = 32;
    private int damage;

    public Sword(float x, float y, float z) {
        init(x, y, z, 1.0f, 0.5f, 0, SIZE, SIZE, SIZE, "Sword", WEAPON_SLOT);
        damage = 2;
    }
}
