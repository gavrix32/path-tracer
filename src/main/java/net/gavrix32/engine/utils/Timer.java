package net.gavrix32.engine.utils;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {
    private static float time;

    public static void tick() {
        time = (float) glfwGetTime();
    }

    public static float getDelta() {
        return (float) (glfwGetTime() - time);
    }
}
