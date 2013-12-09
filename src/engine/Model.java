package engine;

import game.Delay;
import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public class Model {

    public List<Vector3f> vertices = new ArrayList<>();
    public List<Vector3f> normals = new ArrayList<>();
    public List<Face> faces = new ArrayList<>();
    private int modelDisplayList;
    private int stretchFactor = 20;

    public Model() {
    }

    public void init() {
        modelDisplayList = glGenLists(1);
        glNewList(modelDisplayList, GL_COMPILE);
        {
            glBegin(GL_TRIANGLES);
            for (Face face : faces) {
                Vector3f n1 = normals.get((int) face.normal.x - 1);
                glNormal3f(n1.x, n1.y, n1.z);
                Vector3f v1 = vertices.get((int) face.vertex.x - 1);
                glVertex3f(v1.x * stretchFactor, v1.y * stretchFactor, v1.z * stretchFactor);
                Vector3f n2 = normals.get((int) face.normal.y - 1);
                glNormal3f(n2.x, n2.y, n2.z);
                Vector3f v2 = vertices.get((int) face.vertex.y - 1);
                glVertex3f(v2.x * stretchFactor, v2.y * stretchFactor, v2.z * stretchFactor);
                Vector3f n3 = normals.get((int) face.normal.z - 1);
                glNormal3f(n3.x, n3.y, n3.z);
                Vector3f v3 = vertices.get((int) face.vertex.z - 1);
                glVertex3f(v3.x * stretchFactor, v3.y * stretchFactor, v3.z * stretchFactor);
            }
            glEnd();
        }
        glEndList();
    }

    public void interpolate(Model nextModel, Delay delay) {
        modelDisplayList = glGenLists(1);
        glNewList(modelDisplayList, GL_COMPILE);
        {
            glBegin(GL_TRIANGLES);
            int i = 0;
            for (Face face : faces) {
                float multiplier = (float) delay.getCurrentTime() / (float) delay.getLength();

                Vector3f n1 = normals.get((int) face.normal.x - 1);
                glNormal3f(n1.x, n1.y, n1.z);
                Vector3f v1 = vertices.get((int) face.vertex.x - 1);
                Vector3f v1Next = nextModel.vertices.get((int) nextModel.faces.get(i).vertex.x - 1);
                float v1Delta = (v1Next.y * multiplier) - v1.y;
                glVertex3f(v1.x * stretchFactor, (v1.y * stretchFactor) + (v1Delta * stretchFactor), v1.z * stretchFactor);

                Vector3f n2 = normals.get((int) face.normal.y - 1);
                glNormal3f(n2.x, n2.y, n2.z);
                Vector3f v2 = vertices.get((int) face.vertex.y - 1);
                Vector3f v2Next = nextModel.vertices.get((int) nextModel.faces.get(i).vertex.y - 1);
                float v2Delta = (v2Next.y * multiplier) - v2.y;
                glVertex3f(v2.x * stretchFactor, (v2.y * stretchFactor) + (v2Delta * stretchFactor), v2.z * stretchFactor);

                Vector3f n3 = normals.get((int) face.normal.z - 1);
                glNormal3f(n3.x, n3.y, n3.z);
                Vector3f v3 = vertices.get((int) face.vertex.z - 1);
                Vector3f v3Next = nextModel.vertices.get((int) nextModel.faces.get(i).vertex.z - 1);
                float v3Delta = (v3Next.y * multiplier) - v3.y;
                glVertex3f(v3.x * stretchFactor, (v3.y * stretchFactor) + (v3Delta * stretchFactor), v3.z * stretchFactor);

                ++i;
            }
            glEnd();
        }
        glEndList();
    }

    public void render() {
        glCallList(modelDisplayList);
    }
}
