package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.math.Vec3;
import net.gavrix32.engine.math.Vec4;

public class Plane {
    private Vec4 normal;
    private Vec3 color;
    private Material material;
    private boolean checkerBoard;

    public Plane(Vec4 normal, Vec3 color, Material material, boolean checkerBoard) {
        this.normal = normal;
        this.color = color;
        this.material = material;
        this.checkerBoard = checkerBoard;
    }

    public Vec4 getNormal() {
        return normal;
    }

    public Vec3 getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isCheckerBoard() {
        return checkerBoard;
    }
}