package net.gavrix32.engine.graphics;

import net.gavrix32.engine.io.Window;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.ARBInternalformatQuery2.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL15C.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL42C.glBindImageTexture;
import static org.lwjgl.opengl.GL42C.glTexStorage2D;

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
    private static int accFrames = 0;
    private static int samples = 8, bounces = 4, AASize = 150;
    private static boolean
            accumulation = false, reproj = false, randNoise = false, ACESFilm = true,
            showAlbedo = false, showNormals = false, showDepth = false;
    private static int accTexture;

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

        // Accumulation
        accTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, accTexture);
        glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, Window.getWidth(), Window.getHeight());
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void render(Scene scene) {
        scene.getCamera().update();
        quadShader.setVec2("u_resolution", Window.getWidth(), Window.getHeight());
        quadShader.setFloat("u_time", (float) glfwGetTime());
        quadShader.setVec3("u_camera_position", scene.getCamera().getPos());
        quadShader.setMat4("u_camera_rotation", scene.getCamera().getRotMatrix());
        quadShader.setFloat("u_acc_frames", accFrames);
        quadShader.setInt("u_show_albedo", showAlbedo ? 1 : 0);
        quadShader.setInt("u_show_normals", showNormals ? 1 : 0);
        quadShader.setInt("u_show_depth", showDepth ? 1 : 0);
        quadShader.setInt("u_samples", samples);
        quadShader.setInt("u_bounces", bounces);
        quadShader.setInt("u_random_noise", randNoise ? 1 : 0);
        quadShader.setInt("u_reproj", reproj ? 1 : 0);
        quadShader.setInt("u_aa_size", AASize);
        quadShader.setInt("u_aces", ACESFilm ? 1 : 0);
        quadShader.setInt("tex", 0);
        quadShader.setInt("sky_has_texture", scene.getSky().hasTexture ? 1 : 0);
        if (scene.getSky().hasTexture) {
            quadShader.setInt("sky_texture", 1);
        } else {
            quadShader.setVec3("sky_color", scene.getSky().getColor());
        }
        for (int i = 0; i < scene.getBoxes().length; i++) {
            quadShader.setVec3("boxes[" + i + "].position", scene.getBoxes()[i].getPos());
            quadShader.setVec3("boxes[" + i + "].rotation", scene.getBoxes()[i].getRot());
            quadShader.setVec3("boxes[" + i + "].size", scene.getBoxes()[i].getSize());
            quadShader.setVec3("boxes[" + i + "].color", scene.getBoxes()[i].getCol());
            quadShader.setFloat("boxes[" + i + "].material.emission", scene.getBoxes()[i].getMaterial().getEmission());
            quadShader.setFloat("boxes[" + i + "].material.roughness", scene.getBoxes()[i].getMaterial().getRoughness());
        }
        for (int i = 0; i < scene.getSpheres().length; i++) {
            quadShader.setVec3("spheres[" + i + "].position", scene.getSpheres()[i].getPos());
            quadShader.setFloat("spheres[" + i + "].radius", scene.getSpheres()[i].getRadius());
            quadShader.setVec3("spheres[" + i + "].color", scene.getSpheres()[i].getCol());
            quadShader.setFloat("spheres[" + i + "].material.emission", scene.getSpheres()[i].getMaterial().getEmission());
            quadShader.setFloat("spheres[" + i + "].material.roughness", scene.getSpheres()[i].getMaterial().getRoughness());
        }
        if (accumulation || reproj) glBindImageTexture(0, accTexture, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        if (accFrames == 0 && !reproj) resetFramebufferTexture();
        glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0);
        accFrames++;
    }

    public static void setSamples(int samples) {
        Renderer.samples = samples;
    }

    public static void setBounces(int bounces) {
        Renderer.bounces = bounces;
    }

    public static void useAccumulation(boolean value) {
        if (!value) resetAccFrames();
        accumulation = value;
    }

    public static void useReprojection(boolean value) {
        reproj = value;
    }

    public static void resetAccFrames() {
        accFrames = 0;
    }

    public static int getAccFrames() {
        return accFrames;
    }

    public static void resetFramebufferTexture() {
        glDeleteTextures(accTexture);
        accTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, accTexture);
        glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA32F, Window.getWidth(), Window.getHeight());
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public static void useRandomNoise(boolean value) {
        randNoise = value;
    }

    public static void useACESFilm(boolean value) {
        ACESFilm = value;
    }

    public static void setAASize(int aaSize) {
        AASize = aaSize;
    }

    public static void showAlbedo(boolean value) {
        if (value) resetAccFrames();
        showAlbedo = value;
    }

    public static void showNormals(boolean value) {
        if (value) resetAccFrames();
        showNormals = value;
    }

    public static void showDepth(boolean value) {
        if (value) resetAccFrames();
        showDepth = value;
    }
}