package net.gavrix32.engine.objects;

import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.math.Matrix4f;
import net.gavrix32.engine.math.Vector3f;

public class Camera {
    private Vector3f position, rotation;
    private float fov;
    private final Matrix4f euler_rotation;

    public Camera() {
        position = new Vector3f();
        rotation = new Vector3f();
        fov = 70.0f;
        euler_rotation = new Matrix4f();
    }

    public Camera update() {
        euler_rotation.rotate(rotation);
        return this;
    }

    public Camera move(float x, float y, float z) {
        position.add(new Vector3f(1, 0, 0).mul(euler_rotation).mul(x));
        position.add(new Vector3f(0, 1, 0).mul(euler_rotation).mul(y));
        position.add(new Vector3f(0, 0, 1).mul(euler_rotation).mul(z));
        return this;
    }

    public Camera move(Vector3f v) {
        position.add(new Vector3f(1, 0, 0).mul(euler_rotation).mul(v.x));
        position.add(new Vector3f(0, 1, 0).mul(euler_rotation).mul(v.y));
        position.add(new Vector3f(0, 0, 1).mul(euler_rotation).mul(v.z));
        return this;
    }

    public Camera moveX(float x) {
        move(x, 0, 0);
        return this;
    }

    public Camera moveY(float y) {
        move(0, y, 0);
        return this;
    }

    public Camera moveZ(float z) {
        move(0, 0, z);
        return this;
    }

    public Camera rotate(float x, float y, float z) {
        rotation.add(x, y, z);
        return this;
    }

    public Camera rotate(Vector3f rotation) {
        this.rotation.add(rotation);
        return this;
    }

    public Camera rotateX(float x) {
        rotation.addX(x);
        return this;
    }

    public Camera rotateY(float y) {
        rotation.addY(y);
        return this;
    }

    public Camera rotateZ(float z) {
        rotation.addZ(z);
        return this;
    }

    public Matrix4f getEulerRotation() {
        return euler_rotation;
    }

    public float getFov() {
        return fov;
    }

    public Camera setFov(float fov) {
        Renderer.resetAccFrames();
        this.fov = fov;
        return this;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Camera setPosition(Vector3f position) {
        this.position = position;
        return this;
    }

    public Camera setPosition(float x, float y, float z) {
        position.set(x, y, z);
        return this;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Camera setRotation(Vector3f rot) {
        this.rotation = rot;
        return this;
    }

    public Camera setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z);
        return this;
    }
}