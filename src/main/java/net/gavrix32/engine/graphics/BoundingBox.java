package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Box;
import net.gavrix32.engine.objects.Sphere;
import net.gavrix32.engine.objects.Triangle;

public class BoundingBox {
    public Vector3f min = new Vector3f(Float.POSITIVE_INFINITY);
    public Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY);

    private void addPoint(Vector3f point) {
        min.x = Math.min(min.x, point.x);
        min.y = Math.min(min.y, point.y);
        min.z = Math.min(min.z, point.z);

        max.x = Math.max(max.x, point.x);
        max.y = Math.max(max.y, point.y);
        max.z = Math.max(max.z, point.z);
    }

    public void addBox(Box box) {
        addPoint(Vector3f.diff(box.getPos(), box.getScale()));
        addPoint(Vector3f.sum(box.getPos(), box.getScale()));
    }

    public void addSphere(Sphere sphere) {
        addPoint(Vector3f.diff(sphere.getPos(), sphere.getRadius()));
        addPoint(Vector3f.sum(sphere.getPos(), sphere.getRadius()));
    }

    public void addTriangle(Triangle triangle) {
        addPoint(triangle.v1);
        addPoint(triangle.v2);
        addPoint(triangle.v3);
    }

    public Vector3f getCentre() {
        Vector3f sum = Vector3f.sum(min, max);
        return Vector3f.mul(sum, 0.5f);
    }

    public Vector3f getSize() {
        return Vector3f.diff(max, min);
    }

    public String toString() {
        return "min: " + min + " max: " + max;
    }
}