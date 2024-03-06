package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Plane;
import org.joml.Vector3f;

public class Liminal {
    private final Scene scene;

    public Liminal() {
        scene = new Scene();
        scene.setCamera(new Camera().setPos(0, 100, 50));
        scene.setPlane(new Plane());
        scene.addBoxes(
                new Box(new Vector3f(0, 200, 10000), // Roof
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(true, 0, 1, 1),
                        new Vector3f(100, 0, 10000)),
                new Box(new Vector3f(-100, 100, 10000), // Left wall
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(true, 0, 1, 1),
                        new Vector3f(0, 100, 10000)),
                new Box(new Vector3f(100, 100, 10000), // Right wall
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(true, 0, 1, 1),
                        new Vector3f(0, 100, 10000)),
                new Box(new Vector3f(0, 100, 0), // Back wall
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(true, 0, 1, 1),
                        new Vector3f(100, 100, 0)),
                new Box(new Vector3f(0, 199.99f, 200), // Light source
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(true, 5, 1, 1),
                        new Vector3f(20, 0, 20))
        );
    }
    public Scene getScene() {
        return scene;
    }
}
