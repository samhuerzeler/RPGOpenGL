package game.gameobject.statobject.mob.normal;

import game.Item;
import game.gameobject.statobject.mob.Enemy;
import game.item.equippableitem.head.HelmOfCommand;

public class Orc extends Enemy {

    public Orc(float x, float y, float z, int level) {
        super(level);
        name = "Orc";
        size = 32.0f;
        attackRange = size;
        Item[] items = new Item[]{
            new HelmOfCommand()
        };
        addToLootPool(items);
        init(x, y, z, 1.0f, 0.1f, 0.1f, size, size, size);
    }
}
