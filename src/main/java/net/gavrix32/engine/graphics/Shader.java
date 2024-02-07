package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Vec2;
import net.gavrix32.engine.math.Vec3;
import net.gavrix32.engine.utils.Utils;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int program;

    public Shader(String vertexPath, String fragmentPath) {
        int vertex = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertex, Utils.loadString(vertexPath));
        glCompileShader(vertex);
        if (glGetShaderi(vertex, GL_COMPILE_STATUS) == 0) {
            System.err.println(glGetShaderInfoLog(vertex));
        }

        int fragment = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment, Utils.loadString(fragmentPath));
        glCompileShader(fragment);
        if (glGetShaderi(fragment, GL_COMPILE_STATUS) == 0) {
            System.err.println(glGetShaderInfoLog(fragment));
        }

        program = glCreateProgram();
        glAttachShader(program, vertex);
        glAttachShader(program, fragment);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            System.err.println(glGetProgramInfoLog(program));
        }
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(program, name), value);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(program, name), value);
    }

    public void setVec2(String name, Vec2 value) {
        glUniform2f(glGetUniformLocation(program, name), value.x, value.y);
    }

    public void setVec3(String name, Vec3 value) {
        glUniform3f(glGetUniformLocation(program, name), value.x, value.y, value.z);
    }

    public void setMat4(String name, Matrix4f matrix) {
        glUniformMatrix4fv(glGetUniformLocation(program, name), false, matrix.get(new float[16]));
    }

    public void use() {
        glUseProgram(program);
    }
}