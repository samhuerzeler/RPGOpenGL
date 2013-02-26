package game;

import game.item.EquippableItem;

public class Inventory {

    private Item[] items;
    private int firstFree;

    public Inventory(int size) {
        items = new Item[size];
        firstFree = 0;
    }

    public boolean add(Item item) {
        if (firstFree == items.length) {
            return false;
        }

        items[firstFree] = item;
        firstFree++;
        for (int i = firstFree; i < items.length; i++) {
            if (items[i] == null) {
                firstFree = i;
                return true;
            }
        }
        firstFree = items.length;
        return true;
    }

    public void remove(int index) {
        items[index] = null;
        if (index < firstFree) {
            firstFree = index;
        }
    }

    public void remove(Item item) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == item) {
                remove(i);
                return;
            }
        }
    }

    public Item get(int index) {
        return items[index];
    }

    public EquippableItem findByName(String name) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && name.equals(items[i].getName())) {
                return (EquippableItem) items[i];
            }
        }
        return null;
    }

    public Item findById(int id) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getId() == id) {
                return items[i];
            }
        }
        return null;
    }
}
