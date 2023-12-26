package net.gavrix32.engine;

import net.gavrix32.engine.io.Window;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Engine {
    private static float dt;

    public static void run(IApplication app) {
        app.init();
        while (!Window.isClosed()) {
            float last = (float) glfwGetTime();
            app.update();
            dt = (float) (glfwGetTime() - last);
        }
    }

    public static float getDelta() {
        return dt;
    }
}