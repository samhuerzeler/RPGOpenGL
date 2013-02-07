
package game.gameobject.statobject.enemy;

import game.gameobject.statobject.Enemy;

public class Orc extends Enemy {

    public static final int SIZE = 32;

    public Orc(float x, float y, float z, int level) {
        super(level);
        name = "Goblin";
        init(x, y, z, 0.1f, 1f, 0.25f, SIZE, SIZE, SIZE, ENEMY_ID);
        setAttackDelay(500);
    }
}
