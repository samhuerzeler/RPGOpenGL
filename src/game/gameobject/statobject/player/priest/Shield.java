package game.gameobject.statobject.player.priest;

import game.gameobject.statobject.player.Ability;

public class Shield extends Ability {

    public Shield() {
        id = 100;
        abilityName = "Shield";
        resourceConsumption = 8;
        range = CAST_RANGE;
        minValue = 6;
        maxValue = 10;
        cdType = coolDownType.GLOBAL;
        aType = abilityType.DEFENSIVE;
        tooltip = "Absorbs damage between " + minValue + " and " + maxValue + ".";
    }
}
