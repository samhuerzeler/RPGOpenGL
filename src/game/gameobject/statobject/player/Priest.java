package game.gameobject.statobject.player;

import game.Stats;
import game.gameobject.statobject.Player;
import game.gameobject.statobject.player.priest.Heal;
import game.gameobject.statobject.player.priest.Shield;
import org.lwjgl.input.Keyboard;

public class Priest extends Player {

    private final Ability HEAL = new Heal();
    private final Ability SHIELD = new Shield();

    public Priest(float x, float y, float z) {
        super(x, y, z);
        playerCls = Player.playerClass.PRIEST;
        resource = Stats.resource.MANA;
        nonGcdDelay.start();
    }

    @Override
    public void getInput() {
        super.getInput();

        // Hotbars
        if (input.keyPressed(Keyboard.KEY_1)) {
            useAbility(HEAL);
        } else if (input.keyPressed(Keyboard.KEY_2)) {
            useAbility(SHIELD);
        }
    }
}
