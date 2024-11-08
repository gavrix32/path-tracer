package net.gavrix32.engine.linearmath;

public final class Vector2f {
    public float x, y;

    public Vector2f() {
        zero();
    }

    public Vector2f(float s) {
        set(s);
    }

    public Vector2f(float x, float y) {
        set(x, y);
    }

    public Vector2f zero() {
        x = 0.0f;
        y = 0.0f;
        return this;
    }

    public Vector2f set(float s) {
        this.x = s;
        this.y = s;
        return this;
    }

    public Vector2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2f set(Vector2f v) {
        x = v.x;
        y = v.y;
        return this;
    }

    public Vector2f add(float s) {
        this.x += s;
        this.y += s;
        return this;
    }

    public Vector2f add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Vector2f add(Vector2f v) {
        x += v.x;
        y += v.y;
        return this;
    }

    public Vector2f addX(float x) {
        this.x += x;
        return this;
    }

    public Vector2f addY(float y) {
        this.y += y;
        return this;
    }

    public Vector2f sub(float s) {
        this.x -= s;
        this.y -= s;
        return this;
    }

    public Vector2f sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vector2f sub(Vector2f v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    public Vector2f mul(float s) {
        this.x *= s;
        this.y *= s;
        return this;
    }

    public Vector2f mul(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vector2f mul(Vector2f v) {
        x *= v.x;
        y *= v.y;
        return this;
    }

    public Vector2f div(float s) {
        this.x /= s;
        this.y /= s;
        return this;
    }

    public Vector2f div(float x, float y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    public Vector2f div(Vector2f v) {
        x /= v.x;
        y /= v.y;
        return this;
    }

    public Vector2f abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2f normalize() {
        return this.div(length());
    }

    public float dot(float x, float y) {
        return this.x * x + this.y * y;
    }

    public float dot(Vector2f v) {
        return dot(v.x, v.y);
    }

    public String toString() {
        return x + " " + y;
    }
}