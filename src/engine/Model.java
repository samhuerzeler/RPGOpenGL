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
                float multiplier = -1 * (1 - ((float) delay.getCurrentTime() / (float) delay.getLength()));

                Vector3f n1 = normals.get((int) face.normal.x - 1);
                glNormal3f(n1.x, n1.y, n1.z);
                Vector3f v1 = vertices.get((int) face.vertex.x - 1);
                Vector3f v1Next = nextModel.vertices.get((int) nextModel.faces.get(i).vertex.x - 1);
                float v1DeltaX = (v1.x - v1Next.x) * multiplier;
                float v1DeltaY = (v1.y - v1Next.y) * multiplier;
                float v1DeltaZ = (v1.z - v1Next.z) * multiplier;
                glVertex3f(v1.x * stretchFactor + v1DeltaX * stretchFactor,
                        v1.y * stretchFactor + v1DeltaY * stretchFactor,
                        v1.z * stretchFactor + v1DeltaZ * stretchFactor);

                Vector3f n2 = normals.get((int) face.normal.y - 1);
                glNormal3f(n2.x, n2.y, n2.z);
                Vector3f v2 = vertices.get((int) face.vertex.y - 1);
                Vector3f v2Next = nextModel.vertices.get((int) nextModel.faces.get(i).vertex.y - 1);
                float v2DeltaX = (v2.x - v2Next.x) * multiplier;
                float v2DeltaY = (v2.y - v2Next.y) * multiplier;
                float v2DeltaZ = (v2.z - v2Next.z) * multiplier;
                glVertex3f(v2.x * stretchFactor + v2DeltaX * stretchFactor,
                        v2.y * stretchFactor + v2DeltaY * stretchFactor,
                        v2.z * stretchFactor + v2DeltaZ * stretchFactor);

                Vector3f n3 = normals.get((int) face.normal.z - 1);
                glNormal3f(n3.x, n3.y, n3.z);
                Vector3f v3 = vertices.get((int) face.vertex.z - 1);
                Vector3f v3Next = nextModel.vertices.get((int) nextModel.faces.get(i).vertex.z - 1);
                float v3DeltaX = (v3.x - v3Next.x) * multiplier;
                float v3DeltaY = (v3.y - v3Next.y) * multiplier;
                float v3DeltaZ = (v3.z - v3Next.z) * multiplier;
                glVertex3f(v3.x * stretchFactor + v3DeltaX * stretchFactor,
                        v3.y * stretchFactor + v3DeltaY * stretchFactor,
                        v3.z * stretchFactor + v3DeltaZ * stretchFactor);

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
