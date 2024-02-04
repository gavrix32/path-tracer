package net.gavrix32.app;

import net.gavrix32.engine.Engine;
import net.gavrix32.engine.graphics.Camera;
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
            if (key == GLFW_KEY_F1 && action == GLFW_RELEASE) GuiRenderer.toggle();
            if (key == GLFW_KEY_F2 && action == GLFW_RELEASE) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
                LocalDateTime now = LocalDateTime.now();
                Utils.takeScreenshot("screenshots/" + dtf.format(now) + ".png");
            }
        });
    }

    public static void update(Camera camera) {
        Input.update();
        float speed;
        if (Input.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            speed = 256f * Engine.getDelta();
        } else {
            speed = 128 * Engine.getDelta();
        }
        if (Input.isKeyDown(GLFW_KEY_W)) {
            camera.move(0, 0, speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_A)) {
            camera.move(-speed, 0, 0);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_S)) {
            camera.move(0, 0, -speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_D)) {
            camera.move(speed, 0, 0);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_SPACE)) {
            camera.move(0, speed, 0);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            camera.move(0, -speed, 0);
            Renderer.resetAccFrames();
        }
        if (!Window.isCursorVisible()) {
            camera.rotate((float) (Input.getDeltaY() * -0.003f), 0, 0);
            camera.rotate(0, (float) (Input.getDeltaX() * -0.003f), 0);
            if (Input.getDeltaX() != 0 || Input.getDeltaY() != 0) Renderer.resetAccFrames();
        }
    }
}