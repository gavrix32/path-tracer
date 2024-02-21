package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.shapes.Plane;
import net.gavrix32.engine.shapes.Sphere;
import org.joml.Vector3f;

public class Spheres {
    private Scene scene;

    public Spheres() {
        scene = new Scene();
        scene.setCamera(new Camera().setPos(50, 20, -80));
        scene.setPlane(new Plane(new Vector3f(1), new Material(0, 1, false), false));
        scene.addSpheres(
                new Sphere(
                        new Vector3f(0, 20, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 0.5f, 0),
                        new Material(1, 1, true), 20),
                new Sphere(
                        new Vector3f(50, 20, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Material(0, 0, true), 20),
                new Sphere(
                        new Vector3f(100, 20, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.5f, 1, 0),
                        new Material(1, 1, true), 20)
        );
    }
    public Scene getScene() {
        return scene;
    }
}