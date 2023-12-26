package net.gavrix32.engine;

import net.gavrix32.engine.io.Window;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11C.*;
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

    public static void takeScreenshot(String name) {
        // Buffer capacity = pixels count * component count (red, green, blue)
        ByteBuffer buffer = BufferUtils.createByteBuffer(Window.getWidth() * Window.getHeight() * 3);
        glReadPixels(0, 0, Window.getWidth(), Window.getHeight(), GL_RGB, GL_UNSIGNED_BYTE, buffer);
        stbi_flip_vertically_on_write(true);
        stbi_write_png(name, Window.getWidth(), Window.getHeight(), 3, buffer, Window.getWidth() * 3);
    }
}