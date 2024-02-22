package net.gavrix32.app;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.graphics.Editor;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.utils.Logger;
import net.gavrix32.engine.utils.Timer;
import net.gavrix32.engine.utils.Utils;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.io.Sync;
import net.gavrix32.engine.io.Window;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL30C.GL_RENDERBUFFER;

public class GuiRenderer {
    private static boolean status = true;
    private static final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private static final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();

    private static final int[]
            samples = new int[] {1},
            bounces = new int[] {3},
            AASize = new int[] {128};

    private static final float[] gamma = new float[] {2.2f};

    private static final ImBoolean
            accumulation = new ImBoolean(true),
            reproj = new ImBoolean(true),
            randNoise = new ImBoolean(false),
            gammaCorrection = new ImBoolean(true),
            aces = new ImBoolean(false),
            showAlbedo = new ImBoolean(false),
            showNormals = new ImBoolean(false),
            showDepth = new ImBoolean(false);

    private static final ImInt
            syncType = new ImInt(),
            sceneID = new ImInt();

    private static final String[] syncTypes = {"Off", "VSync", "Adaptive"};
    private static final String[] sceneNames = {"Cornell Box", "RGB Room", "RGB Spheres", "Spheres", "Liminal"};
    private static Scene[] scenes;
    private static float guiTime;

    public static void init(Scene... scenes) {
        GuiRenderer.scenes = scenes;
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.getFonts().addFontFromMemoryTTF(Utils.loadBytes("fonts/Inter-Regular.ttf"), 15);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        imGuiImplGlfw.init(Window.get(), true);
        imGuiImplGl3.init();
        Editor.init();
    }

    public static void update() {
        Timer guiTimer = new Timer();
        guiTimer.tick();
        if (status) {
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();
            ImGui.dockSpaceOverViewport();

            ImGui.begin("Viewport");
            Editor.update();
            ImGui.end();

            ImGui.begin("Renderer");
            ImGui.text((int) (1 / Engine.getDelta()) + " fps");
            ImGui.text("Frame time: " + Engine.getDelta() * 1000 + " ms");
            ImGui.text("ImGui time: " + guiTime * 1000 + " ms");
            if (ImGui.combo("Sync", syncType, syncTypes)) {
                switch (syncType.get()) {
                    case 0 -> Window.sync(Sync.OFF);
                    case 1 -> Window.sync(Sync.VSYNC);
                    case 2 -> Window.sync(Sync.ADAPTIVE);
                }
            }
            if (ImGui.sliderInt("Samples", samples, 1, 32)) Renderer.resetAccFrames();
            if (ImGui.sliderInt("Bounces", bounces, 0, 8)) Renderer.resetAccFrames();
            if (ImGui.dragInt("UV Blur", AASize, 1, 0, 256000)) Renderer.resetAccFrames();
            ImGui.text("Accumulated frames: " + Renderer.getAccFrames());
            ImGui.checkbox("Accumulation", accumulation);
            ImGui.checkbox("Gamma Correction", gammaCorrection);
            if (gammaCorrection.get()) ImGui.sliderFloat("Gamma", gamma, 0, 5, "%.1f");
            ImGui.checkbox("ACES Tone Mapping", aces);
            ImGui.checkbox("Temporal Reprojection", reproj);
            ImGui.checkbox("Random Noise", randNoise);
            ImGui.checkbox("Show Albedo", showAlbedo);
            ImGui.checkbox("Show Normals", showNormals);
            ImGui.checkbox("Show Depth", showDepth);
            ImGui.textWrapped("Controls: WASD to move, Ctrl to speed up, Right Click to grab cursor, " +
                    "Escape to exit, F2 to take screenshot, F11 to enter full screen, F1 to off/on gui");
            ImGui.end();

            ImGui.begin("Scene");
            if (ImGui.combo("Select scene", sceneID, sceneNames)) {
                Renderer.resetAccFrames();
            }
            switch (sceneID.get()) {
                case 0: {
                    Renderer.setScene(scenes[0]);
                    showSceneObjectProps(scenes[0]);
                    break;
                }
                case 1: {
                    Renderer.setScene(scenes[1]);
                    showSceneObjectProps(scenes[1]);
                    break;
                }
                case 2: {
                    Renderer.setScene(scenes[2]);
                    showSceneObjectProps(scenes[2]);
                    break;
                }
                case 3: {
                    Renderer.setScene(scenes[3]);
                    showSceneObjectProps(scenes[3]);
                    break;
                }
                case 4: {
                    Renderer.setScene(scenes[4]);
                    showSceneObjectProps(scenes[4]);
                    break;
                }
            }
            ImGui.end();

            ImGui.begin("Logs");
            for (String msg : Logger.getLogList()) {
                if (msg.contains("[INFO]")) ImGui.text(msg);
                if (msg.contains("[WARNING]")) ImGui.textColored(1.0f, 1.0f, 0.0f, 1f, msg);
                if (msg.contains("[ERROR]")) ImGui.textColored(1.0f, 0.0f, 0.0f, 1f, msg);
            }
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
        guiTime = guiTimer.getDelta();
        Renderer.setSamples(samples[0]);
        Renderer.setBounces(bounces[0]);
        Renderer.setAASize(AASize[0]);
        Renderer.useAccumulation(accumulation.get());
        Renderer.useRandomNoise(randNoise.get());
        Renderer.useGammaCorrection(gammaCorrection.get(), gamma[0]);
        Renderer.useACESFilm(aces.get());
        Renderer.useReprojection(reproj.get());
        Renderer.showAlbedo(showAlbedo.get());
        Renderer.showNormals(showNormals.get());
        Renderer.showDepth(showDepth.get());
    }

    public static void toggle() {
        status = !status;
    }

    private static void showSceneObjectProps(Scene scene) {
        if (ImGui.treeNode("Boxes")) {
            for (int i = 0; i < scene.getBoxes().size(); i++) {
                float[] color = new float[] {
                        scene.getBox(i).getColor().x,
                        scene.getBox(i).getColor().y,
                        scene.getBox(i).getColor().z
                };
                float[] emission = new float[] {scene.getBox(i).getMaterial().getEmission()};
                float[] roughness = new float[] {scene.getBox(i).getMaterial().getRoughness()};
                ImBoolean isMetal = new ImBoolean(scene.getBox(i).getMaterial().isMetal());
                ImGui.pushID(i);
                if (ImGui.colorEdit3("Box " + i + " color", color)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setColor(color[0], color[1], color[2]);
                }
                if (ImGui.dragFloat("Emission", emission, 0.01f, 0, 9999, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setMaterial(emission[0], roughness[0], isMetal.get());
                }
                if (ImGui.sliderFloat("Roughness", roughness, 0, 1, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setMaterial(emission[0], roughness[0], isMetal.get());
                }
                if (ImGui.checkbox("isMetal", isMetal)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setMaterial(emission[0], roughness[0], isMetal.get());
                }
                ImGui.popID();
            }
            ImGui.treePop();
        }
        if (ImGui.treeNode("Spheres")) {
            for (int i = 0; i < scene.getSpheres().size(); i++) {
                float[] color = new float[] {
                        scene.getSphere(i).getColor().x,
                        scene.getSphere(i).getColor().y,
                        scene.getSphere(i).getColor().z
                };
                float[] emission = new float[] {scene.getSphere(i).getMaterial().getEmission()};
                float[] roughness = new float[] {scene.getSphere(i).getMaterial().getRoughness()};
                ImBoolean isMetal = new ImBoolean(scene.getSphere(i).getMaterial().isMetal());
                ImGui.pushID(i);
                if (ImGui.colorEdit3("Sphere " + i + " color", color)) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setColor(color[0], color[1], color[2]);
                }
                if (ImGui.dragFloat("Emission", emission, 0.01f, 0, 9999, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setMaterial(emission[0], roughness[0], isMetal.get());
                }
                if (ImGui.sliderFloat("Roughness", roughness, 0, 1, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setMaterial(emission[0], roughness[0], isMetal.get());
                }
                if (ImGui.checkbox("isMetal", isMetal)) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setMaterial(emission[0], roughness[0], isMetal.get());
                }
                ImGui.popID();
            }
            ImGui.treePop();
        }
    }
}