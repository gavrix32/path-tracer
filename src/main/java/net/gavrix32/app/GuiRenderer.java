package net.gavrix32.app;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.utils.Timer;
import net.gavrix32.engine.utils.Utils;
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
            samples = new int[] { 1 },
            bounces = new int[] { 3 },
            AASize = new int[] { 128 };

    private static final ImBoolean
            accumulation = new ImBoolean(true),
            reproj = new ImBoolean(true),
            randNoise = new ImBoolean(false),
            aces = new ImBoolean(true),
            showAlbedo = new ImBoolean(false),
            showNormals = new ImBoolean(false),
            showDepth = new ImBoolean(false);

    private static final ImInt syncType = new ImInt();
    private static final String[] syncTypes = { "Off", "VSync", "Adaptive" };

    private static float guiTime;

    public static void init() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.getFonts().addFontFromMemoryTTF(Utils.loadBytes("fonts/arial.ttf"), 14);
        imGuiImplGlfw.init(Window.get(), true);
        imGuiImplGl3.init();
    }

    public static void update() {
        Timer.tick();
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();
        if (status) {
            ImGui.begin("Render");
            ImGui.text((int) (1 / Engine.getDelta()) + " fps");
            ImGui.text("Frametime: " + Engine.getDelta() * 1000 + " ms");
            ImGui.text("ImGui time: " + guiTime * 1000 + " ms");
            ImGui.combo("Sync", syncType, syncTypes);
            switch (syncType.get()) {
                case 0 -> Window.sync(Sync.OFF);
                case 1 -> Window.sync(Sync.VSYNC);
                case 2 -> Window.sync(Sync.ADAPTIVE);
            }
            if (ImGui.sliderInt("Samples", samples, 1, 32)) Renderer.resetAccFrames();
            if (ImGui.sliderInt("Bounces", bounces, 1, 8)) Renderer.resetAccFrames();
            if (ImGui.dragInt("UV Blur", AASize, 1, 0, 256000)) Renderer.resetAccFrames();
            ImGui.text("Accumulated frames: " + Renderer.getAccFrames());
            ImGui.checkbox("Accumulation", accumulation);
            if (ImGui.checkbox("ACES Film", aces)) Renderer.resetAccFrames();
            ImGui.checkbox("Temporal Reprojection", reproj);
            ImGui.checkbox("Random Noise", randNoise);
            ImGui.checkbox("Show Albedo", showAlbedo);
            ImGui.checkbox("Show Normals", showNormals);
            ImGui.checkbox("Show Depth", showDepth);
            ImGui.textWrapped("Controls: WASD to move, Ctrl to speed up, Right Click to grab cursor, " +
                    "Escape to exit, F2 to take screenshot, F11 to enter full screen");
            ImGui.end();
        }
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
        guiTime = Timer.getDelta();
        Renderer.setSamples(samples[0]);
        Renderer.setBounces(bounces[0]);
        Renderer.setAASize(AASize[0]);
        Renderer.useAccumulation(accumulation.get());
        Renderer.useRandomNoise(randNoise.get());
        Renderer.useACESFilm(aces.get());
        Renderer.useReprojection(reproj.get());
        Renderer.showAlbedo(showAlbedo.get());
        Renderer.showNormals(showNormals.get());
        Renderer.showDepth(showDepth.get());
    }

    public static void toggle() {
        status = !status;
    }
}