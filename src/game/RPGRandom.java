package game;

import java.util.Random;

public class RPGRandom extends Random {

    private long[] state = new long[16];
    private int index = 0;

    public RPGRandom() {
        Random random = new Random();
        seed(random.nextInt());
    }

    public RPGRandom(int seed) {
        seed(seed);
    }

    private void seed(int seed) {
        seed = Math.abs(seed);
        for (int i = 0; i < 16; i++) {
            state[i] = (seed + 1) * ((seed + 1) << 2) * i;
        }
    }

    @Override
    protected int next(int nbits) {
        long a, b, c, d;
        a = state[index];
        c = state[(index + 13) & 15];
        b = a ^ c ^ (a << 16) ^ (c << 15);
        c = state[(index + 9) & 15];
        c ^= (c >> 11);
        a = state[index] = b ^ c;
        d = a ^ ((a << 5) & 0xDA442D24L);
        index = (index + 15) & 15;
        a = state[index];
        state[index] = a ^ b ^ d ^ (a << 2) ^ (b << 18) ^ (c << 28);
        return (int) state[index];
    }
}
