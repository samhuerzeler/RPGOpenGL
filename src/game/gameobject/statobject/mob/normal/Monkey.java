package game.gameobject.statobject.mob.normal;

import game.Item;
import game.gameobject.statobject.mob.Enemy;
import game.item.equippableitem.weapon.Bow;

public class Monkey extends Enemy {

    public Monkey(float x, float y, float z, int level) {
        super(level);
        name = "Monkey";
        size = 32.0f;
        attackRange = size;
        Item[] items = new Item[]{
            new Bow()
        };
        addToLootPool(items);
        init(x, y, z, 0.1f, 1.0f, 0.25f, size, size, size);
        loadModel("res/models/monkey.obj");
    }
}
