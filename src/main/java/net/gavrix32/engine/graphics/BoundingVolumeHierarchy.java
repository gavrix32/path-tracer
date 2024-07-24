package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Triangle;

import java.util.ArrayList;
import java.util.List;

public class BoundingVolumeHierarchy {
    private static final int MAX_DEPTH = 32;

    public List<Triangle> triangles = new ArrayList<>();
    public List<Node> nodes = new ArrayList<>();

    public void build(List<Triangle> triangles) {
        this.triangles = triangles;
        Node rootNode = new Node();
        rootNode.trianglesCount = triangles.size();
        for (Triangle triangle : triangles) {
            rootNode.bounds.addTriangle(triangle);
        }
        nodes.add(rootNode);
        split(rootNode, 0);
    }

    private void split(Node parent, int depth) {
        if (depth == MAX_DEPTH) return;

        // Поиск самой длинной оси ограничивающего объёма
        Vector3f boundsSize = parent.bounds.getSize();
        Vector3f boundsCentre = parent.bounds.getCentre();
        int splitAxis = boundsSize.x > Math.max(boundsSize.y, boundsSize.z) ? 0 : boundsSize.y > boundsSize.z ? 1 : 2;

        Node firstChild = new Node();
        Node secondChild = new Node();
        firstChild.triangleStartIndex = parent.triangleStartIndex;
        secondChild.triangleStartIndex = parent.triangleStartIndex;

        // Распределние треугольников по дочерним узлам
        for (int i = parent.triangleStartIndex; i < parent.triangleStartIndex + parent.trianglesCount; i++) {
            boolean triangleInFirstChild = triangles.get(i).getCentre().get(splitAxis) < boundsCentre.get(splitAxis);
            Node child = triangleInFirstChild ? firstChild : secondChild;
            child.bounds.addTriangle(triangles.get(i));
            child.trianglesCount++;
            if (triangleInFirstChild) {
                int swap = child.triangleStartIndex + child.trianglesCount - 1;
                Triangle temp = triangles.get(i);
                triangles.set(i, triangles.get(swap));
                triangles.set(swap, temp);
                secondChild.triangleStartIndex++;
            }
        }

        // Разделение ограничивающего объёма
        if (firstChild.trianglesCount > 0 && secondChild.trianglesCount > 0) {
            parent.childIndex = nodes.size();
            nodes.add(firstChild);
            nodes.add(secondChild);
            split(firstChild, depth + 1);
            split(secondChild, depth + 1);
        }
    }
}