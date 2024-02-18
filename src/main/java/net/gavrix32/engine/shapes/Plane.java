package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Plane {
    private Vector4f normal;
    private Vector3f color;
    private Material material;
    private boolean checkerBoard;

    public Plane(Vector4f normal, Vector3f color, Material material, boolean checkerBoard) {
        this.normal = normal;
        this.color = color;
        this.material = material;
        this.checkerBoard = checkerBoard;
    }

    public Vector4f getNormal() {
        return normal;
    }

    public Vector3f getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isCheckerBoard() {
        return checkerBoard;
    }
}