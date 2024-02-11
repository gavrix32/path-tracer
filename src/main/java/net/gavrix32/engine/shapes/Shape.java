package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.math.Vec3;

public class Shape {
    private Vec3 pos, rot, col;
    private Material material;

    public Shape(Vec3 pos, Vec3 rot, Vec3 col, Material material) {
        if (col.x < 0 || col.x > 1 || col.y < 0 || col.y > 1 || col.z < 0 || col.z > 1) {
            System.err.println("The color value must be between 0 and 1!");
        }
        this.pos = pos;
        this.rot = rot;
        this.col = col;
        this.material = material;
    }

    public Vec3 getPos() {
        return pos;
    }

    public void setPos(Vec3 pos) {
        this.pos = pos;
    }

    public Vec3 getRot() {
        return rot;
    }

    public void setRot(Vec3 rot) {
        this.rot = rot;
    }

    public Vec3 getColor() {
        return col;
    }

    public void setCol(Vec3 col) {
        this.col = col;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}