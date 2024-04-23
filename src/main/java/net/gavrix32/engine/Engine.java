package net.gavrix32.engine;

import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.utils.Timer;

import static org.lwjgl.glfw.GLFW.*;

public class Engine {
    private static float dt;
    private static int fpsLimit = Integer.MAX_VALUE;

    public static void run(IApp app) {
        app.init();
        double lastTime = glfwGetTime();
        while (!Window.isClosed()) {
            Timer frameTime = new Timer();
            frameTime.tick();
            while (glfwGetTime() < lastTime + (double) 1 / fpsLimit);
            lastTime += (double) 1 / fpsLimit;
            app.update();
            dt = frameTime.getDelta();
        }
        glfwTerminate();
        System.exit(0);
    }

    public static float getDeltaTime() {
        return dt;
    }

    public static int getFpsLimit() {
        return fpsLimit;
    }

    public static void setFpsLimit(int fpsLimit) {
        Engine.fpsLimit = fpsLimit;
    }
}