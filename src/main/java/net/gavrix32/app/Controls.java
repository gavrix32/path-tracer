package net.gavrix32.app;

import net.gavrix32.engine.Engine;
import net.gavrix32.engine.gui.Gui;
import net.gavrix32.engine.gui.Viewport;
import net.gavrix32.engine.io.Key;
import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.io.Input;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.utils.Utils;

import java.io.File;
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
                File screenshotsDir = new File(System.getProperty("user.dir"), "screenshots");
                if (!screenshotsDir.exists()) screenshotsDir.mkdir();
                Utils.takeScreenshot(System.getProperty("user.dir") + "/screenshots/" + dtf.format(now) + ".png");
            }
        });
        glfwSetMouseButtonCallback(Window.get(), (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE && Viewport.cursorInViewport()) Window.toggleCursor();
        });
    }

    public static void update(Camera camera) {
        Input.update();
        float speed = 200.0f * Engine.getDeltaTime();
        if (Input.isKeyDown(Key.LEFT_CONTROL)) {
            speed *= 2;
        }
        if (Input.isKeyDown(Key.W)) {
            camera.moveZ(speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Key.S)) {
            camera.moveZ(-speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Key.A)) {
            camera.moveX(-speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Key.D)) {
            camera.moveX(speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Key.SPACE)) {
            camera.moveY(speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Key.LEFT_SHIFT)) {
            camera.moveY(-speed);
            Renderer.resetAccFrames();
        }
        if (!Window.isCursorVisible()) {
            camera.rotateX(Input.getDeltaY() * -0.1f);
            camera.rotateY(Input.getDeltaX() * 0.1f);
            if (Input.getDeltaX() != 0 || Input.getDeltaY() != 0) Renderer.resetAccFrames();
        }
    }
}