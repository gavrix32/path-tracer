package net.gavrix32.engine.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import net.gavrix32.engine.io.Input;
import net.gavrix32.engine.io.Window;

import static org.lwjgl.opengl.GL30C.*;

public class Viewport {
    private static int frameBuffer, viewportTexture, renderBuffer, width, height, widthDelta, heightDelta;
    private static boolean cursorInViewport;

    static {
        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        viewportTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, viewportTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, viewportTexture, 0);

        renderBuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, Window.getWidth(), Window.getHeight());
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    protected static void update() {
        ImGui.begin("Viewport", ImGuiWindowFlags.NoMove);
        widthDelta = width - (int) ImGui.getContentRegionAvail().x;
        heightDelta = height - (int) ImGui.getContentRegionAvail().y;
        width = (int) ImGui.getContentRegionAvail().x;
        height = (int) ImGui.getContentRegionAvail().y;
        glBindTexture(GL_TEXTURE_2D, viewportTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        glBindTexture(GL_TEXTURE_2D, viewportTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, viewportTexture, 0);

        glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);
        glViewport(0, 0, width, height);

        ImVec2 pos = ImGui.getCursorScreenPos();
        ImGui.getWindowDrawList().addImage(viewportTexture,
                pos.x, pos.y,pos.x + width, pos.y + height,
                0, 1, 1, 0);
        cursorInViewport = Input.getCursorX() >= pos.x && Input.getCursorY() >= pos.y &&
                Window.getWidth() - pos.x - width < Window.getWidth() - Input.getCursorX() &&
                Window.getHeight() - pos.y - height < Window.getHeight() - Input.getCursorY();
        ImGui.end();
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

    public static void bindFramebuffer() {
        if (Editor.status) glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
    }

    public static void unbindFramebuffer() {
        if (Editor.status) glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public static boolean cursorInViewport() {
        if (!Window.isCursorVisible()) return true;
        if (!Editor.isEnabled()) return true;
        return cursorInViewport;
    }
}