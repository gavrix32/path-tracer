package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vector3f;

public class AABB {
    public Vector3f min, max;

    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
    }
}