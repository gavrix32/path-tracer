package net.gavrix32.app;

import net.gavrix32.app.scenes.*;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.IApp;
import net.gavrix32.engine.editor.Editor;
import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.io.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import java.util.ArrayList;

public class Main implements IApp {
    private ArrayList<Scene> scenes = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private CornellBox cornellBox;
    private RGBRoom rgbRoom;
    private RGBSpheres rgbSpheres;
    private Spheres spheres;
    private Liminal liminal;

    /*private AIScene aiScene;
    private AIMesh mesh;
    private ArrayList<Float> positions = new ArrayList<>();
    private ArrayList<Float> normals = new ArrayList<>();*/

    @Override
    public void init() {
        Window.init("Ray Tracing", 1280, 720);
        Window.setFullscreen(true);
        //GLUtil.setupDebugMessageCallback();
        cornellBox = new CornellBox();
        scenes.add(cornellBox.getScene());
        names.add("Cornell Box");
        rgbRoom = new RGBRoom();
        scenes.add(rgbRoom.getScene());
        names.add("RGB Room");
        rgbSpheres = new RGBSpheres();
        scenes.add(rgbSpheres.getScene());
        names.add("RGB Spheres");
        spheres = new Spheres();
        scenes.add(spheres.getScene());
        names.add("Spheres");
        liminal = new Liminal();
        scenes.add(liminal.getScene());
        names.add("Liminal");
        Renderer.init();
        Renderer.setScene(cornellBox.getScene());

        /*aiScene = Assimp.aiImportFile("/home/gavrix32/IdeaProjects/ray-tracing-engine/src/main/resources/toilet_paper/scene.gltf", Assimp.aiProcess_Triangulate);
        PointerBuffer buffer = aiScene.mMeshes();
        for (int i = 0; i < buffer.limit(); i++) mesh = AIMesh.create(buffer.get(i));
        AIVector3D.Buffer positionBuffer = mesh.mVertices();
        System.out.println(positionBuffer.limit());
        for (int i = 0; i < positionBuffer.limit(); i++) {
            AIVector3D vector = positionBuffer.get(i);
            positions.add(vector.x());
            positions.add(vector.y());
            positions.add(vector.z());
        }
        AIVector3D.Buffer normalBuffer = mesh.mNormals();
        for (int i = 0; i < normalBuffer.limit(); i++) {
            AIVector3D normal = normalBuffer.get(i);
            normals.add(normal.x());
            normals.add(normal.y());
            normals.add(normal.z());
        }*/
    }

    @Override
    public void update() {
        Controls.update(Renderer.getScene().getCamera());
        Renderer.render(/*positions, normals*/);
        Editor.update(scenes, names);
        Window.update();
    }

    public static void main(String[] args) {
        Engine.run(new Main());
    }
}