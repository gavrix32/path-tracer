package net.gavrix32.engine.math;

import java.util.Arrays;

public class Matrix4f {
    private final int
            m00 = 0, m10 = 4, m20 = 8, m30 = 12,
            m01 = 1, m11 = 5, m21 = 9, m31 = 13,
            m02 = 2, m12 = 6, m22 = 10, m32 = 14,
            m03 = 3, m13 = 7, m23 = 11, m33 = 15;
    private float[] values = new float[16];

    public Matrix4f() {
        Arrays.fill(values, 0);
        values[m00] = 1;
        values[m11] = 1;
        values[m22] = 1;
        values[m33] = 1;
    }

    public Matrix4f(float[] values) {
        this.values[m00] = values[0];
        this.values[m10] = values[1];
        this.values[m20] = values[2];
        this.values[m30] = values[3];

        this.values[m01] = values[4];
        this.values[m11] = values[5];
        this.values[m21] = values[6];
        this.values[m31] = values[7];

        this.values[m02] = values[8];
        this.values[m12] = values[9];
        this.values[m22] = values[10];
        this.values[m32] = values[11];

        this.values[m03] = values[12];
        this.values[m13] = values[13];
        this.values[m23] = values[14];
        this.values[m33] = values[15];
    }

    public Matrix4f mul() {
        return new Matrix4f(new float[] {

        });
    }

    public void rotate(float x, float y, float z) {
        float sin = (float) Math.sin(-x);
        float cos = (float) Math.cos(-x);
        /*
        * 1  0  0
        * 0  c  -s
        * 0  s  c
        */
        /*mat3 rotX = mat3(1.0, 0.0, 0.0, 0.0, cos, sin, 0.0, -sin, cos);

        mat3 rotY = mat3(cos.x, 0.0, -sin.x, 0.0, 1.0, 0.0, sin.x, 0.0, cos.x);*/
    }
}