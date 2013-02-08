package game.gameobject.item.equippableitem.head;

import game.gameobject.item.equippableitem.Head;

public class HelmOfCommand extends Head {

    public HelmOfCommand(float x, float y, float z) {
        id = 2;
        armor = 200;
        size = 32;
        init(x, y, z, 1.0f, 0.5f, 0, size, size, size, "Helm of Command", HEAD_SLOT);
    }
}
