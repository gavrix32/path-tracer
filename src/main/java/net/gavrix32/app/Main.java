package net.gavrix32.app;

import net.gavrix32.app.scenes.CornellBox;
import net.gavrix32.app.scenes.Spheres;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.IApplication;
import net.gavrix32.engine.Utils;
import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.io.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IApplication {

    @Override
    public void init() {
        Window.init("Ray Tracing", 1280, 720);
        glfwSetKeyCallback(Window.get(), (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) System.exit(0);
            if (key == GLFW_KEY_F && action == GLFW_RELEASE) Window.toggleFullscreen();
            if (key == GLFW_KEY_P && action == GLFW_RELEASE) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
                LocalDateTime now = LocalDateTime.now();
                Utils.takeScreenshot("screenshots/" + dtf.format(now) + ".png");
            }
        });
        glfwSetMouseButtonCallback(Window.get(), (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE) Window.toggleCursor();
        });
        CornellBox.scene.getCamera().setPos(50, 50, -120);
        Spheres.scene.getCamera().setPos(50, 20, -80);
        Renderer.init();
        RendererSettings.init();
    }

    @Override
    public void update() {
        Controls.update(Spheres.scene.getCamera());
        Renderer.render(Spheres.scene);
        RendererSettings.update();
        Window.update();
    }

    public static void main(String[] args) {
        Engine.run(new Main());
    }
}