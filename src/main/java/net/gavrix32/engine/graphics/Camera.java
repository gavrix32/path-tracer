package net.gavrix32.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Camera {
    private Vector3f pos;
    private Matrix4f rotMatrix;
    private Quaternionf quaternion;

    public Camera() {
        pos = new Vector3f();
        quaternion = new Quaternionf();
    }

    public void update() {
        rotMatrix = new Matrix4f();
        rotMatrix.rotate(quaternion);
    }

    public void setPos(float x, float y, float z) {
        pos = new Vector3f(x, y, z);
    }

    public Vector3f getPos() {
        return pos;
    }

    public void move(float x, float y, float z) {
        pos.add(quaternion.positiveX(new Vector3f()).mul(x));
        pos.add(quaternion.positiveY(new Vector3f()).mul(y));
        pos.add(quaternion.positiveZ(new Vector3f()).mul(z));
    }

    public void rotate(float x, float y, float z) {
        quaternion.rotateLocalX(x);
        quaternion.rotateLocalY(y);
        quaternion.rotateLocalZ(z);
    }

    public Matrix4f getRotMatrix() {
        return rotMatrix;
    }

    protected Quaternionf getQuaternion() {
        return quaternion;
    }
}