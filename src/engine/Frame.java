package engine;

import game.Sprite;

public class Frame {

    private int length;
    private Sprite spr;
    private int numDisplayed;

    public Frame(Sprite spr, int length) {
        this.spr = spr;
        this.length = length;
        numDisplayed = 0;
    }

    public boolean render() {
        spr.render();
        numDisplayed++;
        if (numDisplayed >= length) {
            numDisplayed = 0;
            return true;
        }
        return false;
    }
}
