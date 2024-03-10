package net.gavrix32.engine.graphics;

import net.gavrix32.engine.gui.GUI;
import net.gavrix32.engine.gui.Viewport;
import net.gavrix32.engine.io.Input;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.objects.Camera;
import net.gavrix32.engine.utils.Logger;
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
    private static int samples = 1, bounces = 3, fov = 70;
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
        quadShader.setBool("show_albedo", showAlbedo);
        quadShader.setBool("show_normals", showNormals);
        quadShader.setBool("show_depth", showDepth);
        quadShader.setInt("samples", samples);
        quadShader.setInt("bounces", bounces);
        quadShader.setInt("fov", fov);
        quadShader.setBool("random_noise", randNoise);
        quadShader.setBool("frame_mixing", frameMixing);
        quadShader.setBool("taa", taa);
        quadShader.setFloat("gamma", gamma);
        quadShader.setBool("gamma_correction", gammaCorrection);
        quadShader.setBool("tonemapping", tonemapping);
        quadShader.setFloat("exposure", exposure);
        quadShader.setBool("sky_has_texture", scene.getSky().hasTexture());
        if (scene.getSky().hasTexture()) {
            scene.getSky().bindTexture();
            quadShader.setInt("sky_texture", 0);

        } else {
            quadShader.setVec3("sky.material.color", scene.getSky().getColor());
        }
        quadShader.setBool("sky.material.is_metal", scene.getSky().getMaterial().isMetal());
        quadShader.setFloat("sky.material.emission", scene.getSky().getMaterial().getEmission());
        quadShader.setFloat("sky.material.roughness", scene.getSky().getMaterial().getRoughness());
        quadShader.setBool("sky.material.is_glass", scene.getSky().getMaterial().isGlass());
        quadShader.setFloat("sky.material.IOR", scene.getSky().getMaterial().getIOR());
        // Plane
        if (scene.getPlane() != null) {
            quadShader.setInt("plane.exists", 1);
            quadShader.setBool("plane.checkerboard", scene.getPlane().isCheckerBoard());
            if (scene.getPlane().isCheckerBoard()) {
                quadShader.setVec3("plane.color1", scene.getPlane().getColor1());
                quadShader.setVec3("plane.color2", scene.getPlane().getColor2());
            } else {
                quadShader.setVec3("plane.material.color", scene.getPlane().getColor());
            }
            quadShader.setFloat("plane.material.emission", scene.getPlane().getMaterial().getEmission());
            quadShader.setFloat("plane.material.roughness", scene.getPlane().getMaterial().getRoughness());
            quadShader.setBool("plane.material.is_glass", scene.getPlane().getMaterial().isGlass());
            quadShader.setFloat("plane.material.IOR", scene.getPlane().getMaterial().getIOR());
            quadShader.setBool("plane.material.is_metal", scene.getPlane().getMaterial().isMetal());
        } else {
            quadShader.setInt("plane.exists", 0);
        }
        // Spheres
        quadShader.setInt("spheres_count", scene.getSpheres().size());
        for (int i = 0; i < scene.getSpheres().size(); i++) {
            quadShader.setVec3("spheres[" + i + "].position", scene.getSpheres().get(i).getPos());
            quadShader.setFloat("spheres[" + i + "].radius", scene.getSpheres().get(i).getRadius());
            quadShader.setVec3("spheres[" + i + "].material.color", scene.getSpheres().get(i).getColor());
            quadShader.setBool("spheres[" + i + "].material.is_metal", scene.getSpheres().get(i).getMaterial().isMetal());
            quadShader.setFloat("spheres[" + i + "].material.emission", scene.getSpheres().get(i).getMaterial().getEmission());
            quadShader.setFloat("spheres[" + i + "].material.roughness", scene.getSpheres().get(i).getMaterial().getRoughness());
            quadShader.setBool("spheres[" + i + "].material.is_glass", scene.getSpheres().get(i).getMaterial().isGlass());
            quadShader.setFloat("spheres[" + i + "].material.IOR", scene.getSpheres().get(i).getMaterial().getIOR());
        }
        // Boxes
        quadShader.setInt("boxes_count", scene.getBoxes().size());
        for (int i = 0; i < scene.getBoxes().size(); i++) {
            quadShader.setVec3("boxes[" + i + "].position", scene.getBoxes().get(i).getPos());
            scene.getBoxes().get(i).getRotationMatrix().setRotationXYZ(
                    (float) Math.toRadians(scene.getBoxes().get(i).getRot().x),
                    (float) Math.toRadians(scene.getBoxes().get(i).getRot().y),
                    (float) Math.toRadians(scene.getBoxes().get(i).getRot().z)
            );
            quadShader.setMat4("boxes[" + i + "].rotation", scene.getBoxes().get(i).getRotationMatrix());
            quadShader.setVec3("boxes[" + i + "].scale", scene.getBoxes().get(i).getScale());
            quadShader.setVec3("boxes[" + i + "].material.color", scene.getBoxes().get(i).getColor());
            quadShader.setBool("boxes[" + i + "].material.is_metal", scene.getBoxes().get(i).getMaterial().isMetal());
            quadShader.setFloat("boxes[" + i + "].material.emission", scene.getBoxes().get(i).getMaterial().getEmission());
            quadShader.setFloat("boxes[" + i + "].material.roughness", scene.getBoxes().get(i).getMaterial().getRoughness());
            quadShader.setBool("boxes[" + i + "].material.is_glass", scene.getBoxes().get(i).getMaterial().isGlass());
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

    public static int getSamples() {
        return samples;
    }

    public static void setSamples(int samples) {
        Renderer.samples = samples;
    }

    public static int getBounces() {
        return bounces;
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

    public static float getGamma() {
        return gamma;
    }

    public static void setGamma(float gamma) {
        Renderer.gamma = gamma;
    }

    public static void useGammaCorrection(boolean value, float gamma) {
        gammaCorrection = value;
        Renderer.gamma = gamma;
    }

    public static float getExposure() {
        return exposure;
    }

    public static void setExposure(float exposure) {
        Renderer.exposure = exposure;
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