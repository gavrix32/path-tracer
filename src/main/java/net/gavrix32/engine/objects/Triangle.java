package net.gavrix32.engine.objects;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.math.Matrix4f;
import net.gavrix32.engine.math.Vector3f;

public class Triangle extends Shape {
    private Vector3f v1, v2, v3;
    private Matrix4f rotationMatrix;

    public Triangle(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f rot, Vector3f col, Material material) {
        super(new Vector3f(), rot, col, material);
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        rotationMatrix = new Matrix4f();
    }

    public Triangle(Vector3f v1, Vector3f v2, Vector3f v3) {
        super(new Vector3f(), new Vector3f(), new Vector3f(1), new Material(true, 0, 1, 1, false));
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        rotationMatrix = new Matrix4f();
    }

    public Triangle() {
        super(new Vector3f(), new Vector3f(), new Vector3f(1), new Material(true, 0, 1, 1, false));
        this.v1 = new Vector3f();
        this.v2 = new Vector3f();
        this.v3 = new Vector3f();
        rotationMatrix = new Matrix4f();
    }

    public Vector3f getV1() {
        return v1;
    }

    public void setV1(Vector3f v1) {
        this.v1 = v1;
    }

    public void setV1(float x, float y, float z) {
        this.v1 = new Vector3f(x, y, z);
    }

    public Vector3f getV2() {
        return v2;
    }

    public void setV2(Vector3f v2) {
        this.v2 = v2;
    }

    public void setV2(float x, float y, float z) {
        this.v2 = new Vector3f(x, y, z);
    }

    public Vector3f getV3() {
        return v3;
    }

    public void setV3(Vector3f v3) {
        this.v3 = v3;
    }

    public void setV3(float x, float y, float z) {
        this.v3 = new Vector3f(x, y, z);
    }

    public Matrix4f getRotationMatrix() {
        return rotationMatrix;
    }

    public void setRotationMatrix(Matrix4f rotationMatrix) {
        this.rotationMatrix = rotationMatrix;
    }
}