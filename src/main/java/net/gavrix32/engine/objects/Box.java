package net.gavrix32.engine.objects;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.utils.Logger;
import org.joml.Vector3f;

public class Box extends Shape {
    private Vector3f scale;

    public Box(Vector3f pos, Vector3f rot, Vector3f col, Material material, Vector3f scale) {
        super(pos, rot, col, material);
        this.scale = scale;
    }

    public Box() {
        super(new Vector3f(), new Vector3f(), new Vector3f(1), new Material(true, 0, 1, 1));
        this.scale = new Vector3f(10);
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
}