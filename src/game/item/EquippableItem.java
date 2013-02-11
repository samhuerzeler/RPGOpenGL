package game.item;

import game.Item;

public abstract class EquippableItem extends Item {

    public static final int NUM_SLOTS = 4;
    public static final int WEAPON_SLOT = 0;
    public static final int HEAD_SLOT = 1;
    public static final int BODY_SLOT = 2;
    public static final int LEG_SLOT = 3;
    private int slot;

    protected void init(String name, int slot) {
        this.name = name;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}
