package net.gavrix32.engine.gui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.io.VSync;
import net.gavrix32.engine.io.Window;

public class Render {
    private static final int[]
            samples = new int[] {Renderer.getSamples()},
            bounces = new int[] {Renderer.getBounces()},
            fov = new int[] {Renderer.getFOV()},
            fpsLimit = new int[] {Engine.getFpsLimit()};

    private static final float[] gamma = new float[] {Renderer.getGamma()}, exposure = new float[] {Renderer.getExposure()};

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

    private static final String[] syncTypes = {"off", "on", "adaptive"};

    public static void update() {
        ImGui.begin("render", ImGuiWindowFlags.NoMove);
        ImGui.text((int) (1 / Engine.getDelta()) + " fps");
        ImGui.text("frame time: " + Engine.getDelta() * 1000 + " ms");
        if (ImGui.sliderInt("fps limit", fpsLimit, 1, 255, fpsLimit[0] < 255 ? String.valueOf(fpsLimit[0]) : "unlimited")) Engine.setFpsLimit(fpsLimit[0]);
        if (fpsLimit[0] == 255) Engine.setFpsLimit(Integer.MAX_VALUE);
        if (ImGui.combo("sync", syncType, syncTypes)) {
            switch (syncType.get()) {
                case 0 -> Window.sync(VSync.OFF);
                case 1 -> Window.sync(VSync.ON);
                case 2 -> Window.sync(VSync.ADAPTIVE);
            }
        }
        if (ImGui.sliderInt("spp", samples, 1, 32)) {
            Renderer.resetAccFrames();
            Renderer.setSamples(samples[0]);
        }
        if (ImGui.sliderInt("Bounces", bounces, 0, 8)) {
            Renderer.resetAccFrames();
            Renderer.setBounces(bounces[0]);
        }
        if (ImGui.dragInt("fov", fov, 1, 0, 180)) {
            Renderer.resetAccFrames();
            Renderer.setFOV(fov[0]);
        }
        if (ImGui.checkbox("taa", taa)) {
            Renderer.resetAccFrames();
            Renderer.useTAA(taa.get());
        }
        ImGui.text("accumulated frames: " + Renderer.getAccFrames());
        if (ImGui.checkbox("accumulation", accumulation)) Renderer.useAccumulation(accumulation.get());
        if (ImGui.checkbox("gamma Correction", gammaCorrection) || gammaCorrection.get()) {
            Renderer.useGammaCorrection(gammaCorrection.get(), gamma[0]);
            ImGui.sliderFloat("gamma", gamma, 0, 10, "%.1f");
        }
        if (ImGui.checkbox("tone Mapping", tonemapping) || tonemapping.get()) {
            Renderer.useToneMapping(tonemapping.get(), exposure[0]);
            ImGui.sliderFloat("exposure", exposure, 0, 5, "%.1f");
        }
        if (ImGui.checkbox("temporal mixing", reproj)) Renderer.useFrameMixing(reproj.get());
        if (ImGui.checkbox("random noise", randNoise)) Renderer.useRandomNoise(randNoise.get());
        if (ImGui.checkbox("show albedo", showAlbedo)) Renderer.showAlbedo(showAlbedo.get());
        if (ImGui.checkbox("show normals", showNormals)) Renderer.showNormals(showNormals.get());
        if (ImGui.checkbox("show depth", showDepth)) Renderer.showDepth(showDepth.get());
        ImGui.textWrapped("controls: wasd to move, ctrl to speed up, right click to grab cursor, " +
                "escape to exit, f2 to take screenshot, f11 to enter full screen, f1 to off/on gui");
        ImGui.end();
    }
}