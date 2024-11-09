package net.gavrix32.app.scenes;

import net.gavrix32.engine.linearmath.Vector3f;
import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.objects.Box;

public class RGBRoom {
    private final Scene scene;

    public RGBRoom() {
        scene = new Scene();
        scene.setName("RGB Room");
        scene.setCamera(new Camera().setPosition(50, 100, -99));
        scene.setSky("textures/sky/HDR_041_Path_Env.hdr");
        scene.addBoxes(
                new Box(new Vector3f(-30, 100, 90),     // Red light wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.7f, 0, 0),
                        new Vector3f(20, 100, 10),
                        new Material(false, 1, 1, 1, false)
                ),
                new Box(new Vector3f(10, 100, 80),      // Wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.7f),
                        new Vector3f(20, 100, 20),
                        new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(50, 100, 90),      // Green light wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 0.7f, 0),
                        new Vector3f(20, 100, 10),
                        new Material(false, 1, 1, 1, false)
                ),
                new Box(new Vector3f(90, 100, 80),      // Wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.7f),
                        new Vector3f(20, 100, 20),
                        new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(130, 100, 90),     // Blue light wall
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 0, 0.7f),
                        new Vector3f(20, 100, 10),
                        new Material(false, 1, 1, 1, false)
                ),
                new Box(new Vector3f(50, 200, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.7f),
                        new Vector3f(100, 0, 100),
                        new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(50, 0, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.7f),
                        new Vector3f(100, 0, 100),
                        new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(-50, 100, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.7f),
                        new Vector3f(0, 100, 100),
                        new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(150, 100, 0),
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.7f),
                        new Vector3f(0, 100, 100),
                        new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(50, 100, -100),
                        new Vector3f(0, 0, 0),
                        new Vector3f(0.7f),
                        new Vector3f(100, 100, 0),
                        new Material(false, 0, 1, 1, false)
                )
        );
    }

    public Scene getScene() {
        return scene;
    }
}