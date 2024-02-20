package net.gavrix32.engine.graphics;

public class Material {
    private float emission, roughness;
    private boolean isMetal;

    public Material(float emission, float roughness, boolean isMetal) {
        this.emission = emission;
        this.roughness = roughness;
        this.isMetal = isMetal;
    }

    public float getEmission() {
        return emission;
    }

    public void setEmission(float emission) {
        this.emission = emission;
    }

    public float getRoughness() {
        return roughness;
    }

    public void setRoughness(float roughness) {
        this.roughness = roughness;
    }

    public boolean isMetal() {
        return isMetal;
    }

    public void setMetal(boolean metal) {
        isMetal = metal;
    }
}