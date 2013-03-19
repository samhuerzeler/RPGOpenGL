package game;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import org.lwjgl.util.glu.Sphere;

public class Util {

    public static boolean isInLineOfSight(GameObject go1, GameObject go2) {
        return true;
    }

    public static float dist(float x1, float z1, float x2, float z2) {
        double x = x2 - x1;
        double z = z2 - z1;

        return (float) Math.sqrt((x * x) + (z * z));
    }

    public static void renderSphere(float x, float y, float z) {
        glTranslatef(x, y, z);
        Sphere s = new Sphere();
        s.setDrawStyle(GLU_POINT);
        s.draw(200, 50, 50);
    }

    public static void renderCircle(float cx, float cz, float r) {
        int numSegments = 50;
        float theta = (float) (2 * 3.1415926 / numSegments);
        float c = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float t;
        float xx = r;
        float zz = 0;
        glBegin(GL_LINE_LOOP);
        for (int i = 0; i < numSegments; i++) {
            glVertex3f(xx + cx, 0.0f, zz + cz);
            t = xx;
            xx = c * xx - s * zz;
            zz = s * t + c * zz;
        }
        glEnd();
    }
}
