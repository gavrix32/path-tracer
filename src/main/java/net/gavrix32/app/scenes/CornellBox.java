package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Sphere;

public class CornellBox {
    public static Scene scene = new Scene(
            new Camera(),
            new Sphere[] {
                    new Sphere(new Vector3f(0, 20, 0), new Vector3f(0, 0.5f, 1), new Material(0, 0.5f), 20),
                    new Sphere(new Vector3f(50, 20, 0), new Vector3f(1, 1, 1), new Material(0, 1), 20),
                    new Sphere(new Vector3f(100, 20, 0), new Vector3f(1, 0.5f, 0), new Material(0, 0.5f), 20)
            },
            new Box[] {
                    new Box(new Vector3f(50, 100, 100), new Vector3f(1, 1, 1), new Material(0, 0), new Vector3f(100, 100, 0)),
                    new Box(new Vector3f(50, 200, 0), new Vector3f(1, 1, 1), new Material(0, 0), new Vector3f(100, 0, 100)),
                    new Box(new Vector3f(-50, 100, 0), new Vector3f(1, 0, 0), new Material(0, 0), new Vector3f(0, 100, 100)),
                    new Box(new Vector3f(150, 100, 0), new Vector3f(0, 1, 0), new Material(0, 0), new Vector3f(0, 100, 100)),
                    new Box(new Vector3f(50, 200, 0), new Vector3f(1, 1, 1), new Material(10, 0), new Vector3f(50, 1, 50))
            }
    );
}