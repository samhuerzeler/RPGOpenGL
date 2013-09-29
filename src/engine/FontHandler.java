package engine;

import java.awt.Font;
import org.newdawn.slick.TrueTypeFont;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

public class FontHandler {

    private enum type {

        VERDANA, ARIAL, CALIBRI
    }
    private TrueTypeFont font;

    public FontHandler(int size) {
        Font awtFont = new Font(type.ARIAL.toString(), Font.BOLD, size);
        font = new TrueTypeFont(awtFont, false);
    }

    public FontHandler() {
        // load a default java font
        this(10);
    }

    public TrueTypeFont getFont() {
        return font;
    }

    public void drawString(float x, float y, String str, Color color) {
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        font.drawString(x, y, str, color);
        glEnable(GL_LIGHTING);
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }
}
