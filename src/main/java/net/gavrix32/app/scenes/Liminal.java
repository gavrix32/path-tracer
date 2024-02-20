package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Plane;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Liminal {
    public static Scene getScene() {
        Scene scene = new Scene();
        scene.setCamera(new Camera().setPos(0, 100, 50));
        scene.setPlane(new Plane(new Vector3f(1),
                new Material(0, 1, true), true));
        scene.addBoxes(
                new Box(new Vector3f(0, 200, 10000), // Roof
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(0, 1, true),
                        new Vector3f(100, 0, 10000)),
                new Box(new Vector3f(-100, 100, 10000), // Left wall
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(0, 1, true),
                        new Vector3f(0, 100, 10000)),
                new Box(new Vector3f(100, 100, 10000), // Right wall
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(0, 1, true),
                        new Vector3f(0, 100, 10000)),
                new Box(new Vector3f(0, 100, 0), // Back wall
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(0, 1, true),
                        new Vector3f(100, 100, 0)),
                new Box(new Vector3f(0, 199.99f, 200), // Light source
                        new Vector3f(),
                        new Vector3f(1),
                        new Material(5, 1, true),
                        new Vector3f(20, 0, 20))
        );
        return scene;
    }
}
