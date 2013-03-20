package game;

import java.util.Random;

public class RPGRandom {

    private static Random random;

    public static void initRandom() {
        random = new Random();
    }

    public static int nextInt() {
        return random.nextInt();
    }

    public static double nextDouble() {
        return random.nextDouble();
    }

    public static int nextInt(int cap) {
        return (int) ((random.nextDouble()) * (double) cap);
    }

    public static double nextDouble(double cap) {
        return (random.nextDouble()) * cap;
    }
}
