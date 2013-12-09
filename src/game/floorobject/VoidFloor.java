package game.floorobject;

import game.FloorObject;

public class VoidFloor extends FloorObject {

    public VoidFloor() {
    }

    @Override
    public float getHeight(float x, float z) {
        return 0;
    }

    @Override
    public boolean inBound(float x, float z) {
        return true;
    }
}
