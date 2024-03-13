package net.gavrix32.app.scenes;

import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.objects.Plane;
import net.gavrix32.engine.objects.Sphere;
import org.joml.Vector3f;

public class RGBSpheres {
    private final Scene scene;

    public RGBSpheres() {
        scene = new Scene();
        scene.setCamera(new Camera().setPosition(50, 20, -80));
        scene.setPlane(new Plane(false));
        scene.addSpheres(
                new Sphere(
                        new Vector3f(0, 20, 0),
                        new Vector3f(0),
                        new Vector3f(1, 0, 0),
                        new Material(true, 1, 1, 1, false), 20),
                new Sphere(
                        new Vector3f(50, 20, 0),
                        new Vector3f(0),
                        new Vector3f(0, 1, 0),
                        new Material(true, 1, 0, 1, false), 20),
                new Sphere(
                        new Vector3f(100, 20, 0),
                        new Vector3f(0),
                        new Vector3f(0, 0, 1),
                        new Material(true, 1, 1, 1, false), 20)
        );
    }
    public Scene getScene() {
        return scene;
    }
}