package game.gameobject.statobject.enemy;

import game.gameobject.statobject.Enemy;

public class Orc extends Enemy {

    public Orc(float x, float y, float z, int level) {
        super(level);
        name = "Orc";
        size = 32;
        attackRange = size;
        init(x, y, z, 1.0f, 0.1f, 0.1f, size, size, size, ENEMY_ID);
    }
}
