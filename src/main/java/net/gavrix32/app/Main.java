package net.gavrix32.app;

import net.gavrix32.app.scenes.*;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.IApp;
import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.io.*;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IApp {
    private CornellBox cornellBox;
    private RGBRoom rgbRoom;
    private RGBSpheres rgbSpheres;
    private Spheres spheres;
    private Liminal liminal;

    @Override
    public void init() {
        Window.init("Ray Tracing", 1280, 720);
        //GLUtil.setupDebugMessageCallback();

        glfwSetMouseButtonCallback(Window.get(), (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE) Window.toggleCursor();
        });
        Renderer.init();
        cornellBox = new CornellBox();
        rgbRoom = new RGBRoom();
        rgbSpheres = new RGBSpheres();
        spheres = new Spheres();
        liminal = new Liminal();
        GuiRenderer.init(cornellBox.getScene(), rgbRoom.getScene(), rgbSpheres.getScene(), spheres.getScene(), liminal.getScene());
        Renderer.setScene(cornellBox.getScene());
    }

    @Override
    public void update() {
        Controls.update(Renderer.getScene().getCamera());
        Renderer.render();
        GuiRenderer.update();
        Window.update();
    }

    public static void main(String[] args) {
        Engine.run(new Main());
    }
}