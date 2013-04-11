package engine;

import java.util.ArrayList;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;

public class Model {

    public List<Vector3f> vertices = new ArrayList<Vector3f>();
    public List<Vector3f> normals = new ArrayList<Vector3f>();
    public List<Face> faces = new ArrayList<Face>();
    private int modelDisplayList;

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
                glVertex3f(v1.x, v1.y, v1.z);
                Vector3f n2 = normals.get((int) face.normal.y - 1);
                glNormal3f(n2.x, n2.y, n2.z);
                Vector3f v2 = vertices.get((int) face.vertex.y - 1);
                glVertex3f(v2.x, v2.y, v2.z);
                Vector3f n3 = normals.get((int) face.normal.z - 1);
                glNormal3f(n3.x, n3.y, n3.z);
                Vector3f v3 = vertices.get((int) face.vertex.z - 1);
                glVertex3f(v3.x, v3.y, v3.z);
            }
            glEnd();
        }
        glEndList();
    }

    public void render() {
        glCallList(modelDisplayList);
    }
}
