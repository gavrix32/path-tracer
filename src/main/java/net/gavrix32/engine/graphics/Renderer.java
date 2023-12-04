package net.gavrix32.engine.graphics;

import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.shapes.Box;
import net.gavrix32.engine.shapes.Sphere;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.*;

public class Renderer {
    private static final float[] VERTICES = {
            -1, -1, 0,
            -1, 1, 0,
            1, 1, 0,
            1, -1, 0
    };
    private static final int[] INDICES = {
            0, 1, 3,
            1, 2, 3
    };
    private static Shader quadShader;
    private static int texture, frameBuffer;
    private static int accFrames = 0;
    private static int samples = 8, bounces = 8, AASize = 0;
    private static boolean accumulate = false, randNoise = false;
    private static float dt;

    public static void init() {
        int vertexArray = glGenVertexArrays();
        glBindVertexArray(vertexArray);

        int vertexBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, VERTICES, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        int indexBuffer = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, INDICES, GL_STATIC_DRAW);

        quadShader = new Shader("shaders/main.vert", "shaders/main.frag");
        quadShader.use();

        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
    }

    public static void render(Camera cam, Box[] boxes, Sphere[] spheres) {
        dt = (float) glfwGetTime();
        cam.update();
        quadShader.setVec2("uRes", Window.getWidth(), Window.getHeight());
        quadShader.setFloat("uTime", (float) glfwGetTime());
        quadShader.setVec3("ro", cam.getPos().x, cam.getPos().y, cam.getPos().z);
        quadShader.setMat4("uRotation", cam.getRotMatrix());
        quadShader.setFloat("uAccumulate", accFrames);
        quadShader.setInt("samples", samples);
        quadShader.setInt("bounces", bounces);
        quadShader.setInt("randNoise", randNoise ? 1 : 0);
        quadShader.setInt("AASize", AASize);
        for (int i = 0; i < boxes.length; i++) {
            quadShader.setVec3("boxes[" + i + "].pos", boxes[i].getPos().x, boxes[i].getPos().y, boxes[i].getPos().z);
            quadShader.setVec3("boxes[" + i + "].size", boxes[i].getSize().x, boxes[i].getSize().y, boxes[i].getSize().z);
            quadShader.setVec3("boxes[" + i + "].col", boxes[i].getCol().x, boxes[i].getCol().y, boxes[i].getCol().z);
            quadShader.setFloat("boxes[" + i + "].material.emission", boxes[i].getMaterial().getEmission());
            quadShader.setFloat("boxes[" + i + "].material.roughness", boxes[i].getMaterial().getRoughness());
        }
        for (int i = 0; i < spheres.length; i++) {
            quadShader.setVec3("spheres[" + i + "].pos", spheres[i].getPos().x, spheres[i].getPos().y, spheres[i].getPos().z);
            quadShader.setVec3("spheres[" + i + "].col", spheres[i].getCol().x, spheres[i].getCol().y, spheres[i].getCol().z);
            quadShader.setFloat("spheres[" + i + "].rad", spheres[i].getRadius());
            quadShader.setFloat("spheres[" + i + "].material.emission", spheres[i].getMaterial().getEmission());
            quadShader.setFloat("spheres[" + i + "].material.roughness", spheres[i].getMaterial().getRoughness());
        }
        if (accumulate && accFrames > 0) {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, Window.getWidth(), Window.getHeight(), 0, GL_RGBA, GL_FLOAT, 0);
            glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
            glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0);
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0);
        accFrames++;
        dt = (float) (glfwGetTime() - dt);
    }

    public static void setSamples(int samples) {
        Renderer.samples = samples;
    }

    public static void setBounces(int bounces) {
        Renderer.bounces = bounces;
    }

    public static void useDenoiser(boolean value) {
        if (!value) resetAccFrames();
        accumulate = value;
    }

    public static void resetAccFrames() {
        accFrames = 0;
    }

    public static float getFrametime() {
        return dt;
    }

    public static void useRandomNoise(boolean randomSeed) {
        Renderer.randNoise = randomSeed;
    }

    public static void setAASize(int aaSize) {
        Renderer.AASize = aaSize;
    }
}