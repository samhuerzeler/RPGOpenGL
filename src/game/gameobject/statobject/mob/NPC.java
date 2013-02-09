package game.gameobject.statobject.mob;

import game.Sprite;
import game.gameobject.statobject.Mob;

public abstract class NPC extends Mob {

    public NPC(int level) {
        super(level);
        enemyTypeId = ENEMY_ID;
    }

    @Override
    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz, int type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.spawnX = x;
        this.spawnY = y;
        this.spawnZ = z;
        this.type = NPC_ID;
        this.spr = new Sprite(r, g, b, sx, sy, sz);
    }
}
