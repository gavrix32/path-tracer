package net.gavrix32.engine.graphics;

import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Sphere;

public class Scene {
    private Sphere[] spheres;
    private Box[] boxes;;
    private Camera camera;

    public Scene(Camera camera, Sphere[] spheres, Box[] boxes) {
        this.spheres = spheres;
        this.boxes = boxes;
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
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