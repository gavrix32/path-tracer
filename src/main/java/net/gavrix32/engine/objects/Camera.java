package net.gavrix32.engine.objects;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private final Vector3f pos;
    private Vector3f rot;
    private final Matrix4f view;

    public Camera() {
        pos = new Vector3f(0);
        rot = new Vector3f(0);
        view = new Matrix4f();
    }

    public void update() {
        view.identity();
        view.rotateX(rot.x);
        view.rotateY(rot.y);
        view.rotateZ(rot.z);
    }

    public Vector3f getPos() {
        return new Vector3f(pos);
    }

    public Camera setPos(float x, float y, float z) {
        pos.set(x, y, z);
        return this;
    }

    public Vector3f getRot() {
        return rot;
    }

    public void setRot(Vector3f rot) {
        this.rot = rot;
    }

    public void move(float x, float y, float z) {
        pos.add(view.positiveX(new Vector3f()).mul(x));
        pos.add(view.positiveY(new Vector3f()).mul(y));
        pos.add(view.positiveZ(new Vector3f()).mul(z));
    }

    public void rotateX(float x) {
        rot.add(x, 0, 0);
    }

    public void rotateY(float y) {
        rot.add(0, y, 0);
    }

    public Matrix4f getView() {
        return view;
    }
}