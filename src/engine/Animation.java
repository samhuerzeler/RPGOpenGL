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
    private int currentFrame = 0;
    private Delay delay = new Delay(500);

    public Animation() {
        keyFrames = new ArrayList<>();
        delay.start();
    }

    public void render() {
        Model nextModel = keyFrames.get((currentFrame + 1) % keyFrames.size());
        Model currentModel = keyFrames.get(currentFrame);

        if (!delay.isOver()) {
            nextModel.interpolate(currentModel, delay);
        }
        nextModel.render();
        if (delay.isOver()) {
            delay.restart();
            currentFrame++;
            currentFrame %= keyFrames.size();
        }
    }

    public void reset() {
        currentFrame = 0;
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
