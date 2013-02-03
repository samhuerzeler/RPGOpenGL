package engine;

import game.gameobject.GameObject;
import java.awt.Rectangle;

public class Physics {

    public static GameObject checkCollision(GameObject go1, GameObject go2) {
        return checkCollision(new Rectangle((int) go1.getX(), (int) go1.getZ(), (int) go1.getSX(), (int) go1.getSZ()), go2);
    }

    public static GameObject checkCollision(Rectangle r1, GameObject go2) {
        Rectangle r2 = new Rectangle((int) go2.getX(), (int) go2.getZ(), (int) go2.getSX(), (int) go2.getSZ());
        boolean res = r1.intersects(r2);
        if (res) {
            return go2;
        }
        return null;
    }
}
