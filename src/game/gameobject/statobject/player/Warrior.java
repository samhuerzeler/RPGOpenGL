package game.gameobject.statobject.player;

import game.Stats;
import game.gameobject.statobject.Player;
import game.gameobject.statobject.player.warrior.HeroicStrike;
import game.gameobject.statobject.player.warrior.Slam;
import org.lwjgl.input.Keyboard;

public class Warrior extends Player {

    private final Ability HEROIC_STRIKE = new HeroicStrike();
    private final Ability SLAM = new Slam();

    public Warrior(float x, float y, float z) {
        super(x, y, z);
        playerCls = Player.playerClass.WARRIOR;
        resource = Stats.resource.RAGE;
        nonGcdDelay.start();
    }

    @Override
    public void getInput() {
        super.getInput();

        // Hotbars
        if (input.keyPressed(Keyboard.KEY_1)) {
            useAbility(HEROIC_STRIKE);
        } else if (input.keyPressed(Keyboard.KEY_2)) {
            useAbility(SLAM);
        }
    }
}