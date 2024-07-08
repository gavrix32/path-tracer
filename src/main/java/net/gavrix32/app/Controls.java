package net.gavrix32.app;

import net.gavrix32.engine.Engine;
import net.gavrix32.engine.graphics.Config;
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
            //if (key == GLFW_KEY_F1 && action == GLFW_RELEASE) Gui.toggle();
            if (key == GLFW_KEY_F2 && action == GLFW_RELEASE) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
                LocalDateTime now = LocalDateTime.now();
                File screenshotsDir = new File(System.getProperty("user.dir"), "screenshots");
                if (!screenshotsDir.exists()) screenshotsDir.mkdir();
                Utils.takeScreenshot(System.getProperty("user.dir") + "/screenshots/" + dtf.format(now) + ".png");
            }
        });
        glfwSetMouseButtonCallback(Window.get(), (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE /*&& Viewport.cursorInViewport()*/) Window.toggleCursor();
        });
    }

    public static void update() {
        Input.update();
        float speed = 200.0f * Engine.getDeltaTime();
        if (Input.isKeyDown(Config.getKey("sprint"))) {
            speed *= 2;
        }
        Camera camera = Renderer.getScene().getCamera();
        if (Input.isKeyDown(Config.getKey("move_forward"))) {
            camera.moveZ(speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Config.getKey("move_backward"))) {
            camera.moveZ(-speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Config.getKey("move_left"))) {
            camera.moveX(-speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Config.getKey("move_right"))) {
            camera.moveX(speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Config.getKey("move_up"))) {
            camera.moveY(speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(Config.getKey("move_down"))) {
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