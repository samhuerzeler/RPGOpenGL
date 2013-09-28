package game.gameobject.statobject.player;

public class Ability {

    public enum coolDownType {

        GLOBAL, NON_GLOBAL
    }

    public enum abilityType {

        OFFENSIVE, DEFENSIVE, BUFF
    }
    protected static final int MELEE_RANGE = 5;
    protected static final int CAST_RANGE = 60;
    protected int id;
    protected String abilityName;
    protected String tooltip;
    protected int resourceConsumption;
    protected float range;
    protected float minValue;
    protected float maxValue;
    protected coolDownType cdType;
    protected abilityType aType;

    public Ability() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return abilityName;
    }

    public float getRange() {
        return range;
    }

    public int getResourceConsumption() {
        return resourceConsumption;
    }

    public int getValue() {
        return (int) (minValue + maxValue) / 2;
    }

    public coolDownType getCoolDownType() {
        return cdType;
    }

    public abilityType getAbilityType() {
        return aType;
    }
}
