package net.gavrix32.engine;

import net.gavrix32.engine.graphics.Config;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.utils.Timer;
import org.lwjgl.Version;
import org.tinylog.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class Engine {
    private static float dt;
    private static int fpsLimit;

    static {
        Logger.info("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"));
        Logger.info("Java " + System.getProperty("java.version") + " " + System.getProperty("java.vendor"));
        Logger.info("LWJGL " + Version.getVersion());
        fpsLimit = Config.getInt("fps_limit");
    }

    public static void run(IApp app) {
        app.init();
        double lastTime = glfwGetTime();
        while (!Window.isClosed()) {
            Timer timer = new Timer();
            timer.tick();
            while (glfwGetTime() < lastTime + (double) 1 / fpsLimit);
            lastTime += (double) 1 / fpsLimit;
            app.update();
            dt = timer.getDelta();
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