package engine;

import game.Delay;
import game.gameobject.statobject.Player;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Animation {

    private ArrayList<Model> keyFrames;
    private Delay delay = new Delay(500);
    private int curFrame = 0;

    public Animation() {
        keyFrames = new ArrayList<>();
        delay.start();
    }

    public void render() {
        Model currentModel = keyFrames.get(curFrame);
        Model nextModel = keyFrames.get((curFrame + 1) % keyFrames.size());
        Model m = currentModel;
        if (!delay.isOver()) {
            m.interpolate(nextModel, delay);
        }
        m.render();
        if (delay.isOver()) {
            delay.restart();
            curFrame++;
            curFrame %= keyFrames.size();
        }
    }

    public void reset() {
        curFrame = 0;
        delay.start();
    }

    public ArrayList<Model> loadKeyFrames(String path, String fileName) {
        keyFrames = new ArrayList<>();
        int count = new File(path).list().length;
        for (int i = 0; i < count; ++i) {
            Model m;
            String unpaddedExt = Integer.toString(i + 1);
            String paddedExt = "000000".substring(unpaddedExt.length()) + unpaddedExt;
            try {
                m = ModelLoader.loadModel(new File(path.concat(fileName) + "_" + paddedExt + ".obj"));
                m.init();
                keyFrames.add(m);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return keyFrames;
    }
}
