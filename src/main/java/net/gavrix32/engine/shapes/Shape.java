package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import org.joml.Vector3f;

public class Shape {
    private Vector3f pos, col;
    private Material material;

    public Shape(Vector3f pos, Vector3f col, Material material) {
        this.pos = pos;
        this.col = col;
        this.material = material;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getCol() {
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