package net.gavrix32.engine.io;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;

public class Input {
    private static double currentX, currentY, lastX, lastY, deltaX, deltaY;

    static {
        glfwSetCursorPosCallback(Window.get(), (window, xpos, ypos) -> {
            currentX = xpos;
            currentY = ypos;
        });
    }

    public static boolean isKeyDown(int key) {
        return glfwGetKey(Window.get(), key) == GLFW_PRESS;
    }

    public static boolean isButtonDown(int button) {
        return glfwGetMouseButton(Window.get(), button) == GLFW_PRESS;
    }

    public static void update() {
        deltaX = currentX - lastX;
        deltaY = currentY - lastY;
        lastX = currentX;
        lastY = currentY;
    }

    public static double getCursorX() {
        return currentX;
    }

    public static double getCursorY() {
        return currentY;
    }

    public static double getDeltaX() {
        return deltaX;
    }

    public static double getDeltaY() {
        return deltaY;
    }
}