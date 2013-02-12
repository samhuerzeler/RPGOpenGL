package game;

import java.util.Random;

public class Delay {

    private int length;
    private int min;
    private int max;
    private double endTime;
    private boolean started;
    private Random random = new Random();
    private boolean randomized = false;

    public Delay(int length) {
        started = false;
        this.length = length;
    }

    public Delay(int min, int max) {
        started = false;
        randomized = true;
        this.min = min;
        this.max = max;
        this.length = getRandomLength();
    }

    private int getRandomLength() {
        return random.nextInt(max) + min;
    }

    public void init() {
        started = true;
        endTime = 0;
    }

    public void start() {
        started = true;
        if (randomized) {
            this.length = getRandomLength();
        }
        endTime = length * 1000000.0d + Time.getTime();
    }

    public void stop() {
        started = false;
    }

    public boolean isOver() {
        if (!started) {
            return false;
        }
        return Time.getTime() > endTime;
    }

    public boolean isActive() {
        return started;
    }
}
