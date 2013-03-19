package engine;

import game.Sprite;

public class Frame {

    private int length;
    private Sprite sprite;
    private int numDisplayed;

    public Frame(Sprite spr, int length) {
        this.sprite = spr;
        this.length = length;
        numDisplayed = 0;
    }

    public boolean render() {
        sprite.render();
        numDisplayed++;
        if (numDisplayed >= length) {
            numDisplayed = 0;
            return true;
        }
        return false;
    }
}
