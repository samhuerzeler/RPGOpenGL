package game.gameobject.statobject.player;

import game.Delay;
import game.gameobject.statobject.Player;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Keyboard;
import util.Log;

public class Warrior extends Player {

    private String className = "warrior";
    private Ability ability = new Ability();
    private Delay abilityDelay = new Delay(1500);

    public Warrior(float x, float y, float z) {
        super(x, y, z);
        abilityDelay.start();
    }

    @Override
    public void getInput() {
        super.getInput();

        // Hotbars
        try {
            if (input.keyPressed(Keyboard.KEY_1)) {
                useAbility(ability.find("HeroicStrike", className));
            } else if (input.keyPressed(Keyboard.KEY_2)) {
                useAbility(ability.find("ShieldSlam", className));
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Warrior.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Warrior.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Warrior.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void useAbility(Ability a) {
        if (abilityDelay.isOver()) {
            Log.p("ability used: " + a.getName());
            abilityDelay.restart();
        }
    }
}
