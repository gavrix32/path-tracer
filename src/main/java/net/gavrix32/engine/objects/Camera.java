package net.gavrix32.engine.objects;

import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.math.Matrix4f;
import net.gavrix32.engine.math.Vector3f;

public class Camera {
    private Vector3f position;
    private Vector3f rotation;
    private final Vector3f cameraUp;
    private Vector3f cameraFront;
    private float fov;
    private final Matrix4f view;

    public Camera() {
        position = new Vector3f();
        rotation = new Vector3f(-90, 0, 0);
        fov = 70.0f;
        view = new Matrix4f();
        cameraUp = new Vector3f(0, 1, 0);
        cameraFront = new Vector3f();
    }

    public void update() {
        if (rotation.y > 89.9f) rotation.y = 89.9f;
        if (rotation.y < -89.9f) rotation.y = -89.9f;
        Vector3f front = new Vector3f();
        front.x = (float) (Math.cos(Math.toRadians(rotation.x)) * Math.cos(Math.toRadians(rotation.y)));
        front.y = (float) Math.sin(Math.toRadians(rotation.y));
        front.z = (float) (Math.sin(Math.toRadians(rotation.x)) * Math.cos(Math.toRadians(rotation.y)));
        cameraFront = new Vector3f(front).normalize();
        view.lookAt(position, new Vector3f(position).add(cameraFront), cameraUp);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Camera setPosition(Vector3f pos) {
        this.position = pos;
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

    public void move(Vector3f v) {
        position.add(v);
    }

    public void move(float x, float y, float z) {
        position.add(new Vector3f(cameraFront).cross(cameraUp).normalize().mul(x));
        position.add(new Vector3f(cameraUp).mul(y));
        position.add(new Vector3f(cameraFront).mul(z));
    }

    public void moveX(float x) {
        move(x, 0, 0);
    }

    public void moveY(float y) {
        move(0, y, 0);
    }

    public void moveZ(float z) {
        move(0, 0, z);
    }

    public void rotateX(float x) {
        rotation.addX(x);
    }

    public void rotateY(float y) {
        rotation.addY(y);
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