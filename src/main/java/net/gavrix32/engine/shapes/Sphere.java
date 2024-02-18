package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import org.joml.Vector3f;

public class Sphere extends Shape {
    private float radius;

    public Sphere(Vector3f pos, Vector3f rot, Vector3f col, Material material, float radius) {
        super(pos, rot, col, material);
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }
}