package net.gavrix32.engine.objects;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.math.Vector3f;

public class Plane {
    private Vector3f color, color1, color2;
    private float scale = 100.0f;
    private Material material;
    private boolean checkerBoard;

    public Plane(boolean checkerBoard) {
        this.color = new Vector3f(1);
        this.color1 = new Vector3f(1);
        this.color2 = new Vector3f(0);
        this.material = new Material(false, 0, 1, 1, false);
        this.checkerBoard = checkerBoard;
    }

    public Plane(Vector3f color, Material material) {
        this.color = color;
        this.color1 = new Vector3f(1);
        this.color2 = new Vector3f(1);
        this.material = material;
        this.checkerBoard = false;
    }

    public Plane(Vector3f color1, Vector3f color2, Material material) {
        this.color = new Vector3f(1);
        this.color1 = color1;
        this.color2 = color2;
        this.material = material;
        this.checkerBoard = true;
    }

    public Vector3f getColor() {
        return color;
    }

    public Plane setColor(Vector3f color) {
        this.color = color;
        return this;
    }

    public Plane setColor(float r, float g, float b) {
        this.color.x = r;
        this.color.y = g;
        this.color.z = b;
        return this;
    }

    public Vector3f getFirstColor() {
        return color1;
    }

    public Plane setFirstColor(Vector3f color1) {
        this.color1 = color1;
        return this;
    }

    public Plane setFirstColor(float r, float g, float b) {
        this.color1.x = r;
        this.color1.y = g;
        this.color1.z = b;
        return this;
    }

    public Vector3f getSecondColor() {
        return color2;
    }

    public Plane setSecondColor(Vector3f color2) {
        this.color2 = color2;
        return this;
    }

    public Plane setSecondColor(float r, float g, float b) {
        this.color2.x = r;
        this.color2.y = g;
        this.color2.z = b;
        return this;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(boolean isMetal, float emission, float roughness, float IOR, boolean glass) {
        this.material.setMetal(isMetal);
        this.material.setEmission(emission);
        this.material.setRoughness(roughness);
        this.material.setGlass(glass);
        this.material.setIOR(IOR);
    }

    public boolean isCheckerBoard() {
        return checkerBoard;
    }

    public Plane setCheckerBoard(boolean checkerBoard) {
        this.checkerBoard = checkerBoard;
        return this;
    }

    public float getScale() {
        return scale;
    }

    public Plane setScale(float scale) {
        this.scale = scale;
        return this;
    }
}