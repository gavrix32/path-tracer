package net.gavrix32.engine.gui;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import net.gavrix32.engine.io.Input;
import net.gavrix32.engine.io.Window;

import static org.lwjgl.opengl.GL30C.*;

// Deprecated
public class Viewport {
    private static final int frameBuffer;
    private static final int viewportTexture;
    private static int width, height;
    private static int widthDelta, heightDelta;
    private static boolean cursorInViewport;

    static {
        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        viewportTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, viewportTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, viewportTexture, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    protected static void update(boolean gui) {
        if (gui) {
            ImGui.begin("Viewport", ImGuiWindowFlags.NoMove);
            widthDelta = width - (int) ImGui.getContentRegionAvail().x;
            heightDelta = height - (int) ImGui.getContentRegionAvail().y;
            width = (int) ImGui.getContentRegionAvail().x;
            height = (int) ImGui.getContentRegionAvail().y;
        }

        glBindTexture(GL_TEXTURE_2D, viewportTexture);
        if (gui) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        }
        glBindTexture(GL_TEXTURE_2D, 0);

        if (gui) {
            ImVec2 pos = ImGui.getCursorScreenPos();
            ImGui.getWindowDrawList().addImage(viewportTexture,
                    pos.x, pos.y,pos.x + width, pos.y + height, 0, 1, 1, 0
            );
            cursorInViewport = Input.getCursorX() >= pos.x && Input.getCursorY() >= pos.y &&
                    Window.getWidth() - pos.x - width < Window.getWidth() - Input.getCursorX() &&
                    Window.getHeight() - pos.y - height < Window.getHeight() - Input.getCursorY();
            ImGui.end();
        }
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static int getWidthDelta() {
        return widthDelta;
    }

    public static int getHeightDelta() {
        return heightDelta;
    }

    public static void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, viewportTexture);
    }

    public static void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void bindFramebuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
    }

    public static void unbindFramebuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public static boolean cursorInViewport() {
        if (!Window.isCursorVisible()) return true;
        if (!Gui.isEnabled()) return true;
        return cursorInViewport;
    }

    public static int getTexture() {
        return viewportTexture;
    }
}