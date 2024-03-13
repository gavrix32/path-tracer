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
            fpsLimit = new int[] {Engine.getFpsLimit()};
    private static final float[]
            gamma = new float[] {Renderer.getGamma()},
            exposure = new float[] {Renderer.getExposure()},
            focusDistance = new float[] {Renderer.getFocusDistance()},
            defocusBlur = new float[] {Renderer.getDefocusBlur()};
    private static final ImBoolean
            accumulation = new ImBoolean(Renderer.isAccumulation()),
            frameMixing = new ImBoolean(Renderer.isFrameMixing()),
            randNoise = new ImBoolean(Renderer.isRandNoise()),
            gammaCorrection = new ImBoolean(Renderer.isGammaCorrection()),
            tonemapping = new ImBoolean(Renderer.isTonemapping()),
            taa = new ImBoolean(Renderer.isTaa()),
            dof = new ImBoolean(Renderer.isDof()),
            autofocus = new ImBoolean(Renderer.isAutofocus()),
            showAlbedo = new ImBoolean(Renderer.isShowAlbedo()),
            showNormals = new ImBoolean(Renderer.isShowNormals()),
            showDepth = new ImBoolean(Renderer.isShowDepth());
    private static final ImInt syncType = new ImInt();
    private static final String[] syncTypes = {"off", "on", "adaptive"};

    public static void update() {
        ImGui.begin("Render", ImGuiWindowFlags.NoMove);
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
        if (ImGui.sliderInt("spp", samples, 1, 32)) Renderer.setSamples(samples[0]);
        if (ImGui.sliderInt("bounces", bounces, 0, 8)) Renderer.setBounces(bounces[0]);
        if (ImGui.checkbox("taa", taa)) Renderer.useTaa(taa.get());
        if (ImGui.checkbox("dof", dof)) Renderer.setDof(dof.get());
        ImGui.beginDisabled(!dof.get());
        if (ImGui.checkbox("autofocus", autofocus)) Renderer.setAutofocus(autofocus.get());
        ImGui.endDisabled();
        ImGui.beginDisabled(autofocus.get());
        if (ImGui.dragFloat("focus distance", focusDistance, 1, 0, Float.MAX_VALUE, "%.1f")) Renderer.setFocusDistance(focusDistance[0]);
        ImGui.endDisabled();
        ImGui.beginDisabled(!dof.get());
        if (ImGui.sliderFloat("defocus blur", defocusBlur, 0, 10, "%.1f")) Renderer.setDefocusBlur(defocusBlur[0]);
        ImGui.endDisabled();
        ImGui.text("accumulated frames: " + Renderer.getAccFrames());
        if (ImGui.checkbox("accumulation", accumulation)) Renderer.setAccumulation(accumulation.get());
        if (ImGui.checkbox("gamma Correction", gammaCorrection) || gammaCorrection.get()) Renderer.useGammaCorrection(gammaCorrection.get(), gamma[0]);
        ImGui.beginDisabled(!gammaCorrection.get());
        ImGui.sliderFloat("gamma", gamma, 0, 10, "%.1f");
        ImGui.endDisabled();
        if (ImGui.checkbox("tone Mapping", tonemapping) || tonemapping.get()) Renderer.setToneMapping(tonemapping.get(), exposure[0]);
        ImGui.beginDisabled(!tonemapping.get());
        ImGui.sliderFloat("exposure", exposure, 0, 5, "%.1f");
        ImGui.endDisabled();
        if (ImGui.checkbox("temporal mixing", frameMixing)) Renderer.setFrameMixing(frameMixing.get());
        if (ImGui.checkbox("random noise", randNoise)) Renderer.setRandomNoise(randNoise.get());
        if (ImGui.checkbox("show albedo", showAlbedo)) Renderer.showAlbedo(showAlbedo.get());
        if (ImGui.checkbox("show normals", showNormals)) Renderer.showNormals(showNormals.get());
        if (ImGui.checkbox("show depth", showDepth)) Renderer.showDepth(showDepth.get());
        ImGui.textWrapped("controls: wasd to move, ctrl to speed up, right click to grab cursor, " +
                "escape to exit, f2 to take screenshot, f11 to enter full screen, f1 to off/on gui");
        ImGui.end();
    }
}