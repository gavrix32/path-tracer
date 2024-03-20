package net.gavrix32.engine.objects;

import net.gavrix32.engine.graphics.Renderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f pos, rot;
    private final Matrix4f view;
    private float fov;

    public Camera() {
        pos = new Vector3f(0);
        rot = new Vector3f(0);
        view = new Matrix4f();
        fov = 70.0f;
    }

    public void update() {
        view.identity();
        view.rotateX((float) Math.toRadians(rot.x));
        view.rotateY((float) Math.toRadians(rot.y));
        view.rotateZ((float) Math.toRadians(rot.z));
    }

    public Vector3f getPosition() {
        return new Vector3f(pos);
    }

    public Camera setPosition(Vector3f pos) {
        this.pos = pos;
        return this;
    }

    public Camera setPosition(float x, float y, float z) {
        pos.set(x, y, z);
        return this;
    }

    public Vector3f getRotation() {
        return rot;
    }

    public Camera setRotation(Vector3f rot) {
        this.rot = rot;
        return this;
    }

    public Camera setRotation(float x, float y, float z) {
        this.rot.set(x, y, z);
        return this;
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

    public float getFov() {
        return fov;
    }

    public Camera setFov(float fov) {
        Renderer.resetAccFrames();
        this.fov = fov;
        return this;
    }
}