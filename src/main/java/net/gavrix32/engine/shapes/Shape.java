package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.math.Vector3f;

public class Shape {
    private Vector3f pos, rot, col;
    private Material material;

    public Shape(Vector3f pos, Vector3f rot, Vector3f col, Material material) {
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