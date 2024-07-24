package net.gavrix32.engine.math;

import net.gavrix32.engine.utils.Logger;

public final class Vector3f {
    public float x, y, z;

    public Vector3f() {
        zero();
    }

    public Vector3f(float s) {
        set(s);
    }

    public Vector3f(float x, float y, float z) {
        set(x, y, z);
    }

    public Vector3f(Vector3f v) {
        set(v);
    }

    public Vector3f zero() {
        x = 0.0f;
        y = 0.0f;
        z = 0.0f;
        return this;
    }

    public Vector3f set(float s) {
        this.x = s;
        this.y = s;
        this.z = s;
        return this;
    }

    public Vector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3f setX(float x) {
        this.x = x;
        return this;
    }

    public Vector3f setY(float y) {
        this.y = y;
        return this;
    }

    public Vector3f setZ(float z) {
        this.z = z;
        return this;
    }

    public Vector3f set(Vector3f v) {
        x = v.x;
        y = v.y;
        z = v.z;
        return this;
    }

    public float get(int axis) {
        float val = 0;
        switch (axis) {
            case 0 -> val = x;
            case 1 -> val = y;
            case 2 -> val = z;
            default -> Logger.error("Unknown axis " + axis);
        }
        return val;
    }

    public static Vector3f sum(Vector3f... v) {
        Vector3f sum = new Vector3f();
        for (Vector3f vector : v) {
            sum.x += vector.x;
            sum.y += vector.y;
            sum.z += vector.z;
        }
        return sum;
    }

    public static Vector3f sum(Vector3f v, float s) {
        Vector3f sum = new Vector3f();
        sum.x = v.x + s;
        sum.y = v.y + s;
        sum.z = v.z + s;
        return sum;
    }

    public static Vector3f diff(Vector3f v1, Vector3f v2) {
        Vector3f diff = new Vector3f();
        diff.x = v1.x - v2.x;
        diff.y = v1.y - v2.y;
        diff.z = v1.z - v2.z;
        return diff;
    }

    public static Vector3f diff(Vector3f v, float s) {
        Vector3f diff = new Vector3f();
        diff.x = v.x - s;
        diff.y = v.y - s;
        diff.z = v.z - s;
        return diff;
    }

    public Vector3f add(float s) {
        this.x += s;
        this.y += s;
        this.z += s;
        return this;
    }

    public Vector3f add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public Vector3f add(Vector3f v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    public Vector3f addX(float x) {
        this.x += x;
        return this;
    }

    public Vector3f addY(float y) {
        this.y += y;
        return this;
    }

    public Vector3f addZ(float z) {
        this.z += z;
        return this;
    }

    public Vector3f sub(float s) {
        this.x -= s;
        this.y -= s;
        this.z -= s;
        return this;
    }

    public Vector3f sub(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public Vector3f sub(Vector3f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    public Vector3f subX(float x) {
        this.x -= x;
        return this;
    }

    public Vector3f subY(float y) {
        this.y -= y;
        return this;
    }

    public Vector3f subZ(float z) {
        this.z -= z;
        return this;
    }

    public static Vector3f mul(Vector3f v, float s) {
        return new Vector3f(v.x * s, v.y * s, v.z * s);
    }

    public Vector3f mul(float s) {
        this.x *= s;
        this.y *= s;
        this.z *= s;
        return this;
    }

    public Vector3f mul(float x, float y, float z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
        return this;
    }

    public Vector3f mul(Vector3f v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
        return this;
    }

    public Vector3f mul(Matrix4f m) {
        return set(
                m.m[0][0] * x + m.m[0][1] * y + m.m[0][2] * z,
                m.m[1][0] * x + m.m[1][1] * y + m.m[1][2] * z,
                m.m[2][0] * x + m.m[2][1] * y + m.m[2][2] * z
        );
    }

    public static Vector3f div(Vector3f v1, Vector3f v2) {
        return new Vector3f(
                v1.x / v2.x,
                v1.y / v2.y,
                v1.z / v2.z
        );
    }

    public static Vector3f div(Vector3f v1, float s) {
        return new Vector3f(
                v1.x / s,
                v1.y / s,
                v1.z / s
        );
    }

    public Vector3f div(float s) {
        this.x /= s;
        this.y /= s;
        this.z /= s;
        return this;
    }

    public Vector3f div(float x, float y, float z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
        return this;
    }

    public Vector3f div(Vector3f v) {
        x /= v.x;
        y /= v.y;
        z /= v.z;
        return this;
    }

    public Vector3f abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f normalize() {
        return div(length());
    }

    public float dot(float x, float y, float z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public float dot(Vector3f v) {
        return dot(v.x, v.y, v.z);
    }

    public Vector3f cross(Vector3f v) {
        set(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
        return this;
    }

    public float[] toArray() {
        return new float[] {x, y, z};
    }

    public String toString() {
        return x + ", " + y + ", " + z;
    }
}