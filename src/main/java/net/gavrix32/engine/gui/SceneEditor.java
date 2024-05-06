package net.gavrix32.engine.gui;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.objects.Box;
import net.gavrix32.engine.objects.Plane;
import net.gavrix32.engine.objects.Sphere;
import net.gavrix32.engine.objects.Triangle;
import net.gavrix32.engine.utils.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class SceneEditor {
    private static final ImInt sceneID = new ImInt();

    protected static void update(ArrayList<Scene> scenes, ArrayList<String> sceneNames) {
        ImGui.begin("Scene", ImGuiWindowFlags.NoMove);
        String[] names = new String[sceneNames.size()];
        if (ImGui.combo("select scene", sceneID, sceneNames.toArray(names))) {
            Renderer.resetAccFrames();
            Renderer.resetAccTexture();
        }
        Renderer.setScene(scenes.get(sceneID.get()));
        showSceneObjectProps(scenes.get(sceneID.get()));
        ImGui.end();
    }

    private static void showSceneObjectProps(Scene scene) {
        if (ImGui.treeNode("camera")) {
            float[] pos = new float[] {
                    scene.getCamera().getPosition().x,
                    scene.getCamera().getPosition().y,
                    scene.getCamera().getPosition().z
            };
            float[] rot = new float[] {
                    scene.getCamera().getRotation().x,
                    scene.getCamera().getRotation().y,
                    scene.getCamera().getRotation().z
            };
            float[] fov = new float[] {scene.getCamera().getFov()};

            if (ImGui.dragFloat3("camera position", pos)) {
                scene.getCamera().setPosition(pos[0], pos[1], pos[2]);
                Renderer.resetAccFrames();
            }
            if (ImGui.dragFloat3("camera rotation", rot)) {
                scene.getCamera().setRotation(rot[0], rot[1], rot[2]);
                Renderer.resetAccFrames();
            }
            if (ImGui.dragFloat("fov", fov, 1, 0, 180, "%.2f°")) scene.getCamera().setFov(fov[0]);
            ImGui.treePop();
        }
        if (ImGui.treeNode("sky")) {
            float[] color = new float[] {
                    scene.getSky().getColor().x,
                    scene.getSky().getColor().y,
                    scene.getSky().getColor().z
            };
            float[] emission = new float[] {scene.getSky().getMaterial().getEmission()};
            float[] roughness = new float[] {scene.getSky().getMaterial().getRoughness()};
            float[] IOR = new float[] {scene.getSky().getMaterial().getIOR()};
            ImBoolean isMetal = new ImBoolean(scene.getSky().getMaterial().isMetal());
            ImBoolean isGlass = new ImBoolean(scene.getSky().getMaterial().isGlass());

            ImBoolean skyHasTexture = new ImBoolean(scene.getSky().hasTexture());
            if (ImGui.checkbox("texture", skyHasTexture)) {
                Renderer.resetAccFrames();
                scene.getSky().setHasTexture(skyHasTexture.get());
            };
            ImGui.sameLine();
            ImInt skyTextureId = new ImInt();

            /*File dir = null;
            try {
                dir = new File(SceneEditor.class.getResource("/textures").toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }*/
            //Logger.info(dir.exists());
            /*File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    System.out.println(file.getName());
                }
            }*/

            String[] skyTextureNames = new String[] {"HDR_041_Path_Env.hdr", "texture2"};
            ImGui.pushID("sky_textures");
            if (ImGui.combo("", skyTextureId, skyTextureNames)) {
                Renderer.resetAccFrames();
            };
            ImGui.popID();
            if (skyHasTexture.get()) ImGui.beginDisabled();
            if (ImGui.colorEdit3("color", color)) {
                Renderer.resetAccFrames();
                scene.getSky().setColor(color[0], color[1], color[2]);
            }
            if (skyHasTexture.get()) ImGui.endDisabled();
            if (ImGui.checkbox("metal", isMetal)) {
                Renderer.resetAccFrames();
                scene.getSky().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
            }
            if (ImGui.dragFloat("emission", emission, 0.01f, 0, Float.MAX_VALUE, "%.2f")) {
                Renderer.resetAccFrames();
                scene.getSky().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
            }
            if (ImGui.sliderFloat("roughness", roughness, 0, 1, "%.2f")) {
                Renderer.resetAccFrames();
                scene.getSky().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
            }
            if (ImGui.checkbox("glass", isGlass)) {
                Renderer.resetAccFrames();
                scene.getSky().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
            }
            ImGui.beginDisabled(!isGlass.get());
            if (ImGui.dragFloat("ior", IOR, 0.01f, 1, Float.MAX_VALUE, "%.2f")) {
                Renderer.resetAccFrames();
                scene.getSky().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
            }
            ImGui.endDisabled();
            ImGui.treePop();
        }
        if (scene.getPlane() != null) {
            if (ImGui.treeNode("plane")) {
                float[] color = new float[] {
                        scene.getPlane().getColor().x,
                        scene.getPlane().getColor().y,
                        scene.getPlane().getColor().z
                };
                float[] color1 = new float[] {
                        scene.getPlane().getFirstColor().x,
                        scene.getPlane().getFirstColor().y,
                        scene.getPlane().getFirstColor().z
                };
                float[] color2 = new float[] {
                        scene.getPlane().getSecondColor().x,
                        scene.getPlane().getSecondColor().y,
                        scene.getPlane().getSecondColor().z
                };
                float[] scale = new float[] {scene.getPlane().getScale()};
                float[] emission = new float[] {scene.getPlane().getMaterial().getEmission()};
                float[] roughness = new float[] {scene.getPlane().getMaterial().getRoughness()};
                float[] IOR = new float[] {scene.getPlane().getMaterial().getIOR()};
                ImBoolean isMetal = new ImBoolean(scene.getPlane().getMaterial().isMetal());
                ImBoolean isGlass = new ImBoolean(scene.getPlane().getMaterial().isGlass());
                ImBoolean checkerBoard = new ImBoolean(scene.getPlane().isCheckerBoard());

                if (ImGui.checkbox("is metal", isMetal)) {
                    Renderer.resetAccFrames();
                    scene.getPlane().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (checkerBoard.get()) {
                    if (ImGui.colorEdit3("color 1", color1)) {
                        Renderer.resetAccFrames();
                        scene.getPlane().setFirstColor(color1[0], color1[1], color1[2]);
                    }
                    if (ImGui.colorEdit3("color 2", color2)) {
                        Renderer.resetAccFrames();
                        scene.getPlane().setSecondColor(color2[0], color2[1], color2[2]);
                    }
                    if (ImGui.dragFloat("scale", scale, 1.0f, 0.0f, Float.MAX_VALUE)) {
                        Renderer.resetAccFrames();
                        scene.getPlane().setScale(scale[0]);
                    }
                } else {
                    if (ImGui.colorEdit3("color", color)) {
                        Renderer.resetAccFrames();
                        scene.getPlane().setColor(color[0], color[1], color[2]);
                    }
                }
                if (ImGui.dragFloat("emission", emission, 0.01f, 0, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getPlane().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.sliderFloat("roughness", roughness, 0, 1, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getPlane().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.checkbox("glass", isGlass)) {
                    Renderer.resetAccFrames();
                    scene.getPlane().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                ImGui.beginDisabled(!isGlass.get());
                if (ImGui.dragFloat("ior", IOR, 0.01f, 1, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getPlane().setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                ImGui.endDisabled();
                if (ImGui.button("remove")) {
                    Renderer.resetAccFrames();
                    scene.setPlane(null);
                }
                ImGui.treePop();
            }
        }
        if (ImGui.treeNode("spheres")) {
            for (int i = 0; i < scene.getSpheres().size(); i++) {
                float[] color = new float[] {
                        scene.getSphere(i).getColor().x,
                        scene.getSphere(i).getColor().y,
                        scene.getSphere(i).getColor().z
                };
                ImBoolean isMetal = new ImBoolean(scene.getSphere(i).getMaterial().isMetal());
                ImBoolean isGlass = new ImBoolean(scene.getSphere(i).getMaterial().isGlass());
                float[] emission = new float[] {scene.getSphere(i).getMaterial().getEmission()};
                float[] roughness = new float[] {scene.getSphere(i).getMaterial().getRoughness()};
                float[] IOR = new float[] {scene.getSphere(i).getMaterial().getIOR()};
                float[] position = new float[] {
                        scene.getSphere(i).getPos().x,
                        scene.getSphere(i).getPos().y,
                        scene.getSphere(i).getPos().z
                };
                float[] radius = new float[] {scene.getSphere(i).getRadius()};

                ImGui.pushID(i);
                if (ImGui.colorEdit3("sphere " + i + " color", color)) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setColor(color[0], color[1], color[2]);
                }
                if (ImGui.checkbox("is metal", isMetal)) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.dragFloat("emission", emission, 0.01f, 0, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.sliderFloat("roughness", roughness, 0, 1, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.checkbox("glass", isGlass)) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                ImGui.beginDisabled(!isGlass.get());
                if (ImGui.dragFloat("ior", IOR, 0.01f, 1, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                ImGui.endDisabled();
                if (ImGui.dragFloat3("position", position)) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setPos(position[0], position[1], position[2]);
                }
                if (ImGui.dragFloat("radius", radius, 1, 0, Float.MAX_VALUE)) {
                    Renderer.resetAccFrames();
                    scene.getSphere(i).setRadius(radius[0]);
                }
                if (ImGui.button("remove")) {
                    Renderer.resetAccFrames();
                    scene.removeSphere(i);
                }
                ImGui.popID();
                ImGui.separator();
            }
            ImGui.treePop();
        }
        if (ImGui.treeNode("boxes")) {
            for (int i = 0; i < scene.getBoxes().size(); i++) {
                float[] color = new float[] {
                        scene.getBox(i).getColor().x,
                        scene.getBox(i).getColor().y,
                        scene.getBox(i).getColor().z
                };
                ImBoolean isMetal = new ImBoolean(scene.getBox(i).getMaterial().isMetal());
                ImBoolean isGlass = new ImBoolean(scene.getBox(i).getMaterial().isGlass());
                float[] emission = new float[] {scene.getBox(i).getMaterial().getEmission()};
                float[] roughness = new float[] {scene.getBox(i).getMaterial().getRoughness()};
                float[] IOR = new float[] {scene.getBox(i).getMaterial().getIOR()};
                float[] position = new float[] {
                        scene.getBox(i).getPos().x,
                        scene.getBox(i).getPos().y,
                        scene.getBox(i).getPos().z
                };
                float[] rotation = new float[] {
                        scene.getBox(i).getRot().x,
                        scene.getBox(i).getRot().y,
                        scene.getBox(i).getRot().z
                };
                float[] scale = new float[] {
                        scene.getBox(i).getScale().x,
                        scene.getBox(i).getScale().y,
                        scene.getBox(i).getScale().z
                };

                ImGui.pushID(i);
                if (ImGui.colorEdit3("box " + i + " color", color)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setColor(color[0], color[1], color[2]);
                }
                if (ImGui.checkbox("is metal", isMetal)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.dragFloat("emission", emission, 0.01f, 0, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.sliderFloat("roughness", roughness, 0, 1, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.checkbox("glass", isGlass)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                ImGui.beginDisabled(!isGlass.get());
                if (ImGui.dragFloat("ior", IOR, 0.01f, 1, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                ImGui.endDisabled();
                if (ImGui.dragFloat3("position", position)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setPos(position[0], position[1], position[2]);
                }
                if (ImGui.dragFloat3("rotation", rotation)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setRot(rotation[0], rotation[1], rotation[2]);
                }
                if (ImGui.dragFloat3("scale", scale, 1, 0, Float.MAX_VALUE)) {
                    Renderer.resetAccFrames();
                    scene.getBox(i).setScale(scale[0], scale[1], scale[2]);
                }
                if (ImGui.button("remove")) {
                    Renderer.resetAccFrames();
                    scene.removeBox(i);
                }
                ImGui.popID();
                ImGui.separator();
            }
            ImGui.treePop();
        }
        if (ImGui.treeNode("triangles")) {
            for (int i = 0; i < scene.getTriangles().size(); i++) {
                float[] color = new float[] {
                        scene.getTriangle(i).getColor().x,
                        scene.getTriangle(i).getColor().y,
                        scene.getTriangle(i).getColor().z
                };
                ImBoolean isMetal = new ImBoolean(scene.getTriangle(i).getMaterial().isMetal());
                ImBoolean isGlass = new ImBoolean(scene.getTriangle(i).getMaterial().isGlass());
                float[] emission = new float[] {scene.getTriangle(i).getMaterial().getEmission()};
                float[] roughness = new float[] {scene.getTriangle(i).getMaterial().getRoughness()};
                float[] IOR = new float[] {scene.getTriangle(i).getMaterial().getIOR()};
                float[] v1 = new float[] {
                        scene.getTriangle(i).getV1().x,
                        scene.getTriangle(i).getV1().y,
                        scene.getTriangle(i).getV1().z
                };
                float[] v2 = new float[] {
                        scene.getTriangle(i).getV2().x,
                        scene.getTriangle(i).getV2().y,
                        scene.getTriangle(i).getV2().z
                };
                float[] v3 = new float[] {
                        scene.getTriangle(i).getV3().x,
                        scene.getTriangle(i).getV3().y,
                        scene.getTriangle(i).getV3().z
                };
                float[] rotation = new float[] {
                        scene.getTriangle(i).getRot().x,
                        scene.getTriangle(i).getRot().y,
                        scene.getTriangle(i).getRot().z
                };

                ImGui.pushID(i);
                if (ImGui.colorEdit3("box " + i + " color", color)) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setColor(color[0], color[1], color[2]);
                }
                if (ImGui.checkbox("is metal", isMetal)) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.dragFloat("emission", emission, 0.01f, 0, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.sliderFloat("roughness", roughness, 0, 1, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                if (ImGui.checkbox("glass", isGlass)) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                ImGui.beginDisabled(!isGlass.get());
                if (ImGui.dragFloat("ior", IOR, 0.01f, 1, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setMaterial(isMetal.get(), emission[0], roughness[0], IOR[0], isGlass.get());
                }
                ImGui.endDisabled();
                if (ImGui.dragFloat3("v1", v1)) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setV1(v1[0], v1[1], v1[2]);
                }
                if (ImGui.dragFloat3("v2", v2)) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setV2(v2[0], v2[1], v2[2]);
                }
                if (ImGui.dragFloat3("v3", v3)) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setV3(v3[0], v3[1], v3[2]);
                }
                if (ImGui.dragFloat3("rotation", rotation)) {
                    Renderer.resetAccFrames();
                    scene.getTriangle(i).setRot(rotation[0], rotation[1], rotation[2]);
                }
                if (ImGui.button("remove")) {
                    Renderer.resetAccFrames();
                    scene.removeTriangle(i);
                }
                ImGui.popID();
                ImGui.separator();
            }
            ImGui.treePop();
        }
        if (ImGui.button("add sphere")) {
            scene.addSpheres(new Sphere());
            Renderer.resetAccFrames();
        }
        ImGui.sameLine();
        if (ImGui.button("add box")) {
            scene.addBox(new Box());
            Renderer.resetAccFrames();
        }
        ImGui.sameLine();
        if (ImGui.button("add triangle")) {
            scene.addTriangle(new Triangle());
            Renderer.resetAccFrames();
        }
        if (scene.getPlane() == null) {
            if (ImGui.button("add plane")) {
                scene.setPlane(new Plane(true));
                Renderer.resetAccFrames();
            }
        }
    }

    public static void setDefaultScene(int numberInList) {
        sceneID.set(numberInList);
    }
}