package net.gavrix32.app.scenes;

import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.objects.Triangle;

public class BVHTest {
    private final Scene scene;
    private static Model model;
    public static BoundingVolumeHierarchy bvh;

    public BVHTest() {
        scene = new Scene();
        scene.setName("BVH Test");
        scene.setCamera(new Vector3f(20, 0, -100));
        scene.setSky("textures/sky/HDR_041_Path_Env.hdr").setMaterial(true, 1, 1, 1, false);
        model = new Model("models/bunny.obj", 100.0f);
        for (int i = 0; i < model.getTriangles().size(); i++) {
            Triangle triangle = model.getTriangles().get(i);
            triangle.setColor(new Vector3f(1));
            triangle.setMaterial(new Material(true, 0, 1.0f, 1, false));
            scene.addTriangle(triangle);
        }
        bvh = new BoundingVolumeHierarchy();
        bvh.build(model.getTriangles());
    }

    public static Model getModel() {
        return model;
    }

    public Scene getScene() {
        return scene;
    }
}