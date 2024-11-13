package net.gavrix32.engine;

import net.gavrix32.engine.graphics.Config;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.utils.Timer;
import org.lwjgl.Version;
import org.tinylog.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class Engine {
    private static int fpsLimit;
    private static double delta = 0.0;
    private static int fps = 0;

    static {
        Logger.info("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
        Logger.info("Java " + System.getProperty("java.version") + " " + System.getProperty("java.vendor"));
        Logger.info("LWJGL " + Version.getVersion());
        fpsLimit = Config.getInt("fps_limit");
    }

    public static void run(IApp app) {
        app.init();
        double lastTime = glfwGetTime();
        double lastFpsUpdateTime = glfwGetTime();
        while (!Window.isClosed()) {
            double currentTime = glfwGetTime();
            delta = currentTime - lastTime;
            lastTime = currentTime;

            double period = 0.1;
            if (currentTime - lastFpsUpdateTime >= period) {
                fps = (int) (1.0 / delta);
                lastFpsUpdateTime = currentTime;
            }
            while (glfwGetTime() < lastTime + (double) 1 / fpsLimit);
            app.update();
        }
        glfwTerminate();
        System.exit(0);
    }

    public static float getDeltaTime() {
        return (float) delta;
    }

    // FPS updates once per second
    public static int getFps() {
        return fps;
    }

    public static int getFpsLimit() {
        return fpsLimit;
    }

    public static void setFpsLimit(int fpsLimit) {
        Engine.fpsLimit = fpsLimit;
    }
}