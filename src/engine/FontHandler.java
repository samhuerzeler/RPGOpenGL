package engine;

import game.Game;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public class FontHandler {

    private UnicodeFont font;

    public FontHandler(int size) {
        Font awtFont = new Font("Arial", Font.BOLD, size);
        font = new UnicodeFont(awtFont);
        font.getEffects().add(new ColorEffect(java.awt.Color.white));
        font.addAsciiGlyphs();
        try {
            font.loadGlyphs();
        } catch (SlickException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FontHandler() {
        this(10);
    }

    public void drawString(float x, float y, String str) {
        glDisable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);

        font.drawString(x, y, str);

        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);

        if (Main.LIGHTING) {
            glEnable(GL_LIGHTING);
        }
    }
}
