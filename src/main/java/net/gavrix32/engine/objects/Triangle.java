package net.gavrix32.engine.objects;

import net.gavrix32.engine.math.Vector2f;
import net.gavrix32.engine.math.Vector3f;

public class Triangle {
    public Vector3f v1, v2, v3;
    public Vector2f uv1, uv2, uv3;

    public Triangle(Vector3f v1, Vector3f v2, Vector3f v3, Vector2f uv1, Vector2f uv2, Vector2f uv3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.uv1 = uv1;
        this.uv2 = uv2;
        this.uv3 = uv3;
    }

    public Vector3f getCentre() {
        Vector3f sum = Vector3f.sum(v1, v2, v3);
        return Vector3f.div(sum, 3.0f);
    }
}