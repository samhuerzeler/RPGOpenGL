package game.gameobject.item;

public class Bow extends EquippableItem {

    public static final float SIZE = 32;
    private int damage;

    public Bow(float x, float y, float z) {
        init(x, y, z, 1.0f, 0.5f, 0, SIZE, SIZE, SIZE, "Bow", WEAPON_SLOT);
        damage = 3;
    }
}
