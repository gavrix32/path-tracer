package net.gavrix32.app;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.Utils;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.io.Sync;
import net.gavrix32.engine.io.Window;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

public class GuiRenderer {
    private static boolean status = true;
    private static final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private static final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();

    private static final int[]
            samples = new int[] { 8 },
            bounces = new int[] { 4 },
            AASize = new int[] { 150 };

    private static final ImBoolean
            accumulation = new ImBoolean(true),
            randNoise = new ImBoolean(false),
            aces = new ImBoolean(true);

    private static final ImInt syncType = new ImInt();
    private static final String[] syncTypes = { "Off", "VSync", "Adaptive" };

    public static void init() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.getFonts().addFontFromMemoryTTF(Utils.loadBytes("fonts/arial.ttf"), 14);
        imGuiImplGlfw.init(Window.get(), true);
        imGuiImplGl3.init();
    }

    public static void update() {
        if (status) {
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();
            ImGui.begin("Render");
            ImGui.text((int) (1 / Engine.getDelta()) + " fps");
            ImGui.text("Frametime: " + Engine.getDelta() * 1000 + " ms");
            ImGui.combo("Sync", syncType, syncTypes);
            switch (syncType.get()) {
                case 0 -> Window.sync(Sync.OFF);
                case 1 -> Window.sync(Sync.VSYNC);
                case 2 -> Window.sync(Sync.ADAPTIVE);
            }
            if (ImGui.sliderInt("Samples", samples, 1, 128)) Renderer.resetAccFrames();
            if (ImGui.sliderInt("Bounces", bounces, 1, 64)) Renderer.resetAccFrames();
            if (ImGui.dragInt("UV Blur", AASize, 1, 0, 256000)) Renderer.resetAccFrames();
            if (ImGui.checkbox("ACES Film", aces)) Renderer.resetAccFrames();
            ImGui.checkbox("Accumulation", accumulation);
            ImGui.checkbox("Random Noise", randNoise);
            ImGui.end();
            ImGui.render();
            imGuiImplGl3.renderDrawData(ImGui.getDrawData());
            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                glfwMakeContextCurrent(backupWindowPtr);
            }
            Renderer.setSamples(samples[0]);
            Renderer.setBounces(bounces[0]);
            Renderer.setAASize(AASize[0]);
            Renderer.useAccumulation(accumulation.get());
            Renderer.useRandomNoise(randNoise.get());
            Renderer.useACESFilm(aces.get());
        }

    }

    public static void toggle() {
        status = !status;
    }
}