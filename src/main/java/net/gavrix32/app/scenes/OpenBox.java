package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Model;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Box;
import net.gavrix32.engine.objects.Triangle;

import java.util.List;

public class OpenBox {
    private final Scene scene;

    public OpenBox() {
        scene = new Scene();
        scene.setCamera(new Vector3f(0, 0, -350)).setFov(45);
        scene.setSky("textures/sky/HDR_111_Parking_Lot_2_Env.hdr").setMaterial(true, 5, 1, 1, false);
        scene.addBoxes(
                // Floor
                new Box(new Vector3f(0, -100, 0),
                        new Vector3f(),
                        new Vector3f(1),
                        new Vector3f(100, 0, 100),
                        new Material(true, 0, 1, 1, false)
                ),
                // Roof
                new Box(new Vector3f(0, 100, 0),
                        new Vector3f(),
                        new Vector3f(1),
                        new Vector3f(100, 0, 100),
                        new Material(true, 0, 1, 1, false)
                ),
                // Left wall
                new Box(new Vector3f(-100, 0, 0),
                        new Vector3f(),
                        new Vector3f(1, 0, 0),
                        new Vector3f(0, 100, 100),
                        new Material(true, 0, 1, 1, false)
                ),
                // Right wall
                new Box(new Vector3f(100, 0, 0),
                        new Vector3f(),
                        new Vector3f(0, 1, 0),
                        new Vector3f(0, 100, 100),
                        new Material(true, 0, 1, 1, false)
                ),
                // Front wall
                new Box(new Vector3f(0, 0, 100),
                        new Vector3f(),
                        new Vector3f(1),
                        new Vector3f(100, 100, 0),
                        new Material(true, 0, 1, 1, false)
                ),
                // Tall box
                new Box(new Vector3f(-45, -50, 25),
                        new Vector3f(0, -25, 0),
                        new Vector3f(1),
                        new Vector3f(25, 50, 25),
                        new Material(true, 0, 1, 1, false)
                ),
                // Short box
                new Box(new Vector3f(45, -75, -25),
                        new Vector3f(0, 25, 0),
                        new Vector3f(1),
                        new Vector3f(25),
                        new Material(true, 0, 1, 1, false)
                )
        );
        /*scene.addTriangles(
                new Triangle(new Vector3f(0, 50, 0), new Vector3f(50, 0, 0), new Vector3f(0, 0, 0)),
                new Triangle(new Vector3f(50, 50, 0), new Vector3f(50, 0, 0), new Vector3f(0, 50, 0)),
                new Triangle(new Vector3f(0, 0, 50), new Vector3f(0, 50, 50), new Vector3f(0, 0, 0)),
                new Triangle(new Vector3f(0, 50, 0), new Vector3f(0, 0, 0), new Vector3f(0, 50, 50)),
                new Triangle(new Vector3f(50, 50, 50), new Vector3f(50, 0, 0), new Vector3f(50, 50, 0)),
                new Triangle(new Vector3f(50, 50, 50), new Vector3f(50, 0, 50), new Vector3f(50, 0, 0)),
                new Triangle(new Vector3f(0, 50, 50), new Vector3f(50, 50, 0), new Vector3f(0, 50, 0)),
                new Triangle(new Vector3f(0, 50, 50), new Vector3f(50, 50, 50), new Vector3f(50, 50, 0)),
                new Triangle(new Vector3f(50, 0, 0), new Vector3f(50, 0, 50), new Vector3f(0, 0, 50)),
                new Triangle(new Vector3f(50, 0, 0), new Vector3f(0, 0, 50), new Vector3f(0, 0, 0)),
                new Triangle(new Vector3f(50, 0, 50), new Vector3f(0, 50, 50), new Vector3f(0, 0, 50)),
                new Triangle(new Vector3f(50, 50, 50), new Vector3f(0, 50, 50), new Vector3f(50, 0, 50))
        );*/
        Model model = new Model("/home/gavrix32/IdeaProjects/path-tracing/src/main/resources/models/Bunny-LowPoly.stl", 0.5f);
        for (int i = 0; i < model.getTriangles().size(); i++) {
            Triangle triangle = model.getTriangles().get(i);
            triangle.setColor(1, 1, 0.5f);
            triangle.setMaterial(new Material(true, 0, 0.3f, 1, false));
            //triangle.setMaterial(new Material(false, 0, 0, 2.2f, true));
            triangle.setRot(90, 0, 0);
            triangle.setPosition(28, 25, -54);
            scene.addTriangle(triangle);
        }
    }

    public Scene getScene() {
        return scene;
    }
}