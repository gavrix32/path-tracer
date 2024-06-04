package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Triangle;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private static final List<Vector3f> positions = new ArrayList<>();
    private static final List<Triangle> triangles = new ArrayList<>();
    private static float[] verticesData;

    public Model(String filepath, float scale) {
        AIScene scene = Assimp.aiImportFile(filepath, Assimp.aiProcess_Triangulate);
        PointerBuffer buffer = scene.mMeshes();
        for (int i = 0; i < buffer.limit(); i++) {
            AIMesh mesh = AIMesh.create(buffer.get(i));
            loadMesh(mesh, scale);
        }
    }

    private void loadMesh(AIMesh mesh, float scale) {
        List<Vector3f> vertices = new ArrayList<>();
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            Vector3f vector = new Vector3f(mesh.mVertices().get(i).x(), mesh.mVertices().get(i).y(), mesh.mVertices().get(i).z());
            vertices.add(vector.mul(scale));
        }

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            for (int j = 0; j < face.mNumIndices(); j++) {
                int index = face.mIndices().get(j);
                indices.add(index);
            }
        }

        for (int i : indices) {
            positions.add(vertices.get(i));
        }

        for (int i = 0; i < positions.size(); i += 3) {
            triangles.add(i / 3, new Triangle(positions.get(i), positions.get(i + 1), positions.get(i + 2)));
        }

        verticesData = new float[positions.size() * 4];
        int index = 0;
        for (Vector3f pos : positions) {
            verticesData[index++] = pos.x;
            verticesData[index++] = pos.y;
            verticesData[index++] = pos.z;
            verticesData[index++] = 1.0f;
        }
    }

    public float[] getVerticesData() {
        return verticesData;
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }
}