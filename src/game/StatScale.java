package game;

public class StatScale {

    public static final int NUM_STATS = 8;
    public static final double MIN_STAT_SCALE = 0.125;
    public static final double MAX_SCALE_BONUS = 0.25;
    private double[] scales;
    private double[] scaleBonus;
    private RPGRandom random = new RPGRandom();

    public StatScale() {
        scales = new double[NUM_STATS];
        scaleBonus = new double[NUM_STATS];

        scales[Stats.VITALITY] = 1;
        scales[Stats.STRENGTH] = 1;
        scales[Stats.DEXTERITY] = 1;
        scales[Stats.INTELLIGENCE] = 1;
        scales[Stats.SPEED] = 1;
        scales[Stats.DEFENSE] = 1;
        scales[Stats.DODGE] = 1;
        scales[Stats.BLOCK] = 1;
    }

    public void generateScale() {
        double sum = 0;
        for (int i = 0; i < NUM_STATS; i++) {
            double value = RPGRandom.nextDouble(1);
            scales[i] = value;
            sum += value * value;
        }
        sum = Math.sqrt(sum);
        // normalize scales
        for (int i = 0; i < NUM_STATS; i++) {
            scales[i] /= sum;
            if (scales[i] < MIN_STAT_SCALE) {
                generateScale();
                return;
            }
        }
    }

    public double getScale(int stat) {
        return scales[stat] + (scaleBonus[stat] * MAX_SCALE_BONUS);
    }

    public void addBonus(int stat, double bonus) {
        if (bonus > 1) {
            bonus = 1;
        }
        if (bonus < 0) {
            bonus = 0;
        }
        scaleBonus[stat] = bonus;
    }
}
