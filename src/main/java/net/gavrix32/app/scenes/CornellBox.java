package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.graphics.Sky;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Sphere;
import org.joml.Vector3f;

public class CornellBox {
    public static Scene getScene() {
        Scene scene = new Scene();
        scene.setCamera(new Camera().setPos(50, 80, -99));
        scene.setSky(new Sky(new String[] {
                "textures/skybox/iceriver/posx.jpg",
                "textures/skybox/iceriver/negx.jpg",
                "textures/skybox/iceriver/posy.jpg",
                "textures/skybox/iceriver/negy.jpg",
                "textures/skybox/iceriver/posz.jpg",
                "textures/skybox/iceriver/negz.jpg"
        }));
        scene.addBoxes(
                new Box(new Vector3f(50, 100, 100), // Front wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Material(0, 1, true),
                        new Vector3f(100, 100, 0)),
                new Box(new Vector3f(50, 200, 0), // Roof
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Material(0, 1, true),
                        new Vector3f(100, 0, 100)),
                new Box(new Vector3f(-50, 100, 0), // Left wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 0, 0),
                        new Material(0, 01, true),
                        new Vector3f(0, 100, 100)),
                new Box(new Vector3f(150, 100, 0), // Right wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 1, 0),
                        new Material(0, 1, true),
                        new Vector3f(0, 100, 100)),
                new Box(new Vector3f(50, 199.999f, 0), // Light
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Material(5, 1, true),
                        new Vector3f(50, 0.1f, 50)),
                new Box(new Vector3f(50, 100, -100), // Back wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(1, 1, 1),
                        new Material(0, 1, true),
                        new Vector3f(100, 100, 0))
        );
        scene.addSpheres(
                new Sphere(
                        new Vector3f(0, 20, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(0, 1, true), 20),
                new Sphere(
                        new Vector3f(50, 20, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(0, 0.5f, true), 20),
                new Sphere(
                        new Vector3f(100, 20, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(1),
                        new Material(0, 0, true), 20)
        );
        return scene;
    }
}