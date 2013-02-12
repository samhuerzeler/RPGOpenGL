package game.gameobject.statobject.mob;

import game.Item;
import game.Sprite;
import game.gameobject.statobject.Mob;
import game.gameobject.statobject.Player;
import java.util.ArrayList;
import java.util.Arrays;
import util.Log;

public abstract class Enemy extends Mob {

    protected ArrayList<Item> lootPool = new ArrayList<Item>();

    public Enemy(int level) {
        super(level);
        enemyTypeId = PLAYER;
    }

    protected void addToLootPool(Item item) {
        this.lootPool.add(item);
    }

    protected void addToLootPool(Item[] items) {
        this.lootPool.addAll(Arrays.asList(items));
    }

    @Override
    protected void die() {
        for (Item loot : lootPool) {
            Log.p("Loot: " + loot.getName());
            if (target.getType() == PLAYER) {
                ((Player) target).addXp(20);
                ((Player) target).addItem(loot);

            }
        }
        remove();
    }

    @Override
    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz) {
        this.x = x;
        this.y = y;
        this.z = z;
        spawnX = x;
        spawnY = y;
        spawnZ = z;
        type = ENEMY;
        spr = new Sprite(r, g, b, sx, sy, sz);
    }
}
