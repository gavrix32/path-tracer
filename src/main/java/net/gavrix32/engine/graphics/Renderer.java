package net.gavrix32.engine.graphics;

import net.gavrix32.engine.editor.Editor;
import net.gavrix32.engine.editor.Viewport;
import net.gavrix32.engine.io.Input;
import net.gavrix32.engine.io.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46C.*;

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
    private static Scene scene;
    private static Shader quadShader;
    private static int accFrames = 0;
    private static int samples = 1, bounces = 3, AASize = 128;
    private static boolean
            accumulation = true, reproj = true, randNoise = false, gammaCorrection = true, ACESFilm = false,
            showAlbedo = false, showNormals = false, showDepth = false;
    private static int accTexture;
    private static float gamma = 2.2f;
    private static Matrix4f proj, view;

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

        proj = new Matrix4f();
        view = new Matrix4f();
    }

    public static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        quadShader.setMat4("old_view", scene.getCamera().getView());
        quadShader.setVec3("u_old_camera_position", scene.getCamera().getPos());
        scene.getCamera().update();
        if (!Window.isCursorVisible()) {
            scene.getCamera().rotateX(Input.getDeltaY() * -0.003f);
            scene.getCamera().rotateY(Input.getDeltaX() * -0.003f);
            if (Input.getDeltaX() != 0 || Input.getDeltaY() != 0) Renderer.resetAccFrames();
        }
        quadShader.setVec3("u_camera_position", scene.getCamera().getPos());
        quadShader.setMat4("view", scene.getCamera().getView());
        if (Editor.status) {
            if (Viewport.getWidthDelta() != 0 || Viewport.getHeightDelta() != 0) resetAccFrames();
            quadShader.setVec2("u_resolution", new Vector2f(Viewport.getWidth(), Viewport.getHeight()));
        } else {
            quadShader.setVec2("u_resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
        }
        quadShader.setFloat("u_time", (float) glfwGetTime());
        quadShader.setMat4("proj", proj);
        quadShader.setFloat("u_acc_frames", accFrames);
        quadShader.setInt("u_show_albedo", showAlbedo ? 1 : 0);
        quadShader.setInt("u_show_normals", showNormals ? 1 : 0);
        quadShader.setInt("u_show_depth", showDepth ? 1 : 0);
        quadShader.setInt("u_samples", samples);
        quadShader.setInt("u_bounces", bounces);
        quadShader.setInt("u_random_noise", randNoise ? 1 : 0);
        quadShader.setInt("u_reproj", reproj ? 1 : 0);
        quadShader.setInt("u_aa_size", AASize);
        quadShader.setFloat("u_gamma", gamma);
        quadShader.setInt("u_gamma_correction", gammaCorrection ? 1 : 0);
        quadShader.setInt("u_aces", ACESFilm ? 1 : 0);

        quadShader.setInt("sky_has_texture", scene.getSky().hasTexture ? 1 : 0);
        if (scene.getSky().hasTexture) {
            quadShader.setInt("sky_texture", 1);
        } else {
            quadShader.setVec3("sky.color", scene.getSky().getColor());
        }
        quadShader.setFloat("sky.material.emission", scene.getSky().getMaterial().getEmission());
        quadShader.setFloat("sky.material.roughness", scene.getSky().getMaterial().getRoughness());
        quadShader.setFloat("sky.material.isMetal", scene.getSky().getMaterial().isMetal() ? 1 : 0);
        // Plane
        {
            quadShader.setVec3("u_plane_color", scene.getPlane().getColor());
            quadShader.setFloat("u_plane_emission", scene.getPlane().getMaterial().getEmission());
            quadShader.setFloat("u_plane_roughness", scene.getPlane().getMaterial().getRoughness());
            quadShader.setFloat("u_plane_is_dielectric", scene.getPlane().getMaterial().isMetal() ? 1 : 0);
            quadShader.setInt("u_plane_checkerboard", scene.getPlane().isCheckerBoard() ? 1 : 0);
        }
        // Spheres
        quadShader.setInt("u_spheres_count", scene.getSpheres().size());
        for (int i = 0; i < scene.getSpheres().size(); i++) {
            quadShader.setVec3("spheres[" + i + "].position", scene.getSpheres().get(i).getPos());
            quadShader.setFloat("spheres[" + i + "].radius", scene.getSpheres().get(i).getRadius());
            quadShader.setVec3("spheres[" + i + "].color", scene.getSpheres().get(i).getColor());
            quadShader.setFloat("spheres[" + i + "].material.emission", scene.getSpheres().get(i).getMaterial().getEmission());
            quadShader.setFloat("spheres[" + i + "].material.roughness", scene.getSpheres().get(i).getMaterial().getRoughness());
            quadShader.setFloat("spheres[" + i + "].material.isMetal", scene.getSpheres().get(i).getMaterial().isMetal() ? 1 : 0);
        }
        // Boxes
        quadShader.setInt("u_boxes_count", scene.getBoxes().size());
        for (int i = 0; i < scene.getBoxes().size(); i++) {
            quadShader.setVec3("boxes[" + i + "].position", scene.getBoxes().get(i).getPos());
            quadShader.setVec3("boxes[" + i + "].rotation", scene.getBoxes().get(i).getRot());
            quadShader.setVec3("boxes[" + i + "].size", scene.getBoxes().get(i).getSize());
            quadShader.setVec3("boxes[" + i + "].color", scene.getBoxes().get(i).getColor());
            quadShader.setFloat("boxes[" + i + "].material.emission", scene.getBoxes().get(i).getMaterial().getEmission());
            quadShader.setFloat("boxes[" + i + "].material.roughness", scene.getBoxes().get(i).getMaterial().getRoughness());
            quadShader.setFloat("boxes[" + i + "].material.isMetal", scene.getBoxes().get(i).getMaterial().isMetal() ? 1 : 0);
        }
        if (accumulation || reproj) glBindImageTexture(0, accTexture, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        if (accFrames == 0 && !reproj) resetFramebufferTexture();
        if (!Editor.status) glViewport(0, 0, Window.getWidth(), Window.getHeight());
        Viewport.bindFramebuffer();
        glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0);
        Viewport.unbindFramebuffer();
        if (accumulation) accFrames++;
    }

    public static void setScene(Scene scene) {
        Renderer.scene = scene;
    }

    public static Scene getScene() {
        return scene;
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

    public static void useGammaCorrection(boolean value, float gamma) {
        gammaCorrection = value;
        Renderer.gamma = gamma;
    }

    public static void useACESFilm(boolean value) {
        ACESFilm = value;
    }

    public static void setAASize(int aaSize) {
        AASize = aaSize;
    }

    public static void showAlbedo(boolean value) {
        showAlbedo = value;
    }

    public static void showNormals(boolean value) {
        showNormals = value;
    }

    public static void showDepth(boolean value) {
        showDepth = value;
    }
}