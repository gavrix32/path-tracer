package net.gavrix32.engine.graphics;

public class Material {
    private float emission, roughness;

    public Material(float emission, float roughness) {
        this.emission = emission;
        this.roughness = roughness;
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
}