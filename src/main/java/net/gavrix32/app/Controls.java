package net.gavrix32.app;

import net.gavrix32.engine.Engine;
import net.gavrix32.engine.gui.Gui;
import net.gavrix32.engine.gui.Viewport;
import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.io.Input;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.lwjgl.glfw.GLFW.*;

public class Controls {
    static {
        glfwSetKeyCallback(Window.get(), (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) Window.close();
            if (key == GLFW_KEY_F11 && action == GLFW_RELEASE) Window.toggleFullscreen();
            if (key == GLFW_KEY_F1 && action == GLFW_RELEASE) Gui.toggle();
            if (key == GLFW_KEY_F2 && action == GLFW_RELEASE) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
                LocalDateTime now = LocalDateTime.now();
                Utils.takeScreenshot("screenshots/" + dtf.format(now) + ".png");
            }
        });
        glfwSetMouseButtonCallback(Window.get(), (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE && Viewport.cursorInViewport()) Window.toggleCursor();
        });
    }

    public static void update(Camera camera) {
        Input.update();
        float speed = 200.0f * Engine.getDeltaTime();
        if (Input.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            speed *= 2;
        }
        if (Input.isKeyDown(GLFW_KEY_W)) {
            camera.moveZ(-speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_S)) {
            camera.moveZ(speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_A)) {
            camera.moveX(-speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_D)) {
            camera.moveX(speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_SPACE)) {
            camera.moveY(speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            camera.moveY(-speed);
            Renderer.resetAccFrames();
        }
        if (!Window.isCursorVisible()) {
            camera.rotateY(Input.getDeltaY() * 0.1f);
            camera.rotateX(Input.getDeltaX() * -0.1f);
            if (Input.getDeltaX() != 0 || Input.getDeltaY() != 0) Renderer.resetAccFrames();
        }
    }
}