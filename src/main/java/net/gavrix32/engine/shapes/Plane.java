package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Plane {
    private Vector3f color;
    private Material material;
    private boolean checkerBoard;

    public Plane() {
        this.color = new Vector3f(1);
        this.material = new Material(0, 1, false);
        this.checkerBoard = true;
    }

    public Plane(Vector3f color, Material material, boolean checkerBoard) {
        this.color = color;
        this.material = material;
        this.checkerBoard = checkerBoard;
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