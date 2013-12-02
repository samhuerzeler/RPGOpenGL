package game.floor;

import game.Floor;

public class VoidFloor extends Floor {

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
