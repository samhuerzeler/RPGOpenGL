package game.gameobject.statobject.mob.normal;

import game.gameobject.statobject.mob.NPC;

public class Guard extends NPC {

    public Guard(float x, float y, float z, int level) {
        super(level);
        name = "Guard";
        size = 50.0f;
        attackRange = size;
        init(x, y, z, 0.1f, 1f, 1f, size, size, size);
        loadModel("res/models/monkey.obj");
    }
}
