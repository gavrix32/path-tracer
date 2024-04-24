package net.gavrix32.engine.io;

import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.utils.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46C.*;

public class Window {
    private static long window;
    private static int width, height, defaultWidth, defaultHeight, monitorWidth, monitorHeight;
    private static boolean cursorVisible = true, fullscreen = false;

    public static void init(String title, int width, int height) {
        Window.width = width;
        Window.height = height;
        defaultWidth = width;
        defaultHeight = height;
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) Logger.error("Failed to initialize GLFW");
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        window = glfwCreateWindow(width, height, title, 0, 0);
        if (window == 0) Logger.error("Failed to create the GLFW window");
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        monitorWidth = vidMode.width();
        monitorHeight = vidMode.height();
        glfwSetWindowPos(
                window,
                (monitorWidth - width) / 2,
                (monitorHeight - height) / 2
        );
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        Logger.info("LWJGL " + Version.getVersion());
        Logger.info(glGetString(GL_RENDERER));
        Logger.info("OpenGL " + glGetString(GL_VERSION));
        Logger.info("GLSL " + glGetString(GL_SHADING_LANGUAGE_VERSION));
        Logger.info("GLX_EXT_swap_control_tear extension support: " + glfwExtensionSupported("GLX_EXT_swap_control_tear"));
        Logger.info("WGL_EXT_swap_control_tear extension support: " + glfwExtensionSupported("WGL_EXT_swap_control_tear"));
        glfwSwapInterval(0);
        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            Window.width = w;
            Window.height = h;
            glViewport(0, 0, w, h);
            Renderer.resetAccFrames();
            Renderer.resetAccTexture();
        });
    }

    public static void update() {
        glfwPollEvents();
        glfwSwapBuffers(window);
    }

    public static long get() {
        return window;
    }

    public static boolean isClosed() {
        return glfwWindowShouldClose(window);
    }

    public static void close() {
        glfwSetWindowShouldClose(window, true);
    }

    public static void setCursorVisible(boolean value) {
        Window.cursorVisible = value;
        glfwSetInputMode(window, GLFW_CURSOR, value ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
    }

    public static void toggleCursor() {
        cursorVisible = !cursorVisible;
        glfwSetInputMode(window, GLFW_CURSOR, cursorVisible ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
    }

    public static boolean isCursorVisible() {
        return cursorVisible;
    }

    public static void setFullscreen(boolean value) {
        Window.fullscreen = value;
        if (fullscreen) {
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0,
                    monitorWidth, monitorHeight, GLFW_DONT_CARE);
            Renderer.resetAccFrames();
        } else {
            glfwSetWindowMonitor(window, 0, (monitorWidth - defaultWidth) / 2, (monitorHeight - defaultHeight) / 2,
                    defaultWidth, defaultHeight, GLFW_DONT_CARE);
            Renderer.resetAccFrames();
        }
    }

    public static void toggleFullscreen() {
        fullscreen = !fullscreen;
        if (fullscreen) {
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0,
                    monitorWidth, monitorHeight, GLFW_DONT_CARE);
            Renderer.resetAccFrames();
        } else {
            glfwSetWindowMonitor(window, 0, (monitorWidth - defaultWidth) / 2, (monitorHeight - defaultHeight) / 2,
                    defaultWidth, defaultHeight, GLFW_DONT_CARE);
            Renderer.resetAccFrames();
        }
    }

    public static void sync(VSync type) {
        switch (type) {
            case ON -> glfwSwapInterval(1);
            case OFF -> glfwSwapInterval(0);
            case ADAPTIVE -> glfwSwapInterval(-1);
        }
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static float getAspect() {
        return (float) width / height;
    }
}