package net.gavrix32.engine.utils;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {
    private float time;

    public Timer tick() {
        time = (float) glfwGetTime();
        return this;
    }

    public float getDelta() {
        return (float) (glfwGetTime() - time);
    }
}
