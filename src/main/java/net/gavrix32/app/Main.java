package net.gavrix32.app;

import net.gavrix32.app.scenes.*;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.IApp;
import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.io.*;
import net.gavrix32.engine.utils.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IApp {

    @Override
    public void init() {
        Window.init("Ray Tracing", 1280, 720);
        //GLUtil.setupDebugMessageCallback();

        glfwSetMouseButtonCallback(Window.get(), (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE) Window.toggleCursor();
        });
        Renderer.init();
        GuiRenderer.init();
        Renderer.setScene(CornellBox.scene);
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