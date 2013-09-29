package game.gameobject.statobject.player.warrior;

import game.gameobject.statobject.player.Ability;

public class Slam extends Ability {

    public Slam() {
        id = 1;
        abilityName = "Slam";
        resourceConsumption = 8;
        range = MELEE_RANGE;
        minValue = 6;
        maxValue = 10;
        cdType = coolDownType.GLOBAL;
        aType = abilityType.OFFENSIVE;
        tooltip = "Deals Damage between " + minValue + " and " + maxValue + ".";
    }
}
