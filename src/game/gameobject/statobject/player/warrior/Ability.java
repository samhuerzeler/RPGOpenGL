package game.gameobject.statobject.player.warrior;

public class Ability {

    protected static final int MELEE_RANGE = 5;
    protected static final int CAST_RANGE = 60;
    protected int id;
    protected String name;
    protected float attackRange;

    public Ability() {
        id = 0;
        name = null;
        attackRange = 0;
    }

    public Ability find(String str) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Ability a = (Ability) Class.forName("game.gameobject.statobject.player.warrior.ability." + str).newInstance();
        return a;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getAttackRange() {
        return attackRange;
    }
}
