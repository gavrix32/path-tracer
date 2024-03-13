package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.objects.Box;
import org.joml.Vector3f;

public class OpenBox {
    private final Scene scene;

    public OpenBox() {
        scene = new Scene();
        scene.setCamera(new Vector3f(0, 0, -350)).setFov(45);
        scene.setSky("textures/sky/HDR_111_Parking_Lot_2_Env.hdr");
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
                        new Vector3f(0, 25, 0),
                        new Vector3f(1),
                        new Vector3f(25, 50, 25),
                        new Material(true, 0, 1, 1, false)
                ),
                // Short box
                new Box(new Vector3f(45, -75, -25),
                        new Vector3f(0, -25, 0),
                        new Vector3f(1),
                        new Vector3f(25),
                        new Material(true, 0, 1, 1, false)
                )
        );
    }

    public Scene getScene() {
        return scene;
    }
}