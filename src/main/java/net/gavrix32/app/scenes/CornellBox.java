package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.graphics.Sky;
import net.gavrix32.engine.math.Vec3;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Sphere;

public class CornellBox {
    public static Scene scene = new Scene(
            new Camera().setPos(50, 80, -99),
            new Sky(new String[] {
                    "textures/skybox/iceriver/posx.jpg",
                    "textures/skybox/iceriver/negx.jpg",
                    "textures/skybox/iceriver/posy.jpg",
                    "textures/skybox/iceriver/negy.jpg",
                    "textures/skybox/iceriver/posz.jpg",
                    "textures/skybox/iceriver/negz.jpg"
            }),
            new Sphere[] {
                    new Sphere(new Vec3(0, 20, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 0), 20),
                    new Sphere(new Vec3(50, 20, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 0.5f), 20),
                    new Sphere(new Vec3(100, 20, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1),
                            new Material(0, 1), 20)
            },
            new Box[] {
                    new Box(new Vec3(50, 100, 100),
                            new Vec3(0, 0, 0),
                            new Vec3(1, 1, 1),
                            new Material(0, 0),
                            new Vec3(100, 100, 0)),
                    new Box(new Vec3(50, 200, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1, 1, 1),
                            new Material(0, 0),
                            new Vec3(100, 0, 100)),
                    new Box(new Vec3(-50, 100, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1, 0, 0),
                            new Material(0, 0),
                            new Vec3(0, 100, 100)),
                    new Box(new Vec3(150, 100, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(0, 1, 0),
                            new Material(0, 0),
                            new Vec3(0, 100, 100)),
                    new Box(new Vec3(50, 199.999f, 0),
                            new Vec3(0, 0, 0),
                            new Vec3(1, 1, 1),
                            new Material(5, 0),
                            new Vec3(50, 0.1f, 50)),
                    new Box(new Vec3(50, 100, -100),
                            new Vec3(0, 0, 0),
                            new Vec3(1, 1, 1),
                            new Material(0, 0),
                            new Vec3(100, 100, 0))
            });
}