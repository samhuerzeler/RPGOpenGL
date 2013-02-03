package game;

import engine.GameObject;

public class Util {
    
    public static boolean lineOfSight(GameObject go1, GameObject go2) {
        return true;
    }
    
    public static float dist(float x1, float z1, float x2, float z2) {
        double x = x2 - x1;
        double z = z2 - z1;
        
        return (float) Math.sqrt((x * x) + (z * z));
    }
}
