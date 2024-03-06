package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Plane;
import net.gavrix32.engine.shapes.Sphere;
import org.joml.Vector3f;

public class Spheres {
    private final Scene scene;

    public Spheres() {
        scene = new Scene();
        scene.setCamera(new Camera().setPos(0, 100, -200));
        scene.setSky("textures/sky/kiara_1_dawn_4k.hdr");
        scene.addBox(new Box(
                new Vector3f(),
                new Vector3f(),
                new Vector3f(1),
                new Material(true, 0, 1, 1),
                new Vector3f(250, 0, 100)
        ));
        scene.addSpheres(
                new Sphere(
                        new Vector3f(-120, 50, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(true, 0, 1, 1), 50),
                new Sphere(
                        new Vector3f(0, 50, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(true, 0, 0.3f, 1), 50),
                new Sphere(
                        new Vector3f(120, 50, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(true, 0, 0, 1), 50)
        );
    }
    public Scene getScene() {
        return scene;
    }
}