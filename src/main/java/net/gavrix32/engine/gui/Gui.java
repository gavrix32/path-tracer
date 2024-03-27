package net.gavrix32.engine.gui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.gavrix32.engine.graphics.Renderer;
import net.gavrix32.engine.graphics.Scene;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.utils.Utils;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class Gui {
    public static boolean status = true;
    private static final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private static final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private static final String fontPath = "fonts/Inter-Regular.ttf";

    static {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);
        ImGui.loadIniSettingsFromMemory(Utils.loadString("imgui.ini"));
        io.getFonts().addFontFromMemoryTTF(Utils.loadBytes(fontPath), 15);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        Style.apply();
        imGuiImplGlfw.init(Window.get(), true);
        imGuiImplGl3.init();
    }

    public static void update(ArrayList<Scene> scenes, ArrayList<String> sceneNames) {
        if (status) {
            imGuiImplGlfw.newFrame();
            ImGui.newFrame();
            ImGui.dockSpaceOverViewport();
            ImGui.beginDisabled(!Window.isCursorVisible());

            Render.update();
            SceneEditor.update(scenes, sceneNames);
            Logs.update();
            Viewport.update();

            ImGui.endDisabled();
            ImGui.render();
            imGuiImplGl3.renderDrawData(ImGui.getDrawData());
            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                glfwMakeContextCurrent(backupWindowPtr);
            }
        }
    }

    public static void toggle() {
        status = !status;
        Renderer.resetAccFrames();
        Renderer.resetAccTexture();
    }

    public static void setStatus(boolean status) {
        Gui.status = status;
    }

    public static boolean isEnabled() {
        return status;
    }
}