package net.gavrix32.engine.linearmath;

import org.tinylog.Logger;

public final class Matrix3f {
    public float[][] m = new float[3][3];

    public Matrix3f() {
        identity();
    }

    public Matrix3f(float a, float b, float c,
                    float d, float e, float f,
                    float g, float h, float i) {
        set(new float[] {
                a, b, c, d, e, f, g, h, i
        });
    }

    public Matrix3f(float[] values) {
        set(values);
    }

    public Matrix3f zero() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                m[i][j] = 0.0f;
            }
        }
        return this;
    }

    public Matrix3f identity() {
        zero();
        for (int i = 0; i < 3; i++) m[i][i] = 1.0f;
        return this;
    }

    public Matrix3f set(float[] values) {
        if (values.length != 9) Logger.error("Matrix3f length (" + values.length + ") is not equal to 9");
        for (int i = 0; i < 3; i++)
            System.arraycopy(values, 3 * i, m[i], 0, 3);
        return this;
    }

    public Matrix3f set(Matrix3f matrix) {
        m = matrix.m;
        return this;
    }

    public float[] get() {
        float[] values = new float[9];
        for (int i = 0; i < 3; i++)
            System.arraycopy(m[i], 0, values, 3 * i, 3);
        return values;
    }

    public Matrix3f translate(float x, float y, float z) {
        m[0][3] += m[0][0] * x + m[0][1] * y + m[0][2] * z;
        m[1][3] += m[1][0] * x + m[1][1] * y + m[1][2] * z;
        m[2][3] += m[2][0] * x + m[2][1] * y + m[2][2] * z;
        return this;
    }

    public Matrix3f translate(Vector3f v) {
        translate(v.x, v.y, v.z);
        return this;
    }

    public Matrix3f rotateX(float angle) {
        float sin = (float) Math.sin(Math.toRadians(angle));
        float cos = (float) Math.cos(Math.toRadians(angle));
        m[1][1] = cos;
        m[1][2] = -sin;
        m[2][1] = sin;
        m[2][2] = cos;
        return this;
    }

    public Matrix3f rotateY(float angle) {
        float sin = (float) Math.sin(Math.toRadians(angle));
        float cos = (float) Math.cos(Math.toRadians(angle));
        m[0][0] = cos;
        m[0][2] = sin;
        m[2][0] = -sin;
        m[2][2] = cos;
        return this;
    }

    public Matrix3f rotateZ(float angle) {
        float sin = (float) Math.sin(Math.toRadians(angle));
        float cos = (float) Math.cos(Math.toRadians(angle));
        m[0][0] = cos;
        m[0][1] = -sin;
        m[1][0] = sin;
        m[1][1] = cos;
        return this;
    }

    public Matrix3f rotate(float x, float y, float z) {
        return set(new Matrix3f().rotateZ(-z)
                .mul(new Matrix3f().rotateY(y))
                .mul(new Matrix3f().rotateX(-x)));
    }

    public Matrix3f rotate(Vector3f v) {
        return this.rotate(v.x, v.y, v.z);
    }

    public Matrix3f mul(Matrix3f matrix) {
        Matrix3f result = new Matrix3f().zero();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    result.m[i][j] += this.m[i][k] * matrix.m[k][j];
                }
            }
        }
        return result;
    }

    public String toString() {
        return m[0][0] + " " + m[0][1] + " " + m[0][2] + System.lineSeparator() +
               m[1][0] + " " + m[1][1] + " " + m[1][2] + System.lineSeparator() +
               m[2][0] + " " + m[2][1] + " " + m[2][2];
    }
}