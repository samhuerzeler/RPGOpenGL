package game.gameobject.statobject.player.priest;

import game.gameobject.statobject.player.Ability;

public class Heal extends Ability {

    public Heal() {
        id = 101;
        abilityName = "Heal";
        resourceConsumption = 15;
        range = CAST_RANGE;
        minValue = 8;
        maxValue = 12;
        cdType = coolDownType.GLOBAL;
        aType = abilityType.DEFENSIVE;
        tooltip = "Heals between " + minValue + " and " + maxValue + ".";
    }
}
