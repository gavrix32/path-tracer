package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.graphics.Sky;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Plane;
import net.gavrix32.engine.shapes.Sphere;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RGBRoom {
    public static Scene scene = new Scene(
            new Camera().setPos(50, 100, -99),
            new Plane(new Vector4f(), new Vector3f(1), new Material(0, 0), false),
            new Sky(new Vector3f(0)),
            new Sphere[] {
            },
            new Box[] {
                    new Box(new Vector3f(-30, 100, 90),     // Red light wall
                            new Vector3f(0, 0, 0),
                            new Vector3f(1, 0, 0),
                            new Material(1, 0),
                            new Vector3f(20, 100, 10)),
                    new Box(new Vector3f(10, 100, 80),      // Wall
                            new Vector3f(0, 0, 0),
                            new Vector3f(1),
                            new Material(0, 0),
                            new Vector3f(20, 100, 20)),
                    new Box(new Vector3f(50, 100, 90),      // Green light wall
                            new Vector3f(0, 0, 0),
                            new Vector3f(0, 1, 0),
                            new Material(1, 0),
                            new Vector3f(20, 100, 10)),
                    new Box(new Vector3f(90, 100, 80),      // Wall
                            new Vector3f(0, 0, 0),
                            new Vector3f(1),
                            new Material(0, 0),
                            new Vector3f(20, 100, 20)),
                    new Box(new Vector3f(130, 100, 90),     // Blue light wall
                            new Vector3f(0, 0, 0),
                            new Vector3f(0, 0, 1),
                            new Material(1, 0),
                            new Vector3f(20, 100, 10)),
                    new Box(new Vector3f(50, 200, 0),
                            new Vector3f(0, 0, 0),
                            new Vector3f(1),
                            new Material(0, 0),
                            new Vector3f(100, 0, 100)),
                    new Box(new Vector3f(50, 0, 0),
                            new Vector3f(0, 0, 0),
                            new Vector3f(1),
                            new Material(0, 0),
                            new Vector3f(100, 0, 100)),
                    new Box(new Vector3f(-50, 100, 0),
                            new Vector3f(0, 0, 0),
                            new Vector3f(1),
                            new Material(0, 0),
                            new Vector3f(0, 100, 100)),
                    new Box(new Vector3f(150, 100, 0),
                            new Vector3f(0, 0, 0),
                            new Vector3f(1),
                            new Material(0, 0),
                            new Vector3f(0, 100, 100)),
                    new Box(new Vector3f(50, 100, -100),
                            new Vector3f(0, 0, 0),
                            new Vector3f(1),
                            new Material(0, 0),
                            new Vector3f(100, 100, 0))
            });
}