package net.gavrix32.engine.graphics;

import net.gavrix32.engine.objects.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Scene {
    private String name;
    private Camera camera;
    private Sky sky;
    private Plane plane;
    private final ArrayList<Sphere> spheres;
    private final ArrayList<Box> boxes;

    public Scene() {
        name = "Unnamed scene";
        camera = new Camera();
        sky = new Sky();
        spheres = new ArrayList<>();
        boxes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Sky setSky(String path) {
        this.sky = new Sky(path);
        return sky;
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

    public void removeBox(int index) {
        boxes.remove(index);
    }

    public void addBoxes(Box... boxes) {
        this.boxes.addAll(Arrays.asList(boxes));
    }

    public Box getBox(int index) {
        return boxes.get(index);
    }

    public ArrayList<Box> getBoxes() {
        return boxes;
    }

    public void addSphere(Sphere sph) {
        spheres.add(sph);
    }

    public void removeSphere(int index) {
        spheres.remove(index);
    }

    public void addSpheres(Sphere... spheres) {
        this.spheres.addAll(Arrays.asList(spheres));
    }

    public Sphere getSphere(int index) {
        return spheres.get(index);
    }

    public ArrayList<Sphere> getSpheres() {
        return spheres;
    }
}