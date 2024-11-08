package net.gavrix32.engine.linearmath;

public final class Vector4f {
    public float x, y, z, w;

    public Vector4f() {
        zero();
    }

    public Vector4f(float s) {
        set(s);
    }

    public Vector4f(float x, float y, float z, float w) {
        set(x, y, z, w);
    }

    public Vector4f(Vector4f v) {
        set(v);
    }

    public Vector4f zero() {
        x = 0.0f;
        y = 0.0f;
        z = 0.0f;
        w = 0.0f;
        return this;
    }

    public Vector4f set(float s) {
        this.x = s;
        this.y = s;
        this.z = s;
        this.w = s;
        return this;
    }

    public Vector4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public Vector4f setX(float x) {
        this.x = x;
        return this;
    }

    public Vector4f setY(float y) {
        this.y = y;
        return this;
    }

    public Vector4f setZ(float z) {
        this.z = z;
        return this;
    }

    public Vector4f setW(float w) {
        this.w = w;
        return this;
    }

    public Vector4f set(Vector4f v) {
        x = v.x;
        y = v.y;
        z = v.z;
        w = v.w;
        return this;
    }

    public Vector4f add(float s) {
        this.x += s;
        this.y += s;
        this.z += s;
        this.w += s;
        return this;
    }

    public Vector4f add(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vector4f add(Vector4f v) {
        x += v.x;
        y += v.y;
        z += v.z;
        w += v.w;
        return this;
    }

    public Vector4f addX(float x) {
        this.x += x;
        return this;
    }

    public Vector4f addY(float y) {
        this.y += y;
        return this;
    }

    public Vector4f addZ(float z) {
        this.z += z;
        return this;
    }
    public Vector4f addW(float w) {
        this.w += w;
        return this;
    }

    public Vector4f sub(float s) {
        this.x -= s;
        this.y -= s;
        this.z -= s;
        this.w -= w;
        return this;
    }

    public Vector4f sub(float x, float y, float z, float w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    public Vector4f sub(Vector4f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        w -= v.w;
        return this;
    }

    public Vector4f mul(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        this.w *= w;
        return this;
    }

    public Vector4f mul(float x, float y, float z, float w) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        this.w *= w;
        return this;
    }

    public Vector4f mul(Vector4f v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
        w *= v.w;
        return this;
    }

    public Vector4f div(float s) {
        this.x /= s;
        this.y /= s;
        this.z /= s;
        this.w /= s;
        return this;
    }

    public Vector4f div(float x, float y, float z, float w) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        this.w /= w;
        return this;
    }

    public Vector4f div(Vector4f v) {
        x /= v.x;
        y /= v.y;
        z /= v.z;
        w /= v.w;
        return this;
    }

    public Vector4f abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        w = Math.abs(w);
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Vector4f normalize() {
        return div(length());
    }

    public float dot(float x, float y, float z, float w) {
        return this.x * x + this.y * y + this.z * z + this.w * w;
    }

    public float dot(Vector4f v) {
        return dot(v.x, v.y, v.z, v.w);
    }

    public Vector4f cross(Vector4f v) {
        set(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x, w * v.w - w * v.w);
        return this;
    }

    public String toString() {
        return x + " " + y + " " + z;
    }
}