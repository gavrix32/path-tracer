package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.*;

import java.util.ArrayList;
import java.util.Arrays;

public class Scene {
    private String name;
    protected Camera camera;
    protected Sky sky;
    protected Plane plane;
    protected final ArrayList<Sphere> spheres;
    protected final ArrayList<Box> boxes;
    protected final ArrayList<Triangle> triangles;

    public Scene() {
        name = "Unnamed scene";
        camera = new Camera();
        sky = new Sky();
        spheres = new ArrayList<>();
        boxes = new ArrayList<>();
        triangles = new ArrayList<>();
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

    public Camera setCamera(Vector3f position) {
        camera = new Camera().setPosition(position.x, position.y, position.z);
        return camera;
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

    public Box getBox(int index) {
        return boxes.get(index);
    }

    public void removeBox(int index) {
        boxes.remove(index);
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

    public void removeSphere(int index) {
        spheres.remove(index);
    }

    public void addSpheres(Sphere... spheres) {
        this.spheres.addAll(Arrays.asList(spheres));
    }

    public ArrayList<Sphere> getSpheres() {
        return spheres;
    }

    public Sphere getSphere(int index) {
        return spheres.get(index);
    }

    public void addTriangle(Triangle triangle) {
        triangles.add(triangle);
    }

    public Triangle getTriangle(int index) {
        return triangles.get(index);
    }

    public void removeTriangle(int index) {
        triangles.remove(index);
    }

    public void addTriangles(Triangle... triangles) {
        this.triangles.addAll(Arrays.asList(triangles));
    }

    public ArrayList<Triangle> getTriangles() {
        return triangles;
    }
}