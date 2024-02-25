package net.gavrix32.engine.utils;

import net.gavrix32.engine.io.Window;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImageWrite.stbi_flip_vertically_on_write;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;

public class Utils {
    public static byte[] loadBytes(String path) {
        try {
            return Objects
                    .requireNonNull(Utils.class.getClassLoader().getResourceAsStream(path))
                    .readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String loadString(String path) {
        return new String(loadBytes(path));
    }

    public static void takeScreenshot(String path) {
        // Buffer capacity = pixels count * component count (red, green, blue)
        ByteBuffer data = BufferUtils.createByteBuffer(Window.getWidth() * Window.getHeight() * 3);
        glReadPixels(0, 0, Window.getWidth(), Window.getHeight(), GL_RGB, GL_UNSIGNED_BYTE, data);
        stbi_flip_vertically_on_write(true);
        if (stbi_write_png(path, Window.getWidth(), Window.getHeight(), 3, data, Window.getWidth() * 3)) {
            Logger.info("Screenshot saved to " + path);
        } else {
            Logger.error("Failed to save screenshot: " + stbi_failure_reason());
        }
    }
}