package net.gavrix32.engine.graphics;

import static org.lwjgl.opengl.GL46C.*;

public class Framebuffer { // to delete
    protected final int id;

    protected Framebuffer() {
        id = glGenFramebuffers();
    }

    protected void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    protected void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}