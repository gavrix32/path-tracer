package net.gavrix32.engine.graphics;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.glViewport;

public class Window {
    private static long window;
    private static int width, height;
    private static boolean cursorVisible = true;

    public static void init(String title, int width, int height) {
        Window.width = width;
        Window.height = height;
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        window = glfwCreateWindow(width, height, title, 0, 0);
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                window,
                (vidMode.width() - width) / 2,
                (vidMode.height() - height) / 2
        );
        glfwSwapInterval(1);
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            Window.width = w;
            Window.height = h;
            glViewport(0, 0, w, h);
            Renderer.resetAccFrames();
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

    public static void toggleCursor() {
        cursorVisible = !cursorVisible;
        glfwSetInputMode(window, GLFW_CURSOR, cursorVisible ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
    }

    public static boolean isCursorVisible() {
        return cursorVisible;
    }

    public static void setTitle(String title) {
        glfwSetWindowTitle(window, title);
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }
}