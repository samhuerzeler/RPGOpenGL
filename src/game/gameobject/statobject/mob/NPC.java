package game.gameobject.statobject.mob;

import game.Sprite;
import game.gameobject.statobject.Mob;

public abstract class NPC extends Mob {

    public NPC(int level) {
        super(level);
        enemyTypeId = HOSTILE;
    }

    @Override
    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz) {
        position.x = x;
        position.y = y;
        position.z = z;
        spawnPosition.x = x;
        spawnPosition.y = y;
        spawnPosition.z = z;
        type = FRIENDLY;
        sprite = new Sprite(r, g, b, sx, sy, sz);
    }
}
