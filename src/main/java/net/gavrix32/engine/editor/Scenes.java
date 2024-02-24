package net.gavrix32.engine.editor;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.graphics.Material;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Sphere;
import org.joml.Vector3f;

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
        if (ImGui.treeNode("Sky")) {
            float[] color = new float[] {
                    scene.getSky().getColor().x,
                    scene.getSky().getColor().y,
                    scene.getSky().getColor().z
            };
            float[] emission = new float[] {scene.getSky().getMaterial().getEmission()};
            float[] roughness = new float[] {scene.getSky().getMaterial().getRoughness()};
            ImBoolean isMetal = new ImBoolean(scene.getSky().getMaterial().isMetal());

            if (ImGui.colorEdit3("Color", color)) {
                Renderer.resetAccFrames();
                scene.getSky().setColor(color[0], color[1], color[2]);
            }
            if (ImGui.dragFloat("Emission", emission, 0.01f, 0, 9999, "%.2f")) {
                Renderer.resetAccFrames();
                scene.getSky().setMaterial(emission[0], roughness[0], isMetal.get());
            }
            if (ImGui.sliderFloat("Roughness", roughness, 0, 1, "%.2f")) {
                Renderer.resetAccFrames();
                scene.getSky().setMaterial(emission[0], roughness[0], isMetal.get());
            }
            if (ImGui.checkbox("isMetal", isMetal)) {
                Renderer.resetAccFrames();
                scene.getSky().setMaterial(emission[0], roughness[0], isMetal.get());
            }
            ImGui.treePop();
        }
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
                float[] scale = new float[] {
                        scene.getBox(i).getScale().x,
                        scene.getBox(i).getScale().y,
                        scene.getBox(i).getScale().z
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
                if (ImGui.dragFloat3("Scale", scale, 1, 0, Float.MAX_VALUE)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setScale(scale[0], scale[1], scale[2]);
                }
                if (ImGui.button("Remove")) {
                    Renderer.resetAccFrames();
                    scene.removeBox(i);
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
                float[] radius = new float[] {scene.getSphere(i).getRadius()};

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
                if (ImGui.dragFloat("Radius", radius, 1, 0, Float.MAX_VALUE)) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setRadius(radius[0]);
                }
                if (ImGui.button("Remove")) {
                    Renderer.resetAccFrames();
                    scene.removeSphere(i);
                }
                ImGui.popID();
                ImGui.separator();
            }
            ImGui.treePop();
        }
        if (ImGui.button("Add box")) {
            scene.addBox(new Box());
            Renderer.resetAccFrames();
        }
        ImGui.sameLine();
        if (ImGui.button("Add Sphere")) {
            scene.addSpheres(new Sphere());
            Renderer.resetAccFrames();
        }
    }
}