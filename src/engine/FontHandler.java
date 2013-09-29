package engine;

import static org.lwjgl.opengl.GL11.*;

public class FontHandler {

//    private enum type {
//
//        VERDANA, ARIAL, CALIBRI
//    }
    //private TrueTypeFont font;
    public FontHandler(int size) {
//        Font awtFont = new Font(type.ARIAL.toString(), Font.BOLD, size);
//        font = new TrueTypeFont(awtFont, false);
    }

    public FontHandler() {
        // load a default java font
        //this(10);
    }

//    public TrueTypeFont getFont() {
//        return font;
//    }
    public void drawString(float x, float y, String str) {
        glDisable(GL_LIGHTING);
        glColor3f(1, 1, 1);
        SimpleText.drawString(str, x, y);
        glEnable(GL_LIGHTING);

    }
}
