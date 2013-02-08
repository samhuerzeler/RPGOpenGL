package game.gameobject.statobject.enemy;

import game.gameobject.statobject.Enemy;

public class Goblin extends Enemy {

    public Goblin(float x, float y, float z, int level) {
        super(level);
        name = "Goblin";
        size = 32;
        attackRange = size + 2;
        init(x, y, z, 0.1f, 1.0f, 0.25f, size, size, size, ENEMY_ID);
    }
}
