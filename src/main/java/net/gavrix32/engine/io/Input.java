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

    public static boolean isKeyDown(Key key) {
        return glfwGetKey(Window.get(), key.getId()) == GLFW_PRESS;
    }

    public static boolean isButtonDown(Button button) {
        return glfwGetMouseButton(Window.get(), button.getId()) == GLFW_PRESS;
    }

    public static void update() {
        deltaX = currentX - lastX;
        deltaY = currentY - lastY;
        lastX = currentX;
        lastY = currentY;
    }

    public static float getCursorX() {
        return (float) currentX;
    }

    public static float getCursorY() {
        return (float) currentY;
    }

    public static float getDeltaX() {
        return (float) deltaX;
    }

    public static float getDeltaY() {
        return (float) deltaY;
    }
}