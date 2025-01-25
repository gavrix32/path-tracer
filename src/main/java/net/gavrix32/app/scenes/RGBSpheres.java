package net.gavrix32.app.scenes;

import net.gavrix32.engine.linearmath.Vector3f;
import net.gavrix32.engine.objects.Box;
import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.objects.Plane;

import java.util.Random;

public class RGBSpheres {
    private final Scene scene;

    public RGBSpheres() {
        scene = new Scene();
        scene.setName("RGB Spheres");
        scene.setCamera(new Camera().setPosition(500, 250, 5));
        scene.setPlane(new Plane(false).setScale(200).setSecondColor(0.5f, 0.5f, 0.5f));
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
                        new Vector3f(0.5f),
                        new Vector3f(0, 250, 500),
                        new Material(false, 0, 1, 1, false)
                ),
                // Right
                new Box(
                        new Vector3f(1000, 250, 500),
                        new Vector3f(),
                        new Vector3f(0.5f),
                        new Vector3f(0, 250, 500),
                        new Material(false, 0, 1, 1, false)
                ),
                // Front
                new Box(
                        new Vector3f(500, 250, 1000),
                        new Vector3f(),
                        new Vector3f(0.5f),
                        new Vector3f(500, 250, 0),
                        new Material(false, 0, 1, 1, false)
                ),
                new Box(
                        new Vector3f(500, 250, 0),
                        new Vector3f(),
                        new Vector3f(0.5f),
                        new Vector3f(500, 250, 0),
                        new Material(false, 0, 1, 1, false)
                ),
                new Box(
                        new Vector3f(500, 500, 500),
                        new Vector3f(),
                        new Vector3f(0.5f),
                        new Vector3f(500, 0, 500),
                        new Material(false, 0, 1, 1, false)
                )
        );
        Random rnd = new Random();
//        long seed = rnd.nextLong();
        //System.out.println("seed: " + seed);
        // Beautiful seeds: 8974213222167044418L
//        rnd.setSeed(8974213222167044418L);
        for (int i = 0; i < 64; i++) {
            /*scene.addSpheres(
                    new Sphere(
                            new Vector3f(rnd.nextFloat() * 800 + 100, rnd.nextFloat() * 300 + 100, rnd.nextFloat() * 500 + 400),
                            new Vector3f(0),
                            new Vector3f(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat()),
                            new Material(true, rnd.nextBoolean() ? 5 : 0, 1, 1.5f, false), 35)
            );*/
            int materialType = rnd.nextInt(1, 4);
            Material mat = null;
            switch (materialType) {
                case 1 -> mat = new Material(true, rnd.nextFloat(3f, 8f), 0.0f, 0.0f, false); // Emit
                case 2 -> mat = new Material(true, 0.0f, 0.0f, 0.0f, false); // Mirror
                case 3 -> mat = new Material(true, 0.0f, 0.0f, 1.5f, true); // Glass
            }
            scene.addBoxes(
                    new Box(
                            new Vector3f(rnd.nextFloat() * 1000, rnd.nextFloat() * 500, rnd.nextFloat() * 700 + 300),
                            new Vector3f(rnd.nextFloat() * 360, rnd.nextFloat() * 360, rnd.nextFloat() * 360),
                            new Vector3f(rnd.nextFloat(0.5f, 1.0f), rnd.nextFloat(0.0f, 0.2f), rnd.nextFloat(0.5f, 1.0f)),
                            new Vector3f(rnd.nextFloat() * 50, rnd.nextFloat() * 50, rnd.nextFloat() * 50),
                            mat
            ));
        }
    }
    public Scene getScene() {
        return scene;
    }
}