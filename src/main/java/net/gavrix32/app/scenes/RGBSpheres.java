package net.gavrix32.app.scenes;

import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Box;
import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.objects.Plane;
import net.gavrix32.engine.objects.Sphere;

import java.util.Random;

public class RGBSpheres {
    private final Scene scene;

    public RGBSpheres() {
        scene = new Scene();
        scene.setCamera(new Camera().setPosition(500, 250, 1));
        scene.setPlane(new Plane(false).setScale(200));
        scene.setSky("textures/sky/kiara_1_dawn_2k.hdr");
        /*scene.addSpheres(
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
        );*/
        scene.addBoxes(
                // Left
                new Box(
                        new Vector3f(0, 250, 500),
                        new Vector3f(),
                        new Vector3f(1),
                        new Vector3f(0, 250, 500),
                        new Material(true, 0, 1, 1, false)
                ),
                // Right
                new Box(
                        new Vector3f(1000, 250, 500),
                        new Vector3f(),
                        new Vector3f(1),
                        new Vector3f(0, 250, 500),
                        new Material(true, 0, 1, 1, false)
                ),
                // Front
                new Box(
                        new Vector3f(500, 250, 1000),
                        new Vector3f(),
                        new Vector3f(1),
                        new Vector3f(500, 250, 0),
                        new Material(true, 0, 1, 1, false)
                ),
                new Box(
                        new Vector3f(500, 250, 0),
                        new Vector3f(),
                        new Vector3f(1),
                        new Vector3f(500, 250, 0),
                        new Material(true, 0, 1, 1, false)
                ),
                new Box(
                        new Vector3f(500, 500, 500),
                        new Vector3f(),
                        new Vector3f(1),
                        new Vector3f(500, 0, 500),
                        new Material(true, 0, 1, 1, false)
                )
        );
        Random random = new Random();
        for (int i = 0; i < 64; i++) {
            /*scene.addSpheres(
                    new Sphere(
                            new Vector3f(random.nextFloat() * 800 + 100, random.nextFloat() * 300 + 100, random.nextFloat() * 500 + 400),
                            new Vector3f(0),
                            new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat()),
                            new Material(true, random.nextBoolean() ? 5 : 0, 1, 1.5f, false), 35)
            );*/
            scene.addBoxes(
                    new Box(
                            new Vector3f(random.nextFloat() * 1000, random.nextFloat() * 500, random.nextFloat() * 700 + 300),
                            new Vector3f(random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360),
                            new Vector3f(random.nextFloat(0.0f, 1.0f), random.nextFloat(0.0f, 1.0f), random.nextFloat(0.0f, 1.0f)),
                            new Vector3f(random.nextFloat() * 50, random.nextFloat() * 50, random.nextFloat() * 50),
                            new Material(true, random.nextBoolean() ? 5 : 0, 0, 1, false)
            ));
        }
    }
    public Scene getScene() {
        return scene;
    }
}