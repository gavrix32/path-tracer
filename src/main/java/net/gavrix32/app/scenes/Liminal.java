package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.linearmath.Vector3f;
import net.gavrix32.engine.objects.Box;
import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.objects.Plane;

public class Liminal {
    private final Scene scene;

    public Liminal() {
        scene = new Scene();
        scene.setName("Liminal");
        scene.setCamera(new Camera().setPosition(0, 100, 50));
        scene.setPlane(new Plane(true).setFirstColor(0.8f, 0.8f, 0.8f));
        scene.setSky("textures/sky/HDR_111_Parking_Lot_2_Env.hdr").setMaterial(false, 0, 1, 1, false);
        scene.addBoxes(
                new Box(new Vector3f(0, 200, 10000), // Roof
                        new Vector3f(),
                        new Vector3f(0.8f),
                        new Vector3f(100, 0, 10000), new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(-100, 100, 10000), // Left wall
                        new Vector3f(),
                        new Vector3f(0.8f),
                        new Vector3f(0, 100, 10000), new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(100, 100, 10000), // Right wall
                        new Vector3f(),
                        new Vector3f(0.8f),
                        new Vector3f(0, 100, 10000), new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(0, 100, 0), // Back wall
                        new Vector3f(),
                        new Vector3f(0.8f),
                        new Vector3f(100, 100, 0), new Material(false, 0, 1, 1, false)
                ),
                new Box(new Vector3f(0, 199.99f, 200), // Light source
                        new Vector3f(),
                        new Vector3f(0.8f),
                        new Vector3f(20, 0, 20), new Material(false, 5, 1, 1, false)
                )
        );
    }
    public Scene getScene() {
        return scene;
    }
}
