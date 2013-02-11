package game.gameobject.statobject.mob.normal;

import game.gameobject.statobject.mob.Enemy;

public class Goblin extends Enemy {

    public Goblin(float x, float y, float z, int level) {
        super(level);
        name = "Goblin";
        size = 32.0f;
        attackRange = size;
        int[] itemIds = new int[]{0, 1};
        addToLootPool(itemIds);
        init(x, y, z, 0.1f, 1.0f, 0.25f, size, size, size, ENEMY_ID);
    }
}
