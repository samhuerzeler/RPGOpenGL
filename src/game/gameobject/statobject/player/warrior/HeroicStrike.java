package game.gameobject.statobject.player.warrior;

import game.gameobject.statobject.player.Ability;

public class HeroicStrike extends Ability {

    public HeroicStrike() {
        id = 0;
        abilityName = "Heroic Strike";
        resourceConsumption = 20;
        range = MELEE_RANGE;
        minValue = 10;
        maxValue = 15;
        cdType = coolDownType.NON_GLOBAL;
        aType = abilityType.OFFENSIVE;
        tooltip = "Deals Damage between " + minValue + " and " + maxValue + ". Use only when you have too much rage.";
    }
}
