package game.gameobject.item;

import engine.GameObject;
import engine.Sprite;

public class Item extends GameObject {

    protected String name;

    public String getName() {
        return name;
    }

    protected void init(float x, float y, float z, float r, float g, float b, float sx, float sy, float sz, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = ITEM_ID;
        this.name = name;
        this.spr = new Sprite(r, g, b, sx, sy, sz);
    }
}
