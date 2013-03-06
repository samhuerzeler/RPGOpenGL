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

    public EquippableItem[] getItems() {
        return items;
    }

    public Weapon getEquippedWeapon() {
        return (Weapon) items[EquippableItem.WEAPON_SLOT];
    }
}
