package game.gameobject.statobject.mob;

import game.Sprite;
import game.gameobject.statobject.Mob;
import java.util.ArrayList;

public abstract class Enemy extends Mob {

    protected ArrayList<Integer> lootPool = new ArrayList<Integer>();

    public Enemy(int level) {
        super(level);
        enemyTypeId = PLAYER_ID;
    }

    protected void addToLootPool(int id) {
        this.lootPool.add(id);
    }

    protected void addToLootPool(int[] ids) {
        for (int id : ids) {
            this.lootPool.add(id);
        }
    }

    @Override
    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz, int type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.spawnX = x;
        this.spawnY = y;
        this.spawnZ = z;
        this.type = ENEMY_ID;
        this.spr = new Sprite(r, g, b, sx, sy, sz);
    }
}
