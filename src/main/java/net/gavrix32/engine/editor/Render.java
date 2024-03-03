package net.gavrix32.engine.editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.io.Sync;
import net.gavrix32.engine.io.Window;

public class Render {
    private static final int[]
            samples = new int[] {1},
            bounces = new int[] {3};

    private static final float[] gamma = new float[] {1.8f}, exposure = new float[] {1};

    private static final ImBoolean
            accumulation = new ImBoolean(true),
            reproj = new ImBoolean(true),
            randNoise = new ImBoolean(false),
            gammaCorrection = new ImBoolean(true),
            tonemapping = new ImBoolean(true),
            taa = new ImBoolean(true),
            showAlbedo = new ImBoolean(false),
            showNormals = new ImBoolean(false),
            showDepth = new ImBoolean(false);

    private static final ImInt
            syncType = new ImInt();

    private static final String[] syncTypes = {"Off", "VSync", "Adaptive"};

    public static void update() {
        ImGui.begin("Render", ImGuiWindowFlags.NoMove);
        ImGui.text((int) (1 / Engine.getDelta()) + " fps");
        ImGui.text("Frame time: " + Engine.getDelta() * 1000 + " ms");
        //ImGui.text("ImGui time: " + guiTime * 1000 + " ms");
        if (ImGui.combo("Sync", syncType, syncTypes)) {
            switch (syncType.get()) {
                case 0 -> Window.sync(Sync.OFF);
                case 1 -> Window.sync(Sync.VSYNC);
                case 2 -> Window.sync(Sync.ADAPTIVE);
            }
        }
        if (ImGui.sliderInt("SPP", samples, 1, 32)) {
            Renderer.resetAccFrames();
            Renderer.setSamples(samples[0]);
        }
        if (ImGui.sliderInt("Bounces", bounces, 0, 8)) {
            Renderer.resetAccFrames();
            Renderer.setBounces(bounces[0]);
        }
        if (ImGui.checkbox("TAA", taa)) {
            Renderer.resetAccFrames();
            Renderer.useTAA(taa.get());
        }
        ImGui.text("Accumulated frames: " + Renderer.getAccFrames());
        if (ImGui.checkbox("Accumulation", accumulation)) Renderer.useAccumulation(accumulation.get());
        if (ImGui.checkbox("Gamma Correction", gammaCorrection) || gammaCorrection.get()) {
            Renderer.useGammaCorrection(gammaCorrection.get(), gamma[0]);
            ImGui.sliderFloat("Gamma", gamma, 0, 10, "%.1f");
        }
        if (ImGui.checkbox("Tone Mapping", tonemapping) || tonemapping.get()) {
            Renderer.useToneMapping(tonemapping.get(), exposure[0]);
            ImGui.sliderFloat("Exposure", exposure, 0, 5, "%.1f");
        }
        if (ImGui.checkbox("Temporal mixing", reproj)) Renderer.useFrameMixing(reproj.get());
        if (ImGui.checkbox("Random Noise", randNoise)) Renderer.useRandomNoise(randNoise.get());;
        if (ImGui.checkbox("Show Albedo", showAlbedo)) Renderer.showAlbedo(showAlbedo.get());
        if (ImGui.checkbox("Show Normals", showNormals)) Renderer.showNormals(showNormals.get());
        if (ImGui.checkbox("Show Depth", showDepth)) Renderer.showDepth(showDepth.get());
        ImGui.textWrapped("Controls: WASD to move, Ctrl to speed up, Right Click to grab cursor, " +
                "Escape to exit, F2 to take screenshot, F11 to enter full screen, F1 to off/on gui");
        ImGui.end();
    }
}