package net.gavrix32.engine.graphics;

import net.gavrix32.engine.gui.GUI;
import net.gavrix32.engine.gui.Viewport;
import net.gavrix32.engine.io.Input;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.utils.Logger;
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
    private static int samples = 1, bounces = 3, fov = 90;
    private static boolean
            accumulation = true, frameMixing = true, randNoise = false, gammaCorrection = true, tonemapping = false,
            taa = true, showAlbedo = false, showNormals = false, showDepth = false;
    private static int accTexture;
    private static float gamma = 2.2f, exposure = 1.0f;

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
    }

    public static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        scene.getCamera().update();
        if (!Window.isCursorVisible()) {
            scene.getCamera().rotateX(Input.getDeltaY() * -0.003f);
            scene.getCamera().rotateY(Input.getDeltaX() * -0.003f);
            if (Input.getDeltaX() != 0 || Input.getDeltaY() != 0) Renderer.resetAccFrames();
        }
        quadShader.setMat4("view", scene.getCamera().getView());
        quadShader.setVec3("camera_position", scene.getCamera().getPos());
        if (GUI.status) {
            if (Viewport.getWidthDelta() != 0 || Viewport.getHeightDelta() != 0) resetAccFrames();
            quadShader.setVec2("resolution", new Vector2f(Viewport.getWidth(), Viewport.getHeight()));
        } else {
            quadShader.setVec2("resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
        }
        quadShader.setFloat("time", (float) glfwGetTime());
        quadShader.setFloat("acc_frames", accFrames);
        quadShader.setInt("show_albedo", showAlbedo ? 1 : 0);
        quadShader.setInt("show_normals", showNormals ? 1 : 0);
        quadShader.setInt("show_depth", showDepth ? 1 : 0);
        quadShader.setInt("samples", samples);
        quadShader.setInt("bounces", bounces);
        quadShader.setInt("fov", fov);
        quadShader.setInt("random_noise", randNoise ? 1 : 0);
        quadShader.setInt("frame_mixing", frameMixing ? 1 : 0);
        quadShader.setInt("taa", taa ? 1 : 0);
        quadShader.setFloat("gamma", gamma);
        quadShader.setInt("gamma_correction", gammaCorrection ? 1 : 0);
        quadShader.setInt("tonemapping", tonemapping ? 1 : 0);
        quadShader.setFloat("exposure", exposure);
        quadShader.setInt("sky_has_texture", scene.getSky().hasTexture() ? 1 : 0);
        if (scene.getSky().hasTexture()) {
            scene.getSky().bindTexture();
            quadShader.setInt("sky_texture", 0);

        } else {
            quadShader.setVec3("sky.material.color", scene.getSky().getColor());
        }
        quadShader.setFloat("sky.material.is_metal", scene.getSky().getMaterial().isMetal() ? 1 : 0);
        quadShader.setFloat("sky.material.emission", scene.getSky().getMaterial().getEmission());
        quadShader.setFloat("sky.material.roughness", scene.getSky().getMaterial().getRoughness());
        quadShader.setFloat("sky.material.IOR", scene.getSky().getMaterial().getIOR());
        // Plane
        if (scene.getPlane() != null) {
            quadShader.setInt("plane.exists", 1);
            quadShader.setInt("plane.checkerboard", scene.getPlane().isCheckerBoard() ? 1 : 0);
            if (scene.getPlane().isCheckerBoard()) {
                quadShader.setVec3("plane.color1", scene.getPlane().getColor1());
                quadShader.setVec3("plane.color2", scene.getPlane().getColor2());
            } else {
                quadShader.setVec3("plane.material.color", scene.getPlane().getColor());
            }
            quadShader.setFloat("plane.material.emission", scene.getPlane().getMaterial().getEmission());
            quadShader.setFloat("plane.material.roughness", scene.getPlane().getMaterial().getRoughness());
            quadShader.setFloat("plane.material.IOR", scene.getPlane().getMaterial().getIOR());
            quadShader.setFloat("plane.material.is_metal", scene.getPlane().getMaterial().isMetal() ? 1 : 0);
        } else {
            quadShader.setInt("plane.exists", 0);
        }
        // Spheres
        quadShader.setInt("spheres_count", scene.getSpheres().size());
        for (int i = 0; i < scene.getSpheres().size(); i++) {
            quadShader.setVec3("spheres[" + i + "].position", scene.getSpheres().get(i).getPos());
            quadShader.setFloat("spheres[" + i + "].radius", scene.getSpheres().get(i).getRadius());
            quadShader.setVec3("spheres[" + i + "].material.color", scene.getSpheres().get(i).getColor());
            quadShader.setFloat("spheres[" + i + "].material.is_metal", scene.getSpheres().get(i).getMaterial().isMetal() ? 1 : 0);
            quadShader.setFloat("spheres[" + i + "].material.emission", scene.getSpheres().get(i).getMaterial().getEmission());
            quadShader.setFloat("spheres[" + i + "].material.roughness", scene.getSpheres().get(i).getMaterial().getRoughness());
            quadShader.setFloat("spheres[" + i + "].material.IOR", scene.getSpheres().get(i).getMaterial().getIOR());
        }
        // Boxes
        quadShader.setInt("boxes_count", scene.getBoxes().size());
        for (int i = 0; i < scene.getBoxes().size(); i++) {
            quadShader.setVec3("boxes[" + i + "].position", scene.getBoxes().get(i).getPos());
            quadShader.setVec3("boxes[" + i + "].rotation", scene.getBoxes().get(i).getRot());
            quadShader.setVec3("boxes[" + i + "].scale", scene.getBoxes().get(i).getScale());
            quadShader.setVec3("boxes[" + i + "].material.color", scene.getBoxes().get(i).getColor());
            quadShader.setFloat("boxes[" + i + "].material.is_metal", scene.getBoxes().get(i).getMaterial().isMetal() ? 1 : 0);
            quadShader.setFloat("boxes[" + i + "].material.emission", scene.getBoxes().get(i).getMaterial().getEmission());
            quadShader.setFloat("boxes[" + i + "].material.roughness", scene.getBoxes().get(i).getMaterial().getRoughness());
            quadShader.setFloat("boxes[" + i + "].material.IOR", scene.getBoxes().get(i).getMaterial().getIOR());
        }
        if (accumulation || frameMixing) glBindImageTexture(0, accTexture, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        if (accFrames == 0 && !frameMixing) resetAccTexture();
        if (!GUI.status) glViewport(0, 0, Window.getWidth(), Window.getHeight());
        Viewport.bindFramebuffer();
        scene.getSky().bindTexture();
        glDrawElements(GL_TRIANGLES, INDICES.length, GL_UNSIGNED_INT, 0);
        scene.getSky().unbindTexture();
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

    public static int getFOV() {
        return Renderer.fov;
    }

    public static void setFOV(int fov) {
        Renderer.fov = fov;
    }

    public static void useAccumulation(boolean value) {
        if (!value) resetAccFrames();
        accumulation = value;
    }

    public static void useFrameMixing(boolean value) {
        frameMixing = value;
    }

    public static void resetAccFrames() {
        accFrames = 0;
    }

    public static int getAccFrames() {
        return accFrames;
    }

    public static void resetAccTexture() {
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

    public static void useToneMapping(boolean value, float exposure) {
        tonemapping = value;
        Renderer.exposure = exposure;
    }

    public static void useTAA(boolean value) {
        taa = value;
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