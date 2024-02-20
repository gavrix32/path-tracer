package net.gavrix32.engine.graphics;

import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Plane;
import net.gavrix32.engine.shapes.Sphere;

import java.util.ArrayList;
import java.util.Arrays;

public class Scene {
    private Camera camera;
    private Sky sky;
    private Plane plane;
    private final ArrayList<Sphere> spheres;
    private final ArrayList<Box> boxes;

    public Scene() {
        camera = new Camera();
        sky = new Sky();
        plane = new Plane();
        spheres = new ArrayList<>();
        boxes = new ArrayList<>();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Sky getSky() {
        return sky;
    }

    public void setSky(Sky sky) {
        this.sky = sky;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public void addBox(Box box) {
        boxes.add(box);
    }

    public void addBoxes(Box... boxes) {
        this.boxes.addAll(Arrays.asList(boxes));
    }

    public ArrayList<Box> getBoxes() {
        return boxes;
    }

    public void addSphere(Sphere sph) {
        spheres.add(sph);
    }

    public void addSpheres(Sphere... spheres) {
        this.spheres.addAll(Arrays.asList(spheres));
    }

    public ArrayList<Sphere> getSpheres() {
        return spheres;
    }
}