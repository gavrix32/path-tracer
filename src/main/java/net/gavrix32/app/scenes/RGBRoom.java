package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.graphics.Sky;
import net.gavrix32.engine.math.Vec3;
import net.gavrix32.engine.math.Vec4;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Plane;
import net.gavrix32.engine.shapes.Sphere;

public class RGBRoom {
    public static Scene scene = new Scene(
            new Camera().setPos(50, 100, -99),
            new Plane(new Vec4(), new Vec3(1), new Material(0, 0), false),
            new Sky(new Vec3(0)),
            new Sphere[] {
            },
            new Box[] {
                    new Box(new Vec3(-30, 100, 90),     // Red light wall
                            new Vec3(0, 0, 0),
                            new Vec3(1, 0, 0),
                            new Material(1, 0),
                            new Vec3(20, 100, 10)),
                    new Box(new Vec3(10, 100, 80),      // Wall
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 0),
                            new Vec3(20, 100, 20)),
                    new Box(new Vec3(50, 100, 90),      // Green light wall
                            new Vec3(0, 0, 0),
                            new Vec3(0, 1, 0),
                            new Material(1, 0),
                            new Vec3(20, 100, 10)),
                    new Box(new Vec3(90, 100, 80),      // Wall
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 0),
                            new Vec3(20, 100, 20)),
                    new Box(new Vec3(130, 100, 90),     // Blue light wall
                            new Vec3(0, 0, 0),
                            new Vec3(0, 0, 1),
                            new Material(1, 0),
                            new Vec3(20, 100, 10)),
                    new Box(new Vec3(50, 200, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 0),
                            new Vec3(100, 0, 100)),
                    new Box(new Vec3(50, 0, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 0),
                            new Vec3(100, 0, 100)),
                    new Box(new Vec3(-50, 100, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 0),
                            new Vec3(0, 100, 100)),
                    new Box(new Vec3(150, 100, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 0),
                            new Vec3(0, 100, 100)),
                    new Box(new Vec3(50, 100, -100),
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 0),
                            new Vec3(100, 100, 0))
            });
}