package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Triangle;

import java.util.List;

public class BVHNode {
    private AABB aabb;
    private int leftFirst, triCount;
    private int N = 64;
    private Triangle[] tri = new Triangle[N];
    private int[] triIdx = new int[N];
    private BVHNode[] bvhNode = new BVHNode[N * 2];
    private int rootNodeIdx = 0, nodesUsed = 1;

    public BVHNode(List<Triangle> triangleList) {
        for (int i = 0; i < N; i++) {
            tri[i] = triangleList.get(i);
        }
    }

    private void buildBVH() {
        for (int i = 0; i < N; i++) triIdx[i] = i;
        for (int i = 0; i < N; i++)
            tri[i].setCentroid(new Vector3f(tri[i].getV1()).add(new Vector3f(tri[i].getV2())).add(new Vector3f(tri[i].getV3())).mul(0.3333f));
        BVHNode root = bvhNode[rootNodeIdx];
        root.leftFirst = 0;
        root.triCount = N;
        updateNodeBounds(rootNodeIdx);
        subdivide(rootNodeIdx);
    }

    private void updateNodeBounds(int nodeIdx) {
        BVHNode node = bvhNode[nodeIdx];
        node.aabb.min = new Vector3f(Float.MAX_VALUE);
        node.aabb.max = new Vector3f(Float.MIN_VALUE);
        int first = node.leftFirst;
        for (int i = 0; i < node.triCount; i++) {
            int leafTriIdx = triIdx[first + i];
            Triangle leafTri = tri[leafTriIdx];
            node.aabb.min.x = Math.min(node.aabb.min.x, leafTri.getV1().x);
            node.aabb.min.y = Math.min(node.aabb.min.y, leafTri.getV1().y);
            node.aabb.min.z = Math.min(node.aabb.min.z, leafTri.getV1().z);

            node.aabb.min.x = Math.min(node.aabb.min.x, leafTri.getV2().x);
            node.aabb.min.y = Math.min(node.aabb.min.y, leafTri.getV2().y);
            node.aabb.min.z = Math.min(node.aabb.min.z, leafTri.getV2().z);

            node.aabb.min.x = Math.min(node.aabb.min.x, leafTri.getV3().x);
            node.aabb.min.y = Math.min(node.aabb.min.y, leafTri.getV3().y);
            node.aabb.min.z = Math.min(node.aabb.min.z, leafTri.getV3().z);


            node.aabb.max.x = Math.max(node.aabb.max.x, leafTri.getV1().x);
            node.aabb.max.y = Math.max(node.aabb.max.y, leafTri.getV1().y);
            node.aabb.max.z = Math.max(node.aabb.max.z, leafTri.getV1().z);

            node.aabb.max.x = Math.max(node.aabb.max.x, leafTri.getV2().x);
            node.aabb.max.y = Math.max(node.aabb.max.y, leafTri.getV2().y);
            node.aabb.max.z = Math.max(node.aabb.max.z, leafTri.getV2().z);

            node.aabb.max.x = Math.max(node.aabb.max.x, leafTri.getV3().x);
            node.aabb.max.y = Math.max(node.aabb.max.y, leafTri.getV3().y);
            node.aabb.max.z = Math.max(node.aabb.max.z, leafTri.getV3().z);
        }
    }

    private void subdivide(int nodeIdx) {
        BVHNode node = bvhNode[nodeIdx];
        if (node.triCount <= 2) return;
        Vector3f extent = new Vector3f(node.aabb.max).sub(node.aabb.min);
        int axis = 0;
        if (extent.y > extent.x) axis = 1;
        if (extent.z > extent.get(axis)) axis = 2;
        float splitPos = node.aabb.min.get(axis) + extent.get(axis) * 0.5f;
        int i = node.leftFirst;
        int j = i + node.triCount - 1;
        while (i <= j) {
            if (tri[triIdx[i]].getCentroid().get(axis) < splitPos) {
                i++;
            } else {
                int tempA = triIdx[i];
                int tempB = triIdx[j--];
                triIdx[i] = tempB;
                triIdx[j--] = tempA;
            }
        }
        int leftCount = i - node.leftFirst;
        if (leftCount == 0 || leftCount == node.triCount) return;
        int leftChildIdx = nodesUsed++;
        int rightChildIdx = nodesUsed++;
        bvhNode[leftChildIdx].leftFirst = node.leftFirst;
        bvhNode[leftChildIdx].triCount = leftCount;
        bvhNode[rightChildIdx].leftFirst = i;
        bvhNode[rightChildIdx].triCount = node.triCount - leftCount;
        node.leftFirst = leftChildIdx;
        node.triCount = 0;
        updateNodeBounds(leftChildIdx);
        updateNodeBounds(rightChildIdx);
        subdivide(leftChildIdx);
        subdivide(rightChildIdx);
    }

    private boolean isLeaf() {
        return triCount > 0;
    }

    public AABB getAABB() {
        return aabb;
    }

    public int getTriCount() {
        return triCount;
    }

    public int[] getTriIdx() {
        return triIdx;
    }

    public Triangle[] getTri() {
        return tri;
    }

    public int getLeftFirst() {
        return leftFirst;
    }

    public BVHNode[] getBVHNode() {
        return bvhNode;
    }
}