package net.gavrix32.engine.graphics;

import net.gavrix32.engine.utils.Logger;
import net.gavrix32.engine.utils.Utils;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46C.*;
import static org.lwjgl.stb.STBImage.*;

public class Sky {
    private Vector3f color;
    private Material material;
    private String path;
    protected boolean hasTexture;
    private int texture;

    public Sky() {
        color = new Vector3f(0);
        hasTexture = false;
        material = new Material(0, 1, false);
    }

    public Sky(Vector3f color) {
        this.color = color;
        hasTexture = false;
        material = new Material(1, 1, false);
    }

    public Sky(String path) {
        this.path = path;
        texture = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        int[] width = new int[1], height = new int[1];
        byte[] bytes = Utils.loadBytes(path);
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        stbi_set_flip_vertically_on_load(true);
        FloatBuffer data = stbi_loadf_from_memory(buffer, width, height,  new int[1], 3);
        if (data == null) Logger.error("Failed to load texture: " + path + " " + stbi_failure_reason());
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width[0], height[0], 0, GL_RGB, GL_FLOAT, data);
        glBindTexture(GL_TEXTURE_2D, 0);
        color = new Vector3f(0);
        hasTexture = true;
        material = new Material(1, 1, false);
    }

    public void bindTexture() {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setMaterial(float emission, float roughness, boolean isMetal) {
        this.material.setEmission(emission);
        this.material.setRoughness(roughness);
        this.material.setMetal(isMetal);
    }

    public Vector3f getColor() {
        return color;
    }

    public Sky setColor(Vector3f color) {
        this.color = color;
        return this;
    }

    public Sky setColor(float r, float g, float b) {
        this.color.x = r;
        this.color.y = g;
        this.color.z = b;
        return this;
    }
}
