package game.gameobject;

public class Goblin extends Enemy {

    public static final int SIZE = 32;

    public Goblin(float x, float y, float z, int level) {
        super(level);
        init(x, y, z, 0.1f, 1f, 0.25f, SIZE, SIZE, SIZE, ENEMY_ID);
        setAttackDelay(500);
    }
}
