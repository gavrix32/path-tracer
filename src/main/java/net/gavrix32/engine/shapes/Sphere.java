package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.math.Vec3;

public class Sphere extends Shape {
    private float radius;

    public Sphere(Vec3 pos, Vec3 rot, Vec3 col, Material material, float radius) {
        super(pos, rot, col, material);
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }
}