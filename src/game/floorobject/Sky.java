package game.floorobject;

import game.FloorObject;

public class Sky extends FloorObject {

    public Sky() {
    }

    @Override
    public float getHeight(float x, float z) {
        return 32768;
    }

    @Override
    public boolean inBound(float x, float z) {
        return true;
    }
}
