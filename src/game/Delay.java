package game;

import java.util.Random;

public class Delay {

    private int length;
    private int minLength;
    private int maxLength;
    private double endTime;
    private boolean started;
    private Random random = new Random();
    private boolean randomized = false;

    public Delay(int length) {
        started = false;
        this.length = length;
    }

    public Delay(int minLength, int maxLength) {
        started = false;
        randomized = true;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.length = getRandomLength();
    }

    private int getRandomLength() {
        return random.nextInt(maxLength) + minLength;
    }

    public void start() {
        started = true;
        endTime = 0;
    }

    public void restart() {
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

    public double getCurrentTime() {
        return length - ((endTime - Time.getTime()) / 1000000.0d);
    }

    public double getLength() {
        return length;
    }
}
