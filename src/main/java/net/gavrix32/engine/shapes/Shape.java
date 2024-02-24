package net.gavrix32.engine.shapes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.utils.Logger;
import org.joml.Vector3f;

public class Shape {
    private Vector3f pos, rot, col;
    private Material material;

    public Shape(Vector3f pos, Vector3f rot, Vector3f col, Material material) {
        if (col.x < 0 || col.x > 1 || col.y < 0 || col.y > 1 || col.z < 0 || col.z > 1) {
            Logger.error("Color value must be between 0 and 1");
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

    public void setPos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }

    public Vector3f getRot() {
        return rot;
    }

    public void setRot(Vector3f rot) {
        this.rot = rot;
    }

    public void setRot(float x, float y, float z) {
        this.rot.x = x;
        this.rot.y = y;
        this.rot.z = z;
    }

    public Vector3f getColor() {
        return col;
    }

    public void setColor(Vector3f col) {
        this.col = col;
    }

    public void setColor(float r, float g, float b) {
        this.col.x = r;
        this.col.y = g;
        this.col.z = b;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setMaterial(float emission, float roughness, boolean isMetal) {
        this.material = new Material(emission, roughness, isMetal);
    }
}