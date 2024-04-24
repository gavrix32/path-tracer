package net.gavrix32.engine.math;

import net.gavrix32.engine.utils.Logger;

public final class Matrix4f {
    public float[][] m;

    public Matrix4f() {
        m = new float[4][4];
        identity();
    }

    public Matrix4f(float[] values) {
        m = new float[4][4];
        set(values);
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
        for (int i = 0; i < 4; i++) {
            System.arraycopy(values, 4 * i, m[i], 0, 4);
        }
        return this;
    }

    public Matrix4f set(Matrix4f m) {
        this.m = m.m;
        return this;
    }

    public float[] get() {
        float[] values = new float[16];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(m[i], 0, values, 4 * i, 4);
        }
        return values;
    }

    public Matrix4f translate(float x, float y, float z) {
        m[0][3] += m[0][0] * x + m[0][1] * y + m[0][2] * z;
        m[1][3] += m[1][0] * x + m[1][1] * y + m[1][2] * z;
        m[2][3] += m[2][0] * x + m[2][1] * y + m[2][2] * z;
        m[3][3] += m[3][0] * x + m[3][1] * y + m[3][2] * z; // ?
        return this;
    }

    public Matrix4f translate(Vector3f v) {
        translate(v.x, v.y, v.z);
        return this;
    }

    public Vector3f getTranslation() {
        return new Vector3f(m[0][3], m[1][3], m[2][3]);
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
        return set(new Matrix4f().rotateZ(z)
                .mul(new Matrix4f().rotateY(y))
                .mul(new Matrix4f().rotateX(x)));
    }

    public Matrix4f rotate(Vector3f v) {
        return this.rotate(v.x, v.y, v.z);
    }

    public Matrix4f lookAt(Vector3f pos, Vector3f dir, Vector3f up) {
        Vector3f d = new Vector3f(pos).sub(dir).normalize(); // dir
        Vector3f r = new Vector3f(up).cross(d).normalize(); // right
        Vector3f u = new Vector3f(d).cross(r).normalize(); // up
        m[0][0] = r.x; m[0][1] = u.x; m[0][2] = d.x; m[0][3] = 0;
        m[1][0] = r.y; m[1][1] = u.y; m[1][2] = d.y; m[1][3] = 0;
        m[2][0] = r.z; m[2][1] = u.z; m[2][2] = d.z; m[2][3] = 0;
        m[3][0] =   0; m[3][1] =   0; m[3][2] =   0; m[3][3] = 1;
        return this;
    }

    public Matrix4f perspective(float fov, float aspect, float zNear, float zFar) {
        zero();
        float fn = zFar + zNear;
        float f_n = zFar - zNear;
        float t = 1.0f / (float) Math.tan(Math.toRadians(fov) / 2.0f);
        this.m[0][0] = t / aspect;
        this.m[1][1] = t;
        this.m[2][2] = -fn / f_n;
        this.m[3][2] = -2.0f * zFar * zNear / f_n;
        this.m[2][3] = -1.0f;
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