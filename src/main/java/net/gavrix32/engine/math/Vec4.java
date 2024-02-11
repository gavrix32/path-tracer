package net.gavrix32.engine.math;

public class Vec4 {
    public float x, y, z, w;

    public Vec4() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public Vec4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vec4(float value) {
        this.x = value;
        this.y = value;
        this.z = value;
        this.w = value;
    }
}
