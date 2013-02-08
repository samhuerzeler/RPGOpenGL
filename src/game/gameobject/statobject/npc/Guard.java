package game.gameobject.statobject.npc;

import game.gameobject.statobject.NPC;

public class Guard extends NPC {

    public Guard(float x, float y, float z, int level) {
        super(level);
        name = "Guard";
        size = 32;
        init(x, y, z, 0.1f, 1f, 1f, size, size, size, ENEMY_ID);
    }
}
