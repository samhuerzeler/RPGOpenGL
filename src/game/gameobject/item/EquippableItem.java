package game.gameobject.item;

import game.Sprite;
import game.gameobject.Item;

public abstract class EquippableItem extends Item {

    public static final int NUM_SLOTS = 4;
    public static final int WEAPON_SLOT = 0;
    public static final int HEAD_SLOT = 1;
    public static final int BODY_SLOT = 2;
    public static final int LEG_SLOT = 3;
    private int slot;

    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz, String name, int slot) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = ITEM_ID;
        this.slot = slot;
        this.name = name;
        this.spr = new Sprite(r, g, b, sx, sy, sz);
    }

    public int getSlot() {
        return slot;
    }
}
