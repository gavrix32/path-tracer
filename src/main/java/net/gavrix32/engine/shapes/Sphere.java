package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.utils.Logger;
import org.joml.Vector3f;

public class Sphere extends Shape {
    private float radius;

    public Sphere(Vector3f pos, Vector3f rot, Vector3f col, Material material, float radius) {
        super(pos, rot, col, material);
        this.radius = radius;
    }

    public Sphere() {
        super(new Vector3f(), new Vector3f(), new Vector3f(1), new Material(true, 0, 1, 1));
        this.radius = 10;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float value) {
        if (value < 0) Logger.error("Radius cannot be less than 0");
        this.radius = value;
    }
}