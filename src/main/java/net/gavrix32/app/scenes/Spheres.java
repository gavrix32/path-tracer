package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Sphere;

public class Spheres {
    public static Scene scene = new Scene(
            new Camera(),
            new Sphere[] {
                    new Sphere(new Vector3f(0, 20, 0), new Vector3f(1, 0.5f, 0), new Material(1, 0), 20),
                    new Sphere(new Vector3f(50, 20, 0), new Vector3f(1, 1, 1), new Material(0, 1), 20),
                    new Sphere(new Vector3f(100, 20, 0), new Vector3f(0.5f, 1, 0), new Material(1, 0), 20)
            },
            new Box[] {}
    );
}