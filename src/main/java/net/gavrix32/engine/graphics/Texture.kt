package net.gavrix32.engine.graphics

import net.gavrix32.engine.io.Window
import org.lwjgl.opengl.GL46C.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer

open class Texture {
    protected val id = glGenTextures()

    protected fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
    }

    protected fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    protected fun texImage(internalFormat: Int, type: Int, pixels: Long) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, Window.getWidth(), Window.getHeight(), 0, GL_RGB, type, pixels)
    }

    protected fun texImage(internalFormat: Int, type: Int, pixels: ByteBuffer) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, Window.getWidth(), Window.getHeight(), 0, GL_RGB, type, pixels)
    }

    protected fun texImage(internalFormat: Int, type: Int, pixels: FloatBuffer) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, Window.getWidth(), Window.getHeight(), 0, GL_RGB, type, pixels)
    }

    protected fun linearFiltering() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    }

    protected fun clampToEdge() {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    }
}