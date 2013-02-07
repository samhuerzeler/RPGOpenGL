package game.gameobject.item.equippableitem.head;

import game.gameobject.item.equippableitem.Head;

public class HelmOfCommand extends Head {

    public static final float SIZE = 32;

    public HelmOfCommand(float x, float y, float z) {
        init(x, y, z, 1.0f, 0.5f, 0, SIZE, SIZE, SIZE, "Helm of Command", HEAD_SLOT);
        id = 2;
        armor = 200;
    }
}
