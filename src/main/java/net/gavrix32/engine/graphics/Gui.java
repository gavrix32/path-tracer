package net.gavrix32.engine.graphics;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.utils.Utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Gui {
    private static final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private static final String fontPath = "fonts/Inter-Regular.ttf";

    private static final int[] vsync = new int[1], fpsLimit = new int[1], samples = new int[1], bounces = new int[1], maxAccumulatedSamples = new int[1];
    public static final int[] iterations = new int[] {5};
    private static final float[] gamma = new float[1], exposure = new float[1], focusDistance = new float[1], aperture = new float[1], fov = new float[1];
    public static final float[] stepWidth = new float[] {2.8f}, c_phi = new float[] {0.01f}, n_phi = new float[] {0.01f}, p_phi = new float[] {1000.0f};
    private static final ImBoolean accumulation = new ImBoolean(), temporalReprojection = new ImBoolean(),
            temporalAntialiasing = new ImBoolean(), atrousFilter = new ImBoolean();
    private static final ImInt sceneId = new ImInt();

    private static final String[] lightingModeNames = {"Combined", "Direct", "Indirect"};
    private static final ImInt lightingMode = new ImInt(0);

    // Debug BVH
    public static final ImBoolean debugBVH = new ImBoolean(false);
    public static final int[] boundsTestThreshold = new int[] {400}, triangleTestThreshold = new int[] {50};

    static {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        io.getFonts().addFontFromMemoryTTF(Utils.loadBytes(fontPath), 15);
        ImGui.loadIniSettingsFromMemory(Utils.loadString("imgui.ini"));
        imGuiImplGlfw.init(Window.get(), true);
        imGuiImplGl3.init();

        int swapInterval = Config.getInt("swap_interval");
        switch (swapInterval) {
            case 0 -> vsync[0] = 0;
            case 1 -> vsync[0] = 1;
            case -1 -> vsync[0] = 2;
        }
        samples[0] = Renderer.getSamples();
        bounces[0] = Renderer.getBounces();
        gamma[0] = Renderer.getGamma();
        exposure[0] = Renderer.getExposure();
        focusDistance[0] = Renderer.getFocusDistance();
        aperture[0] = Renderer.getAperture();
        fov[0] = Renderer.getFOV();
        maxAccumulatedSamples[0] = Renderer.getMaxAccumulatedSamples();
        accumulation.set(Renderer.isAccumulation());
        temporalReprojection.set(Renderer.isTemporalReprojection());
        temporalAntialiasing.set(Renderer.isTemporalAntialiasing());
        atrousFilter.set(Renderer.isAtrousFilter());
    }

    public static void update(ArrayList<Scene> scenes) {
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();

        ImGui.beginDisabled(!Window.isCursorVisible());
        ImGui.begin("Path Tracer");
        ImGui.pushItemWidth(150);

        // Frametime / FPS
        ImGui.text("Frametime: " + Math.floor(Engine.getDeltaTime() * 1000) + " ms ("
                + (int) (1 / Engine.getDeltaTime()) + " FPS)");

        // Accumulated samples
        ImGui.text("Accumulated samples: " + Renderer.getAccumulatedSamples());

        ImGui.separator();
        // Renderer
        if (ImGui.treeNode("Renderer")) {
            String vsyncFormat = "";
            switch (vsync[0]) {
                case 0 -> vsyncFormat = "Off";
                case 1 -> vsyncFormat = "On";
                case 2 -> vsyncFormat = "Adaptive";
            }
            if (ImGui.sliderInt("VSync", vsync, 0, 2, vsyncFormat)) {
                switch (vsync[0]) {
                    case 0 -> Window.setSwapInterval(0);
                    case 1 -> Window.setSwapInterval(1);
                    case 2 -> Window.setSwapInterval(-1);
                }
            }
            if (ImGui.sliderInt("FPS Limit", fpsLimit, 1, 300))
                Engine.setFpsLimit(fpsLimit[0]);

            if (ImGui.sliderInt("Samples", samples, 1, 16))
                Renderer.setSamples(samples[0]);

            if (ImGui.sliderInt("Bounces", bounces, 1, 16))
                Renderer.setBounces(bounces[0]);

            String gammaFormat = gamma[0] == 0.0f ? "Off" : "%.1f";
            if (ImGui.sliderFloat("Gamma", gamma, 0.0f, 10.0f, gammaFormat))
                Renderer.setGamma(gamma[0]);

            String exposureFormat = exposure[0] == 0.0f ? "Off" : "%.1f";
            if (ImGui.sliderFloat("Exposure", exposure, 0.0f, 10.0f, exposureFormat))
                Renderer.setExposure(exposure[0]);

            String focusDistanceFormat = focusDistance[0] == 0.0 ? "Auto" : "%.1f";
            if (ImGui.sliderFloat("Focus Distance", focusDistance, 0.0f, 1000.0f, focusDistanceFormat))
                Renderer.setFocusDistance(focusDistance[0]);

            String apertureFormat = aperture[0] == 0.0 ? "Off" : "%.1f";
            if (ImGui.sliderFloat("Aperture", aperture, 0.0f, 50.0f, apertureFormat))
                Renderer.setAperture(aperture[0]);

            if (ImGui.sliderFloat("FOV", fov, 0.0f, 180.0f, "%.1f"))
                Renderer.setFOV(fov[0]);

            if (ImGui.checkbox("Accumulation", accumulation))
                Renderer.useAccumulation(accumulation.get());
            ImGui.beginDisabled(!accumulation.get());

            if (ImGui.sliderInt("Max Accumulated Samples", maxAccumulatedSamples, -1, 100))
                Renderer.setMaxAccumulatedSamples(maxAccumulatedSamples[0]);
            ImGui.endDisabled();

            // Temporal Reprojection
            if (ImGui.checkbox("Temporal Reprojection", temporalReprojection))
                Renderer.useTemporalReprojection(temporalReprojection.get());

            // TAA
            if (ImGui.checkbox("TAA", temporalAntialiasing))
                Renderer.useTemporalAntialiasing(temporalAntialiasing.get());

            // À-Trous Filter
            if (ImGui.checkbox("À-Trous Filter", atrousFilter))
                Renderer.useAtrousFilter(atrousFilter.get());

            ImGui.beginDisabled(!atrousFilter.get());
            ImGui.sliderInt("iterations", iterations, 1, 10);
            ImGui.sliderFloat("stepWidth", stepWidth, 0.0f, 10.0f);
            ImGui.sliderFloat("c_phi", c_phi, 0.0f, 10.0f);
            ImGui.sliderFloat("n_phi", n_phi, 0.0f, 10.0f);
            ImGui.sliderFloat("p_phi", p_phi, 0.0f, 10.0f);
            ImGui.endDisabled();

            // Lighting Mode
            ImGui.beginDisabled();
            if (ImGui.combo("Lighting Mode", lightingMode, lightingModeNames)) {}
            ImGui.endDisabled();

            // Debug BVH
            ImGui.checkbox("BVH Debug View", debugBVH);
            ImGui.beginDisabled(!debugBVH.get());
            ImGui.dragInt("Bounds Test Threshold", boundsTestThreshold, 1);
            ImGui.dragInt("Triangles Test Threshold", triangleTestThreshold, 1);
            ImGui.endDisabled();

            if (ImGui.button("Save"))
                Config.save();

            ImGui.sameLine();
            if (ImGui.button("Reset")) {
                Config.reset();
                int swapInterval = Config.getInt("swap_interval");
                switch (swapInterval) {
                    case 0 -> vsync[0] = 0;
                    case 1 -> vsync[0] = 1;
                    case -1 -> vsync[0] = 2;
                }
                fpsLimit[0] = Config.getInt("fps_limit"); Engine.setFpsLimit(fpsLimit[0]);
                samples[0] = Config.getInt("samples"); Renderer.setSamples(samples[0]);
                bounces[0] = Config.getInt("bounces"); Renderer.setBounces(bounces[0]);
                gamma[0] = Config.getFloat("gamma"); Renderer.setGamma(gamma[0]);
                exposure[0] = Config.getFloat("exposure"); Renderer.setExposure(exposure[0]);
                focusDistance[0] = Config.getFloat("focus_distance"); Renderer.setFocusDistance(focusDistance[0]);
                aperture[0] = Config.getFloat("aperture"); Renderer.setAperture(aperture[0]);
                fov[0] = Config.getFloat("fov"); Renderer.setFOV(fov[0]);
                maxAccumulatedSamples[0] = Config.getInt("max_accumulated_samples"); Renderer.setMaxAccumulatedSamples(maxAccumulatedSamples[0]);
                accumulation.set(Config.getBoolean("accumulation")); Renderer.useAccumulation(accumulation.get());
                temporalReprojection.set(Config.getBoolean("temporal_reprojection")); Renderer.useTemporalReprojection(temporalReprojection.get());
                temporalAntialiasing.set(Config.getBoolean("temporal_antialiasing")); Renderer.useTemporalAntialiasing(temporalAntialiasing.get());
                atrousFilter.set(Config.getBoolean("atrous_filter")); Renderer.useAtrousFilter(atrousFilter.get());
            }
            ImGui.treePop();
        }

        // Scene
        sceneId.set(scenes.indexOf(Renderer.getScene()));
        if (ImGui.treeNode("Scene")) {
            String[] names = new String[scenes.size()];
            for (int i = 0; i < scenes.size(); i++)
                names[i] = scenes.get(i).getName();
            if (ImGui.combo("Select", sceneId, names)) {
                Renderer.setScene(scenes.get(sceneId.get()));
                Renderer.resetAccFrames();
            }
            SceneEditor.showSceneObjectProps(scenes.get(sceneId.get()));
            ImGui.treePop();
        }
        if (ImGui.button("Screenshot")) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
            LocalDateTime now = LocalDateTime.now();
            File screenshotsDir = new File(System.getProperty("user.dir"), "screenshots");
            if (!screenshotsDir.exists())
                screenshotsDir.mkdir();
            Utils.takeScreenshot(System.getProperty("user.dir") + "/screenshots/" + dtf.format(now) + ".png");
        }

        ImGui.end();
        ImGui.endDisabled();

        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());
    }
}