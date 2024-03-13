package net.gavrix32.engine.graphics;

import net.gavrix32.engine.gui.Gui;
import net.gavrix32.engine.gui.Viewport;
import net.gavrix32.engine.io.Input;
import net.gavrix32.engine.io.Window;
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
    private static Shader rt_shader;
    private static int accFrames = 0;
    private static int samples = 1, bounces = 3;
    private static boolean
            accumulation = true, frameMixing = true, randNoise = false, gammaCorrection = true, tonemapping = true,
            taa = true, dof = false, autofocus = true, showAlbedo = false, showNormals = false, showDepth = false;
    private static int accTexture;
    private static float gamma = 2.2f, exposure = 1.0f, focusDistance = 50.0f, defocusBlur = 3.0f;

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

        rt_shader = new Shader("shaders/main.vert", "shaders/main.frag");
        rt_shader.use();

        scene = new Scene();
    }

    public static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        scene.getCamera().update();
        if (!Window.isCursorVisible()) {
            scene.getCamera().rotateX(Input.getDeltaY() * -0.12f);
            scene.getCamera().rotateY(Input.getDeltaX() * -0.12f);
            if (Input.getDeltaX() != 0 || Input.getDeltaY() != 0) Renderer.resetAccFrames();
        }
        rt_shader.setMat4("view", scene.getCamera().getView());
        rt_shader.setVec3("camera_position", scene.getCamera().getPosition());
        if (Gui.status) {
            if (Viewport.getWidthDelta() != 0 || Viewport.getHeightDelta() != 0) resetAccFrames();
            rt_shader.setVec2("resolution", new Vector2f(Viewport.getWidth(), Viewport.getHeight()));
        } else rt_shader.setVec2("resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
        rt_shader.setFloat("time", (float) glfwGetTime());
        rt_shader.setFloat("acc_frames", accFrames);
        rt_shader.setBool("show_albedo", showAlbedo);
        rt_shader.setBool("show_normals", showNormals);
        rt_shader.setBool("show_depth", showDepth);
        rt_shader.setInt("samples", samples);
        rt_shader.setInt("bounces", bounces);
        rt_shader.setFloat("fov", scene.getCamera().getFov());
        rt_shader.setBool("random_noise", randNoise);
        rt_shader.setBool("frame_mixing", frameMixing);
        rt_shader.setBool("taa", taa);
        rt_shader.setBool("dof", dof);
        rt_shader.setBool("autofocus", autofocus);
        rt_shader.setFloat("focus_distance", focusDistance);
        rt_shader.setFloat("defocus_blur", defocusBlur);
        rt_shader.setFloat("gamma", gamma);
        rt_shader.setBool("gamma_correction", gammaCorrection);
        rt_shader.setBool("tonemapping", tonemapping);
        rt_shader.setFloat("exposure", exposure);
        rt_shader.setBool("sky_has_texture", scene.getSky().hasTexture());
        if (scene.getSky().hasTexture()) {
            glActiveTexture(GL_TEXTURE0);
            scene.getSky().bindTexture();
            rt_shader.setInt("sky_texture", 0);
        } else rt_shader.setVec3("sky.material.color", scene.getSky().getColor());
        rt_shader.setBool("sky.material.is_metal", scene.getSky().getMaterial().isMetal());
        rt_shader.setFloat("sky.material.emission", scene.getSky().getMaterial().getEmission());
        rt_shader.setFloat("sky.material.roughness", scene.getSky().getMaterial().getRoughness());
        rt_shader.setBool("sky.material.is_glass", scene.getSky().getMaterial().isGlass());
        rt_shader.setFloat("sky.material.IOR", scene.getSky().getMaterial().getIOR());
        // Plane
        if (scene.getPlane() != null) {
            rt_shader.setInt("plane.exists", 1);
            rt_shader.setBool("plane.checkerboard", scene.getPlane().isCheckerBoard());
            if (scene.getPlane().isCheckerBoard()) {
                rt_shader.setVec3("plane.color1", scene.getPlane().getColor1());
                rt_shader.setVec3("plane.color2", scene.getPlane().getColor2());
            } else rt_shader.setVec3("plane.material.color", scene.getPlane().getColor());
            rt_shader.setFloat("plane.material.emission", scene.getPlane().getMaterial().getEmission());
            rt_shader.setFloat("plane.material.roughness", scene.getPlane().getMaterial().getRoughness());
            rt_shader.setBool("plane.material.is_glass", scene.getPlane().getMaterial().isGlass());
            rt_shader.setFloat("plane.material.IOR", scene.getPlane().getMaterial().getIOR());
            rt_shader.setBool("plane.material.is_metal", scene.getPlane().getMaterial().isMetal());
        } else rt_shader.setInt("plane.exists", 0);
        // Spheres
        rt_shader.setInt("spheres_count", scene.getSpheres().size());
        for (int i = 0; i < scene.getSpheres().size(); i++) {
            rt_shader.setVec3("spheres[" + i + "].position", scene.getSpheres().get(i).getPos());
            rt_shader.setFloat("spheres[" + i + "].radius", scene.getSpheres().get(i).getRadius());
            rt_shader.setVec3("spheres[" + i + "].material.color", scene.getSpheres().get(i).getColor());
            rt_shader.setBool("spheres[" + i + "].material.is_metal", scene.getSpheres().get(i).getMaterial().isMetal());
            rt_shader.setFloat("spheres[" + i + "].material.emission", scene.getSpheres().get(i).getMaterial().getEmission());
            rt_shader.setFloat("spheres[" + i + "].material.roughness", scene.getSpheres().get(i).getMaterial().getRoughness());
            rt_shader.setBool("spheres[" + i + "].material.is_glass", scene.getSpheres().get(i).getMaterial().isGlass());
            rt_shader.setFloat("spheres[" + i + "].material.IOR", scene.getSpheres().get(i).getMaterial().getIOR());
        }
        // Boxes
        rt_shader.setInt("boxes_count", scene.getBoxes().size());
        for (int i = 0; i < scene.getBoxes().size(); i++) {
            rt_shader.setVec3("boxes[" + i + "].position", scene.getBoxes().get(i).getPos());
            scene.getBoxes().get(i).getRotationMatrix().setRotationXYZ(
                    (float) Math.toRadians(scene.getBoxes().get(i).getRot().x),
                    (float) Math.toRadians(scene.getBoxes().get(i).getRot().y),
                    (float) Math.toRadians(scene.getBoxes().get(i).getRot().z)
            );
            rt_shader.setMat4("boxes[" + i + "].rotation", scene.getBoxes().get(i).getRotationMatrix());
            rt_shader.setVec3("boxes[" + i + "].scale", scene.getBoxes().get(i).getScale());
            rt_shader.setVec3("boxes[" + i + "].material.color", scene.getBoxes().get(i).getColor());
            rt_shader.setBool("boxes[" + i + "].material.is_metal", scene.getBoxes().get(i).getMaterial().isMetal());
            rt_shader.setFloat("boxes[" + i + "].material.emission", scene.getBoxes().get(i).getMaterial().getEmission());
            rt_shader.setFloat("boxes[" + i + "].material.roughness", scene.getBoxes().get(i).getMaterial().getRoughness());
            rt_shader.setBool("boxes[" + i + "].material.is_glass", scene.getBoxes().get(i).getMaterial().isGlass());
            rt_shader.setFloat("boxes[" + i + "].material.IOR", scene.getBoxes().get(i).getMaterial().getIOR());
        }
        if (accumulation || frameMixing) glBindImageTexture(0, accTexture, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        if (accFrames == 0 && !frameMixing) resetAccTexture();
        if (!Gui.status) glViewport(0, 0, Window.getWidth(), Window.getHeight());
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
        resetAccFrames();
        Renderer.samples = samples;
    }

    public static int getBounces() {
        return bounces;
    }

    public static void setBounces(int bounces) {
        resetAccFrames();
        Renderer.bounces = bounces;
    }

    public static void setAccumulation(boolean value) {
        if (!value) resetAccFrames();
        accumulation = value;
    }

    public static boolean isAccumulation() {
        return accumulation;
    }

    public static void setFrameMixing(boolean value) {
        frameMixing = value;
    }

    public static boolean isFrameMixing() {
        return frameMixing;
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

    public static void setRandomNoise(boolean value) {
        randNoise = value;
    }

    public static boolean isRandNoise() {
        return randNoise;
    }

    public static float getGamma() {
        return gamma;
    }

    public static boolean isGammaCorrection() {
        return gammaCorrection;
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

    public static boolean isTonemapping() {
        return tonemapping;
    }

    public static void setToneMapping(boolean value, float exposure) {
        tonemapping = value;
        Renderer.exposure = exposure;
    }

    public static boolean isTaa() {
        return taa;
    }

    public static void useTaa(boolean value) {
        resetAccFrames();
        taa = value;
    }

    public static boolean isDof() {
        return dof;
    }

    public static void setDof(boolean value) {
        resetAccFrames();
        dof = value;
    }

    public static float getFocusDistance() {
        return focusDistance;
    }

    public static void setFocusDistance(float focusDistance) {
        resetAccFrames();
        Renderer.focusDistance = focusDistance;
    }

    public static float getDefocusBlur() {
        return defocusBlur;
    }

    public static void setDefocusBlur(float defocusBlur) {
        resetAccFrames();
        Renderer.defocusBlur = defocusBlur;
    }

    public static boolean isAutofocus() {
        return autofocus;
    }

    public static void setAutofocus(boolean value) {
        resetAccFrames();
        autofocus = value;
    }

    public static boolean isShowAlbedo() {
        return showAlbedo;
    }

    public static void showAlbedo(boolean value) {
        showAlbedo = value;
    }

    public static boolean isShowNormals() {
        return showNormals;
    }

    public static void showNormals(boolean value) {
        showNormals = value;
    }

    public static boolean isShowDepth() {
        return showDepth;
    }

    public static void showDepth(boolean value) {
        showDepth = value;
    }
}