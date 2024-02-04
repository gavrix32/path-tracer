package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.math.Vec3;

public class Box extends Shape {
    private Vec3 size;

    public Box(Vec3 pos, Vec3 rot, Vec3 col, Material material, Vec3 size) {
        super(pos, rot, col, material);
        this.size = size;
    }

    public Vec3 getSize() {
        return size;
    }
}