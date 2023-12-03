package net.gavrix32.app;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import net.gavrix32.engine.graphics.*;
import net.gavrix32.engine.graphics.Camera;
import net.gavrix32.engine.input.Input;
import net.gavrix32.engine.graphics.Window;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Sphere;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Main {
    private static Camera cam;
    private static ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private static ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private static ImGuiIO io;

    public static void main(String[] args) {
        Box[] boxes = {
                new Box(new Vector3f(50, 100, 100), new Vector3f(1, 1, 1), new Material(0, 0), new Vector3f(100, 100, 0)),
                new Box(new Vector3f(50, 200, 0), new Vector3f(1, 1, 1), new Material(0, 0), new Vector3f(100, 0, 100)),
                new Box(new Vector3f(-50, 100, 0), new Vector3f(1, 0, 1), new Material(1, 0), new Vector3f(0, 100, 100)),
                new Box(new Vector3f(150, 100, 0), new Vector3f(0, 1, 1), new Material(1, 0), new Vector3f(0, 100, 100)),
                new Box(new Vector3f(50, 200, 0), new Vector3f(1, 1, 1), new Material(1, 0), new Vector3f(50, 1, 50))
        };
        Sphere[] spheres = {
                new Sphere(new Vector3f(0, 20, 0), new Vector3f(1, 0.5f, 0), new Material(1, 0), 20),
                new Sphere(new Vector3f(50, 20, 0), new Vector3f(1, 1, 1), new Material(0, 1), 20),
                new Sphere(new Vector3f(100, 20, 0), new Vector3f(0.5f, 1, 0), new Material(1, 0), 20)
        };
        Window.init("Ray Tracing", 1280, 720);
        glfwSetKeyCallback(Window.get(), (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) System.exit(0);
        });
        glfwSetMouseButtonCallback(Window.get(), (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE) Window.toggleCursor();
        });
        cam = new Camera();
        cam.setPos(50, 50, -120);
        Renderer.init();

        ImGui.createContext();
        io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        imGuiImplGlfw.init(Window.get(), true);
        imGuiImplGl3.init();

        int[] samples = new int[] { 32 };
        int[] bounces = new int[] { 8 };
        int[] AASize = new int[] { 150 };
        ImBoolean accumulate = new ImBoolean(true);
        ImBoolean randNoise = new ImBoolean(false);

        while (!Window.isClosed()) {
            Input.update();
            input();
            Window.update();
            Renderer.useDenoiser(accumulate.get());
            Renderer.setSamples(samples[0]);
            Renderer.setBounces(bounces[0]);
            Renderer.useRandomNoise(randNoise.get());
            Renderer.setAASize(AASize[0]);
            Renderer.render(cam, boxes, spheres);
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();

            ImGui.begin("Render");
            ImGui.text((int) (1 / Renderer.getFrametime()) + " fps");
            ImGui.text("Frametime: " + Renderer.getFrametime() * 1000 + " ms");
            if (ImGui.sliderInt("Samples", samples, 1, 128)) Renderer.resetAccFrames();
            if (ImGui.sliderInt("Bounces", bounces, 1, 64)) Renderer.resetAccFrames();
            ImGui.checkbox("Denoiser", accumulate);
            ImGui.checkbox("Random Noise", randNoise);
            if (ImGui.dragInt("AA Blur", AASize, 1, 0, 256)) Renderer.resetAccFrames();
            ImGui.end();

            ImGui.render();
            imGuiImplGl3.renderDrawData(ImGui.getDrawData());
            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                glfwMakeContextCurrent(backupWindowPtr);
            }
        }
    }

    private static void input() {
        float speed;
        if (Input.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            speed = 1.6f;
        } else {
            speed = 0.8f;
        }
        if (Input.isKeyDown(GLFW_KEY_W)) {
            cam.move(0, 0, speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_A)) {
            cam.move(-speed, 0, 0);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_S)) {
            cam.move(0, 0, -speed);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_D)) {
            cam.move(speed, 0, 0);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_SPACE)) {
            cam.move(0, speed, 0);
            Renderer.resetAccFrames();
        }
        if (Input.isKeyDown(GLFW_KEY_LEFT_SHIFT)) {
            cam.move(0, -speed, 0);
            Renderer.resetAccFrames();
        }
        if (!Window.isCursorVisible()) {
            cam.rotate((float) (Input.getDeltaY() * -0.003f), 0, 0);
            cam.rotate(0, (float) (Input.getDeltaX() * -0.003f), 0);
            if (Input.getDeltaX() != 0 || Input.getDeltaY() != 0) Renderer.resetAccFrames();
        }
    }
}