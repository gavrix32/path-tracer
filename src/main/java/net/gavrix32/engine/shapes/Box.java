package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.math.Vector3f;

public class Box extends Shape {
    private Vector3f size;

    public Box(Vector3f pos, Vector3f col, Material material, Vector3f size) {
        super(pos, col, material);
        this.size = size;
    }

    public Vector3f getSize() {
        return size;
    }
}