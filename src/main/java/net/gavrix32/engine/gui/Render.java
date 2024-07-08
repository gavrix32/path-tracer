package net.gavrix32.engine.gui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.graphics.Renderer;

// Deprecated
public class Render {
    private static final int[]
            samples = new int[] {Renderer.getSamples()},
            bounces = new int[] {Renderer.getBounces()},
            fpsLimit = new int[] {Engine.getFpsLimit()};
    private static final float[]
            gamma = new float[] {Renderer.getGamma()},
            exposure = new float[] {Renderer.getExposure()},
            focusDistance = new float[] {Renderer.getFocusDistance()},
            defocusBlur = new float[] {Renderer.getAperture()};
    private static final ImBoolean
            accumulation = new ImBoolean(Renderer.isAccumulation());
    private static final ImInt syncType = new ImInt();
    private static final String[] syncTypes = {"off", "on", "adaptive"};

    public static void update() {
        ImGui.begin("Render", ImGuiWindowFlags.NoMove);
        ImGui.text((int) (1 / Engine.getDeltaTime()) + " fps");
        ImGui.text("frame time: " + Engine.getDeltaTime() * 1000 + " ms");
        if (ImGui.sliderInt("fps limit", fpsLimit, 1, 255, fpsLimit[0] < 255 ? String.valueOf(fpsLimit[0]) : "unlimited")) Engine.setFpsLimit(fpsLimit[0]);
        if (fpsLimit[0] == 255) Engine.setFpsLimit(Integer.MAX_VALUE);
        if (ImGui.combo("sync", syncType, syncTypes)) {
            switch (syncType.get()) {
            }
        }
        if (ImGui.sliderInt("spp", samples, 1, 32)) Renderer.setSamples(samples[0]);
        if (ImGui.sliderInt("bounces", bounces, 0, 16)) Renderer.setBounces(bounces[0]);
        ImGui.endDisabled();
        if (ImGui.dragFloat("focus distance", focusDistance, 1, 0, Float.MAX_VALUE, "%.1f")) Renderer.setFocusDistance(focusDistance[0]);
        ImGui.endDisabled();
        if (ImGui.sliderFloat("defocus blur", defocusBlur, 0, 10, "%.1f")) Renderer.setAperture(defocusBlur[0]);
        ImGui.endDisabled();
        ImGui.text("accumulated frames: " + Renderer.getFrames());
        if (ImGui.checkbox("accumulation", accumulation)) Renderer.useAccumulation(accumulation.get());
        ImGui.sameLine();
        ImGui.pushID("slider_gamma");
        ImGui.sliderFloat("", gamma, 0, 10, "%.1f");
        ImGui.popID();
        ImGui.endDisabled();
        ImGui.sameLine();
        ImGui.pushID("slider_exposure");
        ImGui.sliderFloat("", exposure, 0, 5, "%.1f");
        ImGui.popID();
        ImGui.endDisabled();
        ImGui.textWrapped("controls: wasd to move, ctrl to speed up, right click to grab cursor, " +
                "escape to exit, f2 to take screenshot, f11 to enter full screen, f1 to off/on gui");
        ImGui.end();
    }
}