package game.gameobject.statobject.npc;

import game.gameobject.statobject.NPC;

public class Guard extends NPC {

    public static final int SIZE = 32;

    public Guard(float x, float y, float z, int level) {
        super(level);
        name = "Guard";
        init(x, y, z, 0.1f, 1f, 1f, SIZE, SIZE, SIZE, ENEMY_ID);
        setAttackDelay(500);
    }
}
