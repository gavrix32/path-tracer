package net.gavrix32.engine.graphics;

public class Material {
    private boolean isMetal;
    private float emission;
    private float roughness;
    private float IOR; // Index Of Refraction

    public Material(boolean isMetal, float emission, float roughness, float IOR) {
        this.isMetal = isMetal;
        this.emission = emission;
        this.roughness = roughness;
        this.IOR = IOR;
    }

    public boolean isMetal() {
        return isMetal;
    }

    public void setMetal(boolean metal) {
        isMetal = metal;
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

    public float getIOR() {
        return IOR;
    }

    public void setIOR(float ior) {
        this.IOR = ior;
    }
}