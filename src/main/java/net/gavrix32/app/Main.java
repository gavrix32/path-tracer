package net.gavrix32.app;

import net.gavrix32.app.scenes.*;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.IApp;
import net.gavrix32.engine.gui.Gui;
import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.gui.SceneEditor;
import net.gavrix32.engine.io.*;
import net.gavrix32.engine.math.Matrix4f;
import net.gavrix32.engine.utils.Logger;

import java.util.ArrayList;

public class Main implements IApp {
    private final ArrayList<Scene> scenes = new ArrayList<>();
    private final ArrayList<String> names = new ArrayList<>();
    private CornellBox cornellBox;
    private RGBRoom rgbRoom;
    private RGBSpheres rgbSpheres;
    private Spheres spheres;
    private Liminal liminal;
    private OpenBox openBox;

    @Override
    public void init() {
        Window.init("Path Tracing", 1280, 720);
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

        openBox = new OpenBox();
        scenes.add(openBox.getScene());
        names.add("Open Box");

        Renderer.init();
        SceneEditor.setDefaultScene(5);

        Matrix4f m1 = new Matrix4f(new float[] {
                4, 2, 0, 0,
                0, 8, 1, 0,
                0, 1, 0, 0,
                0, 0, 0, 1
        });
        Matrix4f m2 = new Matrix4f(new float[] {
                4, 2, 1, 0,
                2, 0, 4, 0,
                9, 4, 2, 0,
                0, 0, 0, 1
        });
        Logger.info(System.lineSeparator() + System.lineSeparator() + m1.mul(m2));
        /*
        20.0 8.0 12.0 0.0
        25.0 4.0 34.0 0.0
        2.0 0.0 4.0 0.0
        0.0 0.0 0.0 1.0
         */
        //System.out.println();
        //System.out.println(new Matrix4f().perspective(70, (float) 1820 / 720, 0.01f, 100.0f));
    }

    @Override
    public void update() {
        Controls.update(Renderer.getScene().getCamera());
        Renderer.render();
        Gui.update(scenes, names);
        Window.update();
    }

    public static void main(String[] args) {
        Engine.run(new Main());
    }
}