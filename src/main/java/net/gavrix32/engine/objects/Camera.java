package net.gavrix32.engine.objects;

import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.math.Matrix4f;
import net.gavrix32.engine.math.Vector3f;

public class Camera {
    private Vector3f temp_pos, position, rotation;
    private final Matrix4f rotation_matrix;

    public Camera() {
        position = new Vector3f();
        temp_pos = new Vector3f();
        rotation = new Vector3f();
        rotation_matrix = new Matrix4f();
    }

    public Camera update() {
        rotation_matrix.rotate(rotation);
        temp_pos.set(position);
        return this;
    }

    public Camera move(float x, float y, float z) {
        position.add(new Vector3f(1, 0, 0).mul(rotation_matrix).mul(x));
        position.add(new Vector3f(0, 1, 0).mul(rotation_matrix).mul(y));
        position.add(new Vector3f(0, 0, 1).mul(rotation_matrix).mul(z));
        return this;
    }

    public Camera move(Vector3f v) {
        position.add(new Vector3f(1, 0, 0).mul(rotation_matrix).mul(v.x));
        position.add(new Vector3f(0, 1, 0).mul(rotation_matrix).mul(v.y));
        position.add(new Vector3f(0, 0, 1).mul(rotation_matrix).mul(v.z));
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

    public Matrix4f getRotationMatrix() {
        return rotation_matrix;
    }

    public Vector3f getPosition() {
        return temp_pos;
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