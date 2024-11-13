package net.gavrix32.engine.graphics;

import net.gavrix32.engine.io.Window;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46C.*;

public class Texture {
    protected final int id;

    protected Texture() {
        id = glGenTextures();
    }

    protected void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    protected void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    protected void texImage(int internalFormat, int type, long pixels) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, Window.getWidth(), Window.getHeight(), 0, GL_RGB, type, pixels);
    }

    protected void texImage(int internalFormat, int type, ByteBuffer pixels) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, Window.getWidth(), Window.getHeight(), 0, GL_RGB, type, pixels);
    }

    protected void texImage(int internalFormat, int type, FloatBuffer pixels) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, Window.getWidth(), Window.getHeight(), 0, GL_RGB, type, pixels);
    }

    protected void linearFiltering() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }

    protected void clampToEdge() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }
}