package game;

import game.item.EquippableItem;
import game.item.equippableitem.Weapon;

public class Equipment {

    private EquippableItem[] items;
    private Inventory inventory;

    public Equipment(Inventory inv) {
        this.inventory = inv;
        items = new EquippableItem[EquippableItem.NUM_SLOTS];
    }

    public boolean equip(EquippableItem item) {
        int slot = item.getSlot();
        if (items[slot] != null) {
            if (!deEquip(slot)) {
                return false;
            }
        }
        items[slot] = item;
        return true;
    }

    public boolean deEquip(int slot) {
        if (inventory.add(items[slot])) {
            items[slot] = null;
            return true;
        }
        return false;
    }

    public EquippableItem[] getItems() {
        return items;
    }

    public Weapon getEquippedWeapon() {
        return (Weapon) items[EquippableItem.WEAPON_SLOT];
    }
}
