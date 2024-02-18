package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import org.joml.Vector3f;

public class Shape {
    private Vector3f pos, rot, col;
    private Material material;

    public Shape(Vector3f pos, Vector3f rot, Vector3f col, Material material) {
        if (col.x < 0 || col.x > 1 || col.y < 0 || col.y > 1 || col.z < 0 || col.z > 1) {
            System.err.println("The color value must be between 0 and 1!");
        }
        this.pos = pos;
        this.rot = rot;
        this.col = col;
        this.material = material;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getRot() {
        return rot;
    }

    public void setRot(Vector3f rot) {
        this.rot = rot;
    }

    public Vector3f getColor() {
        return col;
    }

    public void setCol(Vector3f col) {
        this.col = col;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}