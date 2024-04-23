package net.gavrix32.app.scenes;

import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.objects.Box;
import net.gavrix32.engine.objects.Plane;
import net.gavrix32.engine.objects.Sphere;

public class CornellBox {
    private final Scene scene;

    public CornellBox() {
        scene = new Scene();
        scene.setCamera(new Camera().setPosition(50, 100, -99));
        scene.setSky("textures/sky/industrial_sunset_puresky_2k.hdr");
        scene.setPlane(new Plane(true));
        scene.addBoxes(
                new Box(new Vector3f(50, 100, 200), // Front wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Vector3f(100, 100, 0), new Material(true, 0, 1, 1, false)
                ),
                new Box(new Vector3f(50, 100, -100), // Back wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Vector3f(100, 100, 0), new Material(true, 0, 1, 1, false)
                ),
                new Box(new Vector3f(-50, 100, 50), // Left wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 0, 0),
                        new Vector3f(0, 100, 150), new Material(true, 0, 1, 1, false)
                ),
                new Box(new Vector3f(150, 100, 50), // Right wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 1, 0),
                        new Vector3f(0, 100, 150), new Material(true, 0, 1, 1, false)
                ),
                new Box(new Vector3f(50, 200, 50), // Roof
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Vector3f(100, 0, 150), new Material(true, 0, 1, 1, false)
                ),
                new Box(new Vector3f(50, 0, 50), // Floor
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Vector3f(100, 0, 150), new Material(true, 0, 1, 1, false)
                ),
                new Box(new Vector3f(50, 199.999f, 50), // Light
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Vector3f(25, 0.1f, 25), new Material(true, 50, 1, 1, false)
                )

        );
        scene.addSpheres(
                new Sphere(
                        new Vector3f(0, 20, 75),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(true, 0, 1, 1, false), 20),
                new Sphere(
                        new Vector3f(50, 20, 75),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(true, 0, 0.5f, 1, false), 20),
                new Sphere(
                        new Vector3f(100, 20, 75),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(true, 0, 0, 1, false), 20)
        );
    }
    public Scene getScene() {
        return scene;
    }
}