package net.gavrix32.engine.editor;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.io.Window;

import java.util.ArrayList;

public class Scenes {
    private static ImInt sceneID = new ImInt();

    protected static void update(ArrayList<Scene> scenes, ArrayList<String> sceneNames) {
        ImGui.begin("Scene");
        String[] names = new String[sceneNames.size()];
        if (ImGui.combo("Select scene", sceneID, sceneNames.toArray(names))) {
            Renderer.resetAccFrames();
        }
        switch (sceneID.get()) {
            case 0: {
                Renderer.setScene(scenes.get(0));
                showSceneObjectProps(scenes.get(0));
                break;
            }
            case 1: {
                Renderer.setScene(scenes.get(1));
                showSceneObjectProps(scenes.get(1));
                break;
            }
            case 2: {
                Renderer.setScene(scenes.get(2));
                showSceneObjectProps(scenes.get(2));
                break;
            }
            case 3: {
                Renderer.setScene(scenes.get(3));
                showSceneObjectProps(scenes.get(3));
                break;
            }
            case 4: {
                Renderer.setScene(scenes.get(4));
                showSceneObjectProps(scenes.get(4));
                break;
            }
        }
        ImGui.end();
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
                float[] position = new float[] {
                        scene.getBox(i).getPos().x,
                        scene.getBox(i).getPos().y,
                        scene.getBox(i).getPos().z
                };
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
                if (ImGui.dragFloat3("Position", position)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setPos(position[0], position[1], position[2]);
                }
                ImGui.popID();
                ImGui.separator();
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
                float[] position = new float[] {
                        scene.getSphere(i).getPos().x,
                        scene.getSphere(i).getPos().y,
                        scene.getSphere(i).getPos().z
                };
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
                if (ImGui.dragFloat3("Position", position)) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setPos(position[0], position[1], position[2]);
                }
                ImGui.popID();
                ImGui.separator();
            }
            ImGui.treePop();
        }
    }
}