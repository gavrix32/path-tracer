package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    public Camera setPos(float x, float y, float z) {
        pos.set(x, y, z);
        return this;
    }

    public Vec3 getPos() {
        return new Vec3(pos.x, pos.y, pos.z);
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
}