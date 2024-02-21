package net.gavrix32.engine.utils;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {
    private float time;

    public static float getTime() {
        return (float) glfwGetTime();
    }

    public void tick() {
        time = (float) glfwGetTime();
    }

    public float getDelta() {
        return (float) (glfwGetTime() - time);
    }
}
