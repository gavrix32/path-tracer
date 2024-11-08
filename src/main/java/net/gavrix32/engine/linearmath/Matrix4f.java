package net.gavrix32.engine.linearmath;

import org.tinylog.Logger;

public final class Matrix4f {
    public float[][] m = new float[4][4];

    public Matrix4f() {
        identity();
    }

    public Matrix4f(float a, float b, float c, float d,
                    float e, float f, float g, float h,
                    float i, float j, float k, float l,
                    float m, float n, float o, float p) {
        set(new float[] {
                a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p
        });
    }

    public Matrix4f(float[] values) {
        set(values);
    }

    public Matrix4f(Matrix4f m) {
        set(m);
    }

    public Matrix4f zero() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                m[i][j] = 0.0f;
            }
        }
        return this;
    }

    public Matrix4f identity() {
        zero();
        for (int i = 0; i < 4; i++) m[i][i] = 1.0f;
        return this;
    }

    public Matrix4f set(float[] values) {
        if (values.length != 16) Logger.error("Matrix4f length (" + values.length + ") is not equal to 16");
        for (int i = 0; i < 4; i++)
            System.arraycopy(values, 4 * i, m[i], 0, 4);
        return this;
    }

    public Matrix4f set(Matrix4f matrix) {
        m = matrix.m;
        return this;
    }

    public float[] get() {
        float[] values = new float[16];
        for (int i = 0; i < 4; i++)
            System.arraycopy(m[i], 0, values, 4 * i, 4);
        return values;
    }

    public Matrix4f translate(float x, float y, float z) {
        m[0][3] += m[0][0] * x + m[0][1] * y + m[0][2] * z;
        m[1][3] += m[1][0] * x + m[1][1] * y + m[1][2] * z;
        m[2][3] += m[2][0] * x + m[2][1] * y + m[2][2] * z;
        return this;
    }

    public Matrix4f translate(Vector3f v) {
        translate(v.x, v.y, v.z);
        return this;
    }

    public Matrix4f rotateX(float angle) {
        float sin = (float) Math.sin(Math.toRadians(angle));
        float cos = (float) Math.cos(Math.toRadians(angle));
        m[1][1] = cos;
        m[1][2] = -sin;
        m[2][1] = sin;
        m[2][2] = cos;
        return this;
    }

    public Matrix4f rotateY(float angle) {
        float sin = (float) Math.sin(Math.toRadians(angle));
        float cos = (float) Math.cos(Math.toRadians(angle));
        m[0][0] = cos;
        m[0][2] = sin;
        m[2][0] = -sin;
        m[2][2] = cos;
        return this;
    }

    public Matrix4f rotateZ(float angle) {
        float sin = (float) Math.sin(Math.toRadians(angle));
        float cos = (float) Math.cos(Math.toRadians(angle));
        m[0][0] = cos;
        m[0][1] = -sin;
        m[1][0] = sin;
        m[1][1] = cos;
        return this;
    }

    public Matrix4f rotate(float x, float y, float z) {
        return set(new Matrix4f().rotateZ(-z)
                .mul(new Matrix4f().rotateY(y))
                .mul(new Matrix4f().rotateX(-x)));
    }

    public Matrix4f rotate(Vector3f v) {
        return this.rotate(v.x, v.y, v.z);
    }

    public Matrix4f setScale(Vector3f scale) {
        m[0][0] = scale.x;
        m[1][1] = scale.y;
        m[2][2] = scale.z;
        return this;
    }

    public Matrix4f setScale(float x, float y, float z) {
        m[0][0] = x;
        m[1][1] = y;
        m[2][2] = z;
        return this;
    }

    public Matrix4f setScale(float s) {
        m[0][0] = s;
        m[1][1] = s;
        m[2][2] = s;
        return this;
    }

    public Matrix4f mul(Matrix4f matrix) {
        Matrix4f result = new Matrix4f().zero();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    result.m[i][j] += this.m[i][k] * matrix.m[k][j];
                }
            }
        }
        return result;
    }

    public String toString() {
        return m[0][0] + " " + m[0][1] + " " + m[0][2] + " " + m[0][3] + System.lineSeparator() +
               m[1][0] + " " + m[1][1] + " " + m[1][2] + " " + m[1][3] + System.lineSeparator() +
               m[2][0] + " " + m[2][1] + " " + m[2][2] + " " + m[2][3] + System.lineSeparator() +
               m[3][0] + " " + m[3][1] + " " + m[3][2] + " " + m[3][3];
    }
}