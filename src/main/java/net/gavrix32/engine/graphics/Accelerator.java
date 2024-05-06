package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Box;
import net.gavrix32.engine.objects.Sphere;

public class Accelerator {
    private final Scene scene;

    public Accelerator(Scene scene) {
        this.scene = scene;
    }

    public AABB getSpheresBoundingBox() {
        AABB aabb = new AABB(
                new Vector3f(Float.MAX_VALUE),
                new Vector3f(Float.MIN_VALUE)
        );
        for (Sphere sphere : scene.spheres) {
            if (sphere.getAABB().min.x < aabb.min.x) aabb.min.x = sphere.getAABB().min.x;
            if (sphere.getAABB().min.y < aabb.min.y) aabb.min.y = sphere.getAABB().min.y;
            if (sphere.getAABB().min.z < aabb.min.z) aabb.min.z = sphere.getAABB().min.z;
            if (sphere.getAABB().max.x > aabb.max.x) aabb.max.x = sphere.getAABB().max.x;
            if (sphere.getAABB().max.y > aabb.max.y) aabb.max.y = sphere.getAABB().max.y;
            if (sphere.getAABB().max.z > aabb.max.z) aabb.max.z = sphere.getAABB().max.z;
        }
        return aabb;
    }

    public AABB getBoxesBoundingBox() {
        AABB aabb = new AABB(
                new Vector3f(Float.MAX_VALUE),
                new Vector3f(Float.MIN_VALUE)
        );
        for (Box box : scene.boxes) {
            if (box.getAABB().min.x < aabb.min.x) aabb.min.x = box.getAABB().min.x;
            if (box.getAABB().min.y < aabb.min.y) aabb.min.y = box.getAABB().min.y;
            if (box.getAABB().min.z < aabb.min.z) aabb.min.z = box.getAABB().min.z;
            if (box.getAABB().max.x > aabb.max.x) aabb.max.x = box.getAABB().max.x;
            if (box.getAABB().max.y > aabb.max.y) aabb.max.y = box.getAABB().max.y;
            if (box.getAABB().max.z > aabb.max.z) aabb.max.z = box.getAABB().max.z;
        }
        return aabb;
    }
}