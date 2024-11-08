package net.gavrix32.engine.graphics;

import net.gavrix32.engine.linearmath.Vector3f;
import net.gavrix32.engine.objects.Triangle;

import java.util.ArrayList;
import java.util.List;

// Thanks https://youtu.be/C1H4zIiCOaI?si=7Ljb703Gs02Vxgdt
public class BoundingVolumeHierarchy {
    private static final int MAX_DEPTH = 32;

    public List<Triangle> triangles = new ArrayList<>();
    public List<Node> nodes = new ArrayList<>();

    public void build(List<Triangle> triangles) {
        this.triangles = triangles;
        Node rootNode = new Node();
        rootNode.trianglesCount = triangles.size();
        for (Triangle triangle : triangles)
            rootNode.bounds.addTriangle(triangle);
        nodes.add(rootNode);
        split(rootNode, 0);
    }

    private void split(Node parent, int depth) {
        if (depth == MAX_DEPTH)
            return;

        // Поиск самой длинной оси ограничивающего объёма
        /*Vector3f boundsSize = parent.bounds.getSize();
        Vector3f boundsCentre = parent.bounds.getCentre();
        int splitAxis = boundsSize.x > Math.max(boundsSize.y, boundsSize.z) ? 0 : boundsSize.y > boundsSize.z ? 1 : 2;
        float splitPos = boundsCentre.get(splitAxis);*/

        SplitData splitData = chooseSplit(parent);
        if (splitData.cost >= nodeCost(parent.bounds.getSize(), parent.triangleStartIndex + parent.trianglesCount)) return;
        int splitAxis = splitData.axis;
        float splitPos = splitData.pos;

        Node firstChild = new Node();
        Node secondChild = new Node();
        firstChild.triangleStartIndex = parent.triangleStartIndex;
        secondChild.triangleStartIndex = parent.triangleStartIndex;

        // Распределние треугольников по дочерним узлам
        for (int i = parent.triangleStartIndex; i < parent.triangleStartIndex + parent.trianglesCount; i++) {
            boolean triangleInFirstChild = triangles.get(i).getCentre().get(splitAxis) < splitPos;
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

    private float nodeCost(Vector3f size, int numTriangles) {
        float halfArea = size.x * (size.y + size.z) + size.y * size.z;
        return halfArea * numTriangles;
    }

    class SplitData {
        int axis;
        float pos, cost;

        public SplitData(int axis, float pos, float cost) {
            this.axis = axis;
            this.pos = pos;
            this.cost = cost;
        }
    }

    private SplitData chooseSplit(Node node) {
        int testsPerAxis = 3;
        int bestAxis = 0;
        float bestPos = 0.0f;
        float bestCost = Float.POSITIVE_INFINITY;

        for (int axis = 0; axis < 3; axis++) {
            float boundsStart = node.bounds.min.get(axis);
            float boundsEnd = node.bounds.max.get(axis);

            for (int i = 0; i < testsPerAxis; i++) {
                float splitT = (i + 1) / (testsPerAxis + 1.0f);
                float pos = boundsStart + (boundsEnd - boundsStart) * splitT;
                float cost = evaluateSplit(node, axis, pos);
                if (cost < bestCost) {
                    bestAxis = axis;
                    bestPos = pos;
                    bestCost = cost;
                }
            }
        }
        return new SplitData(bestAxis, bestPos, bestCost);
    }

    private float evaluateSplit(Node node, int splitAxis, float splitPos) {
        BoundingBox firstBounds = new BoundingBox();
        BoundingBox secondBounds = new BoundingBox();
        int numInFirst = 0;
        int numInSecond = 0;

        for (int i = node.triangleStartIndex; i < node.triangleStartIndex + node.trianglesCount; i++) {
            Triangle triangle = triangles.get(i);
            if (triangle.getCentre().get(splitAxis) < splitPos) {
                firstBounds.addTriangle(triangle);
                numInFirst++;
            } else {
                secondBounds.addTriangle(triangle);
                numInSecond++;
            }
        }
        return nodeCost(firstBounds.getSize(), numInFirst) + nodeCost(secondBounds.getSize(), numInSecond);
    }
}