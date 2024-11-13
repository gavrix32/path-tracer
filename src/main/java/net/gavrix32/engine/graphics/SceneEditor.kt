package net.gavrix32.engine.graphics

import imgui.ImGui
import imgui.type.ImBoolean
import imgui.type.ImInt
import net.gavrix32.engine.objects.Box
import net.gavrix32.engine.objects.Plane
import net.gavrix32.engine.objects.Sphere
import net.gavrix32.engine.utils.Utils

class SceneEditor {
    fun showSceneObjectProps(scene: Scene) {
        if (ImGui.treeNode("camera")) {
            val pos = floatArrayOf(
                scene.camera.position.x,
                scene.camera.position.y,
                scene.camera.position.z
            )
            val rot = floatArrayOf(
                scene.camera.rotation.x,
                scene.camera.rotation.y,
                scene.camera.rotation.z
            )

            if (ImGui.dragFloat3("camera position", pos)) {
                scene.camera.setPosition(pos[0], pos[1], pos[2])
                Renderer.resetAccFrames()
            }
            if (ImGui.dragFloat3("camera rotation", rot)) {
                scene.camera.setRotation(rot[0], rot[1], rot[2])
                Renderer.resetAccFrames()
            }
            ImGui.treePop()
        }
        if (ImGui.treeNode("sky")) {
            val color = floatArrayOf(
                scene.sky.color.x,
                scene.sky.color.y,
                scene.sky.color.z
            )
            val emission = floatArrayOf(scene.sky.material.emission)
            val roughness = floatArrayOf(scene.sky.material.roughness)
            val ior = floatArrayOf(scene.sky.material.ior)
            val isMetal = ImBoolean(scene.sky.material.isMetal)
            val isGlass = ImBoolean(scene.sky.material.isGlass)
            val skyHasTexture = ImBoolean(scene.sky.hasTexture())

            if (ImGui.checkbox("texture", skyHasTexture)) {
                Renderer.resetAccFrames()
                scene.sky.setHasTexture(skyHasTexture.get())
            }
            ImGui.sameLine()

            val skyTextureId = ImInt()
            val skyTextureNames = Utils.listResources("textures/sky")
            ImGui.pushID("sky_textures")
            if (ImGui.combo("", skyTextureId, skyTextureNames)) {
                Renderer.resetAccFrames()
                scene.sky.setTexture("textures/sky/" + skyTextureNames[skyTextureId.get()])
            }
            ImGui.popID()
            if (skyHasTexture.get()) ImGui.beginDisabled()
            if (ImGui.colorEdit3("color", color)) {
                Renderer.resetAccFrames()
                scene.sky.setColor(color[0], color[1], color[2])
            }
            if (skyHasTexture.get()) ImGui.endDisabled()
            if (ImGui.checkbox("metal", isMetal)) {
                Renderer.resetAccFrames()
                scene.sky.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
            }
            if (ImGui.dragFloat("emission", emission, 0.01f, 0f, Float.MAX_VALUE, "%.2f")) {
                Renderer.resetAccFrames()
                scene.sky.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
            }
            if (ImGui.sliderFloat("roughness", roughness, 0f, 1f, "%.2f")) {
                Renderer.resetAccFrames()
                scene.sky.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
            }
            if (ImGui.checkbox("glass", isGlass)) {
                Renderer.resetAccFrames()
                scene.sky.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
            }
            ImGui.beginDisabled(!isGlass.get())
            if (ImGui.dragFloat("ior", ior, 0.01f, 1f, Float.MAX_VALUE, "%.2f")) {
                Renderer.resetAccFrames()
                scene.sky.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
            }
            ImGui.endDisabled()
            ImGui.treePop()
        }
        if (scene.plane != null) {
            if (ImGui.treeNode("plane")) {
                val color = floatArrayOf(scene.plane.color.x, scene.plane.color.y, scene.plane.color.z)
                val color1 = floatArrayOf(scene.plane.firstColor.x, scene.plane.firstColor.y, scene.plane.firstColor.z)
                val color2 = floatArrayOf(scene.plane.secondColor.x, scene.plane.secondColor.y, scene.plane.secondColor.z)
                val scale = floatArrayOf(scene.plane.scale)
                val emission = floatArrayOf(scene.plane.material.emission)
                val roughness = floatArrayOf(scene.plane.material.roughness)
                val ior = floatArrayOf(scene.plane.material.ior)
                val isMetal = ImBoolean(scene.plane.material.isMetal)
                val isGlass = ImBoolean(scene.plane.material.isGlass)
                val checkerBoard = ImBoolean(scene.plane.isCheckerBoard)

                if (ImGui.checkbox("is metal", isMetal)) {
                    Renderer.resetAccFrames()
                    scene.plane.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                if (checkerBoard.get()) {
                    if (ImGui.colorEdit3("color 1", color1)) {
                        Renderer.resetAccFrames()
                        scene.plane.setFirstColor(color1[0], color1[1], color1[2])
                    }
                    if (ImGui.colorEdit3("color 2", color2)) {
                        Renderer.resetAccFrames()
                        scene.plane.setSecondColor(color2[0], color2[1], color2[2])
                    }
                    if (ImGui.dragFloat("scale", scale, 1.0f, 0.0f, Float.MAX_VALUE)) {
                        Renderer.resetAccFrames()
                        scene.plane.setScale(scale[0])
                    }
                } else {
                    if (ImGui.colorEdit3("color", color)) {
                        Renderer.resetAccFrames()
                        scene.plane.setColor(color[0], color[1], color[2])
                    }
                }
                if (ImGui.dragFloat("emission", emission, 0.01f, 0f, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames()
                    scene.plane.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                if (ImGui.sliderFloat("roughness", roughness, 0f, 1f, "%.2f")) {
                    Renderer.resetAccFrames()
                    scene.plane.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                if (ImGui.checkbox("glass", isGlass)) {
                    Renderer.resetAccFrames()
                    scene.plane.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                ImGui.beginDisabled(!isGlass.get())
                if (ImGui.dragFloat("ior", ior, 0.01f, 1f, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames()
                    scene.plane.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                ImGui.endDisabled()
                if (ImGui.button("remove")) {
                    Renderer.resetAccFrames()
                    scene.plane = null
                }
                ImGui.treePop()
            }
        }
        if (ImGui.treeNode("spheres")) {
            val iterator = scene.spheres.iterator()
            var index = 0
            while (iterator.hasNext()) {
                val sph = iterator.next()
                val color = floatArrayOf(sph.color.x, sph.color.y, sph.color.z)
                val isMetal = ImBoolean(sph.material.isMetal)
                val isGlass = ImBoolean(sph.material.isGlass)
                val emission = floatArrayOf(sph.material.emission)
                val roughness = floatArrayOf(sph.material.roughness)
                val ior = floatArrayOf(sph.material.ior)
                val position = floatArrayOf(sph.pos.x, sph.pos.y, sph.pos.z)
                val radius = floatArrayOf(sph.radius)

                ImGui.pushID(index)
                if (ImGui.colorEdit3("sphere $index color", color)) {
                    Renderer.resetAccFrames()
                    sph.setColor(color[0], color[1], color[2])
                }
                if (ImGui.checkbox("is metal", isMetal)) {
                    Renderer.resetAccFrames()
                    sph.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                if (ImGui.dragFloat("emission", emission, 0.01f, 0f, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames()
                    sph.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                if (ImGui.sliderFloat("roughness", roughness, 0f, 1f, "%.2f")) {
                    Renderer.resetAccFrames()
                    sph.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                if (ImGui.checkbox("glass", isGlass)) {
                    Renderer.resetAccFrames()
                    sph.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                ImGui.beginDisabled(!isGlass.get())
                if (ImGui.dragFloat("ior", ior, 0.01f, 1f, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames()
                    sph.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                ImGui.endDisabled()
                if (ImGui.dragFloat3("position", position, 0.1f)) {
                    Renderer.resetAccFrames()
                    sph.setPos(position[0], position[1], position[2])
                }
                if (ImGui.dragFloat("radius", radius, 0.1f, 0.01f, Float.MAX_VALUE)) {
                    Renderer.resetAccFrames()
                    sph.radius = radius[0]
                }
                if (ImGui.button("remove")) {
                    Renderer.resetAccFrames()
                    iterator.remove()
                }
                ImGui.popID()
                ImGui.separator()
                index++
            }
            ImGui.treePop()
        }
        if (ImGui.treeNode("boxes")) {
            val iterator = scene.boxes.iterator()
            var index = 0
            while (iterator.hasNext()) {
                val box = iterator.next()
                val color = floatArrayOf(box.color.x, box.color.y, box.color.z)
                val isMetal = ImBoolean(box.material.isMetal)
                val isGlass = ImBoolean(box.material.isGlass)
                val emission = floatArrayOf(box.material.emission)
                val roughness = floatArrayOf(box.material.roughness)
                val ior = floatArrayOf(box.material.ior)
                val position = floatArrayOf(box.pos.x, box.pos.y, box.pos.z)
                val rotation = floatArrayOf(box.rot.x, box.rot.y, box.rot.z)
                val scale = floatArrayOf(box.scale.x, box.scale.y, box.scale.z)

                ImGui.pushID(index)
                if (ImGui.colorEdit3("box $index color", color)) {
                    Renderer.resetAccFrames()
                    box.setColor(color[0], color[1], color[2])
                }
                if (ImGui.checkbox("is metal", isMetal)) {
                    Renderer.resetAccFrames()
                    box.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                if (ImGui.dragFloat("emission", emission, 0.01f, 0f, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames()
                    box.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                if (ImGui.sliderFloat("roughness", roughness, 0f, 1f, "%.2f")) {
                    Renderer.resetAccFrames()
                    box.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                if (ImGui.checkbox("glass", isGlass)) {
                    Renderer.resetAccFrames()
                    box.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                ImGui.beginDisabled(!isGlass.get())
                if (ImGui.dragFloat("ior", ior, 0.01f, 1f, Float.MAX_VALUE, "%.2f")) {
                    Renderer.resetAccFrames()
                    box.setMaterial(isMetal.get(), emission[0], roughness[0], ior[0], isGlass.get())
                }
                ImGui.endDisabled()
                if (ImGui.dragFloat3("position", position)) {
                    Renderer.resetAccFrames()
                    box.setPos(position[0], position[1], position[2])
                }
                if (ImGui.dragFloat3("rotation", rotation)) {
                    Renderer.resetAccFrames()
                    box.setRot(rotation[0], rotation[1], rotation[2])
                }
                if (ImGui.dragFloat3("scale", scale, 1f, 0f, Float.MAX_VALUE)) {
                    Renderer.resetAccFrames()
                    box.setScale(scale[0], scale[1], scale[2])
                }
                if (ImGui.button("remove")) {
                    Renderer.resetAccFrames()
                    iterator.remove()
                }
                ImGui.popID()
                ImGui.separator()
                index++
            }
            ImGui.treePop()
        }

        if (ImGui.button("add sphere")) {
            scene.addSpheres(Sphere())
            Renderer.resetAccFrames()
        }
        ImGui.sameLine()
        if (ImGui.button("add box")) {
            scene.addBox(Box())
            Renderer.resetAccFrames()
        }
        if (scene.plane == null) {
            ImGui.sameLine()
            if (ImGui.button("add plane")) {
                scene.plane = Plane(true)
                Renderer.resetAccFrames()
            }
        }
    }
}
