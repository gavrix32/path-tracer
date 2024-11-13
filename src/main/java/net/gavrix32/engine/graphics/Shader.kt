package net.gavrix32.engine.graphics

import net.gavrix32.engine.io.Window
import net.gavrix32.engine.linearmath.Matrix4f
import net.gavrix32.engine.linearmath.Vector2f
import net.gavrix32.engine.linearmath.Vector3f
import net.gavrix32.engine.linearmath.Vector4f
import net.gavrix32.engine.utils.Utils
import org.lwjgl.opengl.GL46C.*
import org.tinylog.kotlin.Logger

open class Shader {
    private val shaders = mutableListOf<Int>()
    private var program = 0

    fun load(path: String, type: Int) {
        val shader = glCreateShader(type)
        glShaderSource(shader, Utils.loadString(path))
        glCompileShader(shader)
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            Logger.error(path + System.lineSeparator() + glGetShaderInfoLog(shader))
        }
        shaders.add(shader)
    }

    fun initProgram() {
        program = glCreateProgram()
        for (shader in shaders) {
            glAttachShader(program, shader)
        }
        glLinkProgram(program)
        if (glGetProgrami(program, GL_LINK_STATUS) == 0)
            Logger.error("Shader program " + glGetProgramInfoLog(program))
    }

    fun invokeCompute() {
        glDispatchCompute(Window.getWidth() / 8, Window.getHeight() / 8, 1)
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT)
    }

    fun setInt(name: String, value: Int) {
        glUniform1i(glGetUniformLocation(program, name), value)
    }

    fun setBool(name: String, value: Boolean) {
        glUniform1i(glGetUniformLocation(program, name), if (value) 1 else 0)
    }

    fun setFloat(name: String, value: Float) {
        glUniform1f(glGetUniformLocation(program, name), value)
    }

    fun setVec2(name: String, value: Vector2f) {
        glUniform2f(glGetUniformLocation(program, name), value.x, value.y)
    }

    fun setVec3(name: String, value: Vector3f) {
        glUniform3f(glGetUniformLocation(program, name), value.x, value.y, value.z)
    }

    fun setVec4(name: String, value: Vector4f) {
        glUniform4f(glGetUniformLocation(program, name), value.x, value.y, value.z, value.w)
    }

    fun setMat4(name: String, matrix: Matrix4f) {
        glUniformMatrix4fv(glGetUniformLocation(program, name), false, matrix.get())
    }

    fun setMat4(name: String, values: FloatArray) {
        glUniformMatrix4fv(glGetUniformLocation(program, name), false, values)
    }

    fun use() {
        glUseProgram(program)
    }
}