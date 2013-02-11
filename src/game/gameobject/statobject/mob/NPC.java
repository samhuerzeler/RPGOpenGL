package game.gameobject.statobject.mob;

import game.Sprite;
import game.gameobject.statobject.Mob;

public abstract class NPC extends Mob {

    public NPC(int level) {
        super(level);
        enemyTypeId = ENEMY;
    }

    @Override
    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz) {
        this.x = x;
        this.y = y;
        this.z = z;
        spawnX = x;
        spawnY = y;
        spawnZ = z;
        type = NPC;
        spr = new Sprite(r, g, b, sx, sy, sz);
    }
}
