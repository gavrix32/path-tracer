package net.gavrix32.engine.math;

public class Vec3 {
    public float x, y, z;

    public Vec3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(float value) {
        this.x = value;
        this.y = value;
        this.z = value;
    }
}