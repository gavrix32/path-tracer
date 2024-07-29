package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Triangle;
import net.gavrix32.engine.utils.Logger;
import net.gavrix32.engine.utils.Utils;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private static final List<Triangle> triangles = new ArrayList<>();

    public Model(String filepath, float scale) {
        byte[] data = Utils.loadBytes(filepath);
        ByteBuffer dataBuffer = BufferUtils.createByteBuffer(data.length);
        dataBuffer.put(data).flip();
        AIScene scene = Assimp.aiImportFileFromMemory(dataBuffer, Assimp.aiProcess_Triangulate, "obj");
        PointerBuffer buffer = scene.mMeshes();
        for (int i = 0; i < scene.mNumMeshes(); i++) {
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

        int i = 0;
        while (i < indices.size()) {
            Vector3f v1 = vertices.get(indices.get(i++));
            Vector3f v2 = vertices.get(indices.get(i++));
            Vector3f v3 = vertices.get(indices.get(i++));
            triangles.add(new Triangle(v1, v2, v3));
        }
    }

    public List<Triangle> getTriangles() {
        return triangles;
    }
}