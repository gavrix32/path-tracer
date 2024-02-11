package net.gavrix32.engine.graphics;

import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Plane;
import net.gavrix32.engine.shapes.Sphere;

public class Scene {
    private Camera camera;
    private Sky sky;
    private Plane plane;
    private Sphere[] spheres;
    private Box[] boxes;

    public Scene(Camera camera, Plane plane, Sky sky, Sphere[] spheres, Box[] boxes) {
        this.camera = camera;
        this.sky = sky;
        this.plane = plane;
        this.spheres = spheres;
        this.boxes = boxes;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public Sky getSky() {
        return sky;
    }

    public void setSky(Sky sky) {
        this.sky = sky;
    }

    public Sphere[] getSpheres() {
        return spheres;
    }

    public void setSpheres(Sphere[] spheres) {
        this.spheres = spheres;
    }

    public Box[] getBoxes() {
        return boxes;
    }

    public void setBoxes(Box[] boxes) {
        this.boxes = boxes;
    }
}