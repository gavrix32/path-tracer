package net.gavrix32.engine.editor;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.Engine;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.io.Sync;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.utils.Logger;

public class Render {
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

    public static void update() {
        ImGui.begin("Renderer");
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
        if (ImGui.sliderInt("Samples", samples, 1, 32)) {
            Renderer.resetAccFrames();
            Renderer.setSamples(samples[0]);
        }
        if (ImGui.sliderInt("Bounces", bounces, 0, 8)) {
            Renderer.resetAccFrames();
            Renderer.setBounces(bounces[0]);
        }
        if (ImGui.dragInt("UV Blur", AASize, 1, 0, 256000)) {
            Renderer.resetAccFrames();
            Renderer.setAASize(AASize[0]);
        }
        ImGui.text("Accumulated frames: " + Renderer.getAccFrames());
        if (ImGui.checkbox("Accumulation", accumulation)) Renderer.useAccumulation(accumulation.get());
        if (ImGui.checkbox("Gamma Correction", gammaCorrection) || gammaCorrection.get()) {
            Renderer.useGammaCorrection(gammaCorrection.get(), gamma[0]);
            ImGui.sliderFloat("Gamma", gamma, 0, 5, "%.1f");
        }
        if (ImGui.checkbox("ACES Tone Mapping", aces)) Renderer.useACESFilm(aces.get());
        if (ImGui.checkbox("Temporal Reprojection", reproj)) Renderer.useReprojection(reproj.get());
        if (ImGui.checkbox("Random Noise", randNoise)) Renderer.useRandomNoise(randNoise.get());;
        if (ImGui.checkbox("Show Albedo", showAlbedo)) Renderer.showAlbedo(showAlbedo.get());
        if (ImGui.checkbox("Show Normals", showNormals)) Renderer.showNormals(showNormals.get());
        if (ImGui.checkbox("Show Depth", showDepth)) Renderer.showDepth(showDepth.get());
        ImGui.textWrapped("Controls: WASD to move, Ctrl to speed up, Right Click to grab cursor, " +
                "Escape to exit, F2 to take screenshot, F11 to enter full screen, F1 to off/on gui");
        ImGui.end();
    }
}