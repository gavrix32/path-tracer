package net.gavrix32.engine.graphics;

import net.gavrix32.engine.math.Matrix4f;
import net.gavrix32.engine.math.Vector2f;
import net.gavrix32.engine.math.Vector3f;
import net.gavrix32.engine.math.Vector4f;
import net.gavrix32.engine.utils.Logger;
import net.gavrix32.engine.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private List<Integer> shaders = new ArrayList<>();
    private int program;

    public void load(String path, int type) {
        int shader = glCreateShader(type);
        glShaderSource(shader, Utils.loadString(path));
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0)
            Logger.error(path + System.lineSeparator() + glGetShaderInfoLog(shader));
        shaders.add(shader);
    }
    
    public void initProgram() {
        program = glCreateProgram();
        for (int shader : shaders) glAttachShader(program, shader);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == 0)
            Logger.error("Shader program " + glGetProgramInfoLog(program));
    }

    /*private String parseIncludes(String code) {
        int includeIndex = code.indexOf("#include");
        while (includeIndex != -1) {
            String includeLine = code.substring(includeIndex, code.indexOf(System.lineSeparator(), includeIndex));
            String includePath = includeLine.replace("#include", "");
            includePath = includePath.replace(" ", "");
            code = code.replace(includeLine, Utils.loadString(includePath));
            includeIndex = code.indexOf("#include", code.indexOf(includePath));
        }
        return code;
    }*/

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(program, name), value);
    }

    public void setBool(String name, boolean value) {
        glUniform1i(glGetUniformLocation(program, name), value ? 1 : 0);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(program, name), value);
    }

    public void setVec2(String name, Vector2f value) {
        glUniform2f(glGetUniformLocation(program, name), value.x, value.y);
    }

    public void setVec3(String name, Vector3f value) {
        glUniform3f(glGetUniformLocation(program, name), value.x, value.y, value.z);
    }

    public void setVec4(String name, Vector4f value) {
        glUniform4f(glGetUniformLocation(program, name), value.x, value.y, value.z, value.w);
    }

    public void setMat4(String name, Matrix4f matrix) {
        glUniformMatrix4fv(glGetUniformLocation(program, name), false, matrix.get());
    }

    public void setMat4(String name, float[] values) {
        glUniformMatrix4fv(glGetUniformLocation(program, name), false, values);
    }

    public void use() {
        glUseProgram(program);
    }

    public int get() {
        return program;
    }
}