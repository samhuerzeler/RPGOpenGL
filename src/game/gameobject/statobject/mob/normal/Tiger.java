package game.gameobject.statobject.mob.normal;

import game.Item;
import game.gameobject.statobject.mob.Enemy;
import game.item.equippableitem.head.HelmOfCommand;
import game.item.equippableitem.weapon.Sword;

public class Tiger extends Enemy {

    public Tiger(float x, float y, float z, int level) {
        super(level);
        name = "Tiger";
        size = 32.0f;
        attackRange = size;
        Item[] items = new Item[]{
            new HelmOfCommand(),
            new Sword()
        };
        addToLootPool(items);
        init(x, y, z, 1.0f, 0.1f, 0.1f, size, size, size);
        loadModel("res/models/monkey.obj");
    }
}
