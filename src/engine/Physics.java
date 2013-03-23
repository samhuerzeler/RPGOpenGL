package engine;

import game.GameObject;
import game.Time;
import java.awt.Rectangle;

public class Physics {

    private static final float GRAVITY = -9.8f;
    private float fallingVelocity = 0.0f;

    public Physics() {
        fallingVelocity = 0.0f;
    }

    public float getFallingDistance() {
        float newVelocity;
        float yDistance;
        newVelocity = fallingVelocity + 0.05f * Time.getDelta();
        if (newVelocity < GRAVITY) {
            newVelocity = GRAVITY * Time.getDelta();
        }
        yDistance = (fallingVelocity + newVelocity) / 2;
        fallingVelocity = newVelocity;
        return yDistance * Time.getDelta();
    }

    public void resetFallingVelocity() {
        fallingVelocity = 0.0f;
    }
    
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
