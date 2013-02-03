package game.gameobject;

import game.gameobject.item.EquippableItem;

public class Equipment {

    private EquippableItem[] items;
    private Inventory inventory;

    public Equipment(Inventory inv) {
        this.inventory = inv;
        items = new EquippableItem[EquippableItem.NUM_SLOTS];
    }

    public boolean equip(EquippableItem item) {
        int index = item.getSlot();
        if (items[index] != null) {
            if (!deEquip(index)) {
                return false;
            }
        }
        items[index] = item;
        return true;
    }

    public boolean deEquip(int slot) {
        if (inventory.add(items[slot])) {
            items[slot] = null;
            return true;
        }
        return false;
    }
}
