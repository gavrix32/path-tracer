package net.gavrix32.engine.graphics;

import net.gavrix32.app.Main;
import net.gavrix32.engine.utils.Logger;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11C.GL_LINEAR;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL13C.*;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Sky {
    private Vector3f color;
    private Material material;
    private String[] paths;
    protected boolean hasTexture;

    public Sky() {
        this.color = new Vector3f(0);
        hasTexture = false;
        material = new Material(0, 0, false);
    }

    public Sky(Vector3f color) {
        this.color = color;
        hasTexture = false;
        material = new Material(1, 0, false);
    }

    public Sky(String[] paths) {
        this.paths = paths;
        int texture = glGenTextures();
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        for (int i = 0; i < 6; i++) {
            int[] width = new int[1], height = new int[1];
            ByteBuffer data;
            data = stbi_load(Main.class.getClassLoader().getResource(paths[i]).getPath(), width, height,  new int[1], 3);
            //data = stbi_load_from_memory(ByteBuffer.wrap(Utils.loadBytes(skyBoxPaths[i])), width, height,  new int[1], 3);
            if (data == null) Logger.error("Failed to load texture: " + stbi_failure_reason());
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGB, width[0], height[0], 0, GL_RGB, GL_UNSIGNED_BYTE, data);
        }
        hasTexture = true;
        material = new Material(1, 0, false);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Vector3f getColor() {
        return color;
    }

    public Sky setColor(Vector3f color) {
        this.color = color;
        return this;
    }
}
