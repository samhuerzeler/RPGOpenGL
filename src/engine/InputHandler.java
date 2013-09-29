package engine;

import org.lwjgl.input.Keyboard;

public class InputHandler {

    private boolean[] locked = new boolean[256];
    private boolean[] pressed = new boolean[256];

    public void update() {
        int key = Keyboard.getEventKey();
        if (Keyboard.getEventKeyState()) {
            pressed[key] = true;
        } else {
            pressed[key] = false;
            release(key);
        }
    }

    public boolean keyPressed(int key) {
        return keyPressed(key, false);
    }

    public boolean keyPressed(int key, boolean hold) {
        if (!hold) {
            if (pressed[key] && !locked[key]) {
                lock(key);
                return pressed[key];
            }
            return false;
        }
        return pressed[key];
    }

    public void lock(int key) {
        locked[key] = true;
    }

    public void release(int key) {
        locked[key] = false;
    }
}