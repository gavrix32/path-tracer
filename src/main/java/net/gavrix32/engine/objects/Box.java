package net.gavrix32.engine.objects;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.linearmath.Matrix4f;
import net.gavrix32.engine.linearmath.Vector3f;
import org.tinylog.Logger;

public class Box extends Shape {
    private Vector3f scale;
    private final Matrix4f rotationMatrix;

    public Box(Vector3f pos, Vector3f rotation, Vector3f col, Vector3f scale, Material material) {
        super(pos, rotation, col, material);
        this.scale = scale;
        rotationMatrix = new Matrix4f();
    }

    public Box() {
        super(new Vector3f(), new Vector3f(), new Vector3f(1), new Material(true, 0, 1, 1, false));
        this.scale = new Vector3f(10);
        rotationMatrix = new Matrix4f();
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f value) {
        if (value.x < 0 || value.y < 0 || value.z < 0) Logger.error("Scale cannot be less than 0");
        this.scale = value;
    }

    public void setScale(float x, float y, float z) {
        if (x < 0 || y < 0 || z < 0) Logger.error("Scale cannot be less than 0");
        this.scale.x = x;
        this.scale.y = y;
        this.scale.z = z;
    }

    public Matrix4f getRotationMatrix() {
        return rotationMatrix;
    }
}