package net.gavrix32.app.scenes;

import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.objects.Box;
import net.gavrix32.engine.objects.Sphere;
import org.joml.Vector3f;

public class Spheres {
    private final Scene scene;

    public Spheres() {
        scene = new Scene();
        scene.setCamera(new Camera().setPosition(0, 100, -200));
        scene.setSky("textures/sky/lauter_waterfall_2k.hdr");
        scene.addBox(new Box(
                new Vector3f(),
                new Vector3f(),
                new Vector3f(1),
                new Vector3f(250, 0, 100), new Material(true, 0, 1, 1, false)
        ));
        scene.addSpheres(
                new Sphere(
                        new Vector3f(-120, 50, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(true, 0, 1, 1, false), 50),
                new Sphere(
                        new Vector3f(0, 50, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(true, 0, 0.5f, 1, false), 50),
                new Sphere(
                        new Vector3f(120, 50, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(true, 0, 0, 1, false), 50)
        );
    }
    public Scene getScene() {
        return scene;
    }
}