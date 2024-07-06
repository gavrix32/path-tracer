package net.gavrix32.engine.graphics;

import net.gavrix32.engine.io.Input;
import net.gavrix32.engine.io.Key;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.math.Vector2f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46C.*;

public class Renderer {
    private static Scene scene;
    private static Shader pt_shader, show_shader;
    private static int frames = 0;
    private static int samples = 1, bounces = 3;
    private static boolean
            accumulation = true, frameMixing = true, randNoise = false, gammaCorrection = true, tonemapping = true,
            taa = false, dof = false, autofocus = true, showAlbedo = false, showNormals = false, showDepth = false;
    private static int accTexture, frameBuffer, frameTexture;
    private static float mixFactor = 0.8f, gamma = 2.2f, exposure = 1.0f, focusDistance = 50.0f, defocusBlur = 3.0f;
    private static Accelerator accelerator;
    private static int verticesBuffer, verticesTexture;

    public static void init() {
        Quad.init();

        pt_shader = new Shader("shaders/main.vert", "shaders/main.frag");
        show_shader = new Shader("shaders/main.vert", "shaders/show.frag");

        scene = new Scene();

        // Frame buffer
        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        frameTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, frameTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, 1920, 1080, 0, GL_RGB, GL_FLOAT, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glBindTexture(GL_TEXTURE_2D, 0);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, frameTexture, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        if (Input.isKeyDown(Key.U))
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        pt_shader.use();
        pt_shader.setMat4("prev_camera_rotation_matrix", scene.camera.getRotationMatrix());
        pt_shader.setVec3("prev_camera_position", scene.camera.getPosition());
        scene.camera.update();
        pt_shader.setMat4("camera_rotation_matrix", scene.camera.getRotationMatrix());
        pt_shader.setVec3("camera_position", scene.camera.getPosition());
        pt_shader.setVec2("resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
        pt_shader.setFloat("time", (float) glfwGetTime());
        pt_shader.setFloat("acc_frames", frames);
        pt_shader.setBool("show_albedo", showAlbedo);
        pt_shader.setBool("show_normals", showNormals);
        pt_shader.setBool("show_depth", showDepth);
        pt_shader.setInt("samples", samples);
        pt_shader.setInt("bounces", bounces);
        pt_shader.setFloat("fov", scene.camera.getFov());
        pt_shader.setBool("use_random_noise", randNoise);
        pt_shader.setBool("use_reproj", frameMixing);
        pt_shader.setBool("use_taa", taa);
        pt_shader.setBool("use_dof", dof);
        pt_shader.setBool("use_autofocus", autofocus);
        pt_shader.setFloat("focus_distance", focusDistance);
        pt_shader.setFloat("defocus_blur", defocusBlur);
        pt_shader.setFloat("mix_factor", mixFactor);
        pt_shader.setFloat("gamma", gamma);
        pt_shader.setBool("use_gamma_correction", gammaCorrection);
        pt_shader.setBool("use_tonemapping", tonemapping);
        pt_shader.setFloat("exposure", exposure);
        pt_shader.setBool("sky_has_texture", scene.sky.hasTexture());
        pt_shader.setBool("use_accel", true);
        if (scene.sky.hasTexture()) {
            glActiveTexture(GL_TEXTURE1);
            scene.sky.bindTexture();
            pt_shader.setInt("sky_texture", 1);
        } else {
            pt_shader.setVec3("sky.material.color", scene.sky.getColor());
        }
        pt_shader.setBool("sky.material.is_metal", scene.sky.getMaterial().isMetal());
        pt_shader.setFloat("sky.material.emission", scene.sky.getMaterial().getEmission());
        pt_shader.setFloat("sky.material.roughness", scene.sky.getMaterial().getRoughness());
        pt_shader.setBool("sky.material.is_glass", scene.sky.getMaterial().isGlass());
        pt_shader.setFloat("sky.material.IOR", scene.sky.getMaterial().getIOR());
        // Plane
        if (scene.plane != null) {
            pt_shader.setInt("plane.exists", 1);
            pt_shader.setBool("plane.checkerboard", scene.plane.isCheckerBoard());
            if (scene.plane.isCheckerBoard()) {
                pt_shader.setVec3("plane.color1", scene.plane.getFirstColor());
                pt_shader.setVec3("plane.color2", scene.plane.getSecondColor());
                pt_shader.setFloat("plane.scale", scene.plane.getScale());
            } else {
                pt_shader.setVec3("plane.material.color", scene.plane.getColor());
            }
            pt_shader.setFloat("plane.material.emission", scene.plane.getMaterial().getEmission());
            pt_shader.setFloat("plane.material.roughness", scene.plane.getMaterial().getRoughness());
            pt_shader.setBool("plane.material.is_glass", scene.plane.getMaterial().isGlass());
            pt_shader.setFloat("plane.material.IOR", scene.plane.getMaterial().getIOR());
            pt_shader.setBool("plane.material.is_metal", scene.plane.getMaterial().isMetal());
        } else {
            pt_shader.setInt("plane.exists", 0);
        }
        accelerator = new Accelerator(scene);
        // Spheres
        pt_shader.setVec3("sphAABB.min", accelerator.getSpheresBoundingBox().min);
        pt_shader.setVec3("sphAABB.max", accelerator.getSpheresBoundingBox().max);
        pt_shader.setInt("spheres_count", scene.spheres.size());
        for (int i = 0; i < scene.spheres.size(); i++) {
            pt_shader.setVec3("spheres[" + i + "].position", scene.spheres.get(i).getPos());
            pt_shader.setFloat("spheres[" + i + "].radius", scene.spheres.get(i).getRadius());
            pt_shader.setVec3("spheres[" + i + "].material.color", scene.spheres.get(i).getColor());
            pt_shader.setBool("spheres[" + i + "].material.is_metal", scene.spheres.get(i).getMaterial().isMetal());
            pt_shader.setFloat("spheres[" + i + "].material.emission", scene.spheres.get(i).getMaterial().getEmission());
            pt_shader.setFloat("spheres[" + i + "].material.roughness", scene.spheres.get(i).getMaterial().getRoughness());
            pt_shader.setBool("spheres[" + i + "].material.is_glass", scene.spheres.get(i).getMaterial().isGlass());
            pt_shader.setFloat("spheres[" + i + "].material.IOR", scene.spheres.get(i).getMaterial().getIOR());
        }
        // Boxes
        pt_shader.setVec3("boxAABB.min", accelerator.getBoxesBoundingBox().min);
        pt_shader.setVec3("boxAABB.max", accelerator.getBoxesBoundingBox().max);
        pt_shader.setInt("boxes_count", scene.boxes.size());
        for (int i = 0; i < scene.boxes.size(); i++) {
            pt_shader.setVec3("boxes[" + i + "].position", scene.boxes.get(i).getPos());
            scene.boxes.get(i).getRotationMatrix().rotate(
                    scene.boxes.get(i).getRot().x,
                    scene.boxes.get(i).getRot().y,
                    scene.boxes.get(i).getRot().z
            );
            pt_shader.setMat4("boxes[" + i + "].rotation", scene.boxes.get(i).getRotationMatrix());
            pt_shader.setVec3("boxes[" + i + "].scale", scene.boxes.get(i).getScale());
            pt_shader.setVec3("boxes[" + i + "].material.color", scene.boxes.get(i).getColor());
            pt_shader.setBool("boxes[" + i + "].material.is_metal", scene.boxes.get(i).getMaterial().isMetal());
            pt_shader.setFloat("boxes[" + i + "].material.emission", scene.boxes.get(i).getMaterial().getEmission());
            pt_shader.setFloat("boxes[" + i + "].material.roughness", scene.boxes.get(i).getMaterial().getRoughness());
            pt_shader.setBool("boxes[" + i + "].material.is_glass", scene.boxes.get(i).getMaterial().isGlass());
            pt_shader.setFloat("boxes[" + i + "].material.IOR", scene.boxes.get(i).getMaterial().getIOR());
        }
        // Triangles
        /*pt_shader.setVec3("triAABB.min", accelerator.getTrianglesBoundingBox().min);
        pt_shader.setVec3("triAABB.max", accelerator.getTrianglesBoundingBox().max);

        if (OpenBox.getModel() != null) {
            verticesBuffer = glGenBuffers();
            glBindBuffer(GL_TEXTURE_BUFFER, verticesBuffer);
            glBufferData(GL_TEXTURE_BUFFER, OpenBox.getModel().getVerticesData(), GL_STATIC_DRAW);
            //glBufferData(GL_TEXTURE_BUFFER, OpenBox.getModel().getVerticesData(), GL_STATIC_DRAW);
        }

        verticesTexture = glGenTextures();
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_BUFFER, verticesTexture);
        glTexBuffer(GL_TEXTURE_BUFFER, GL_RGBA32F, verticesBuffer);
        pt_shader.setInt("verticesTexture", 2);

        // Triangles BVH data
        pt_shader.setInt("triangles_count", scene.triangles.size());
        for (int i = 0; i < scene.triangles.size(); i++) {
            pt_shader.setVec3("triangles[" + i + "].v1", scene.triangles.get(i).getV1());
            pt_shader.setVec3("triangles[" + i + "].v2", scene.triangles.get(i).getV2());
            pt_shader.setVec3("triangles[" + i + "].v3", scene.triangles.get(i).getV3());
            scene.triangles.get(i).getRotationMatrix().rotate(
                    scene.triangles.get(i).getRot().x,
                    scene.triangles.get(i).getRot().y,
                    scene.triangles.get(i).getRot().z
            );
            pt_shader.setMat4("triangles[" + i + "].rotation", scene.triangles.get(i).getRotationMatrix());
            pt_shader.setVec3("triangles[" + i + "].material.color", scene.triangles.get(i).getColor());
            pt_shader.setBool("triangles[" + i + "].material.is_metal", scene.triangles.get(i).getMaterial().isMetal());
            pt_shader.setFloat("triangles[" + i + "].material.emission", scene.triangles.get(i).getMaterial().getEmission());
            pt_shader.setFloat("triangles[" + i + "].material.roughness", scene.triangles.get(i).getMaterial().getRoughness());
            pt_shader.setBool("triangles[" + i + "].material.is_glass", scene.triangles.get(i).getMaterial().isGlass());
            pt_shader.setFloat("triangles[" + i + "].material.IOR", scene.triangles.get(i).getMaterial().getIOR());
        }*/

        //glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        //glBindTexture(GL_TEXTURE_2D, frameTexture);
        //glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, frameTexture, 0);
        //pt_shader.setInt("frame_tex", 2);
        //if (accumulation || frameMixing) glBindImageTexture(0, accTexture, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        //if (accFrames == 0 && !frameMixing) resetAccTexture();
        //glBindTexture(GL_TEXTURE_2D, frameTexture);
        // Update frame texture
        //glBindTexture(GL_TEXTURE_2D, frameTexture);
        //if (accumulation) accFrames++;

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, frameTexture);
        pt_shader.setInt("prev_frame", 2);

        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        Quad.draw();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, frameTexture);
        show_shader.use();
        show_shader.setInt("frame_texture", 0);
        show_shader.setVec2("resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
        Quad.draw();
        frames++;
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
        frames = 0;
    }

    public static int getFrames() {
        return frames;
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

    public static float getMixFactor() {
        return mixFactor;
    }

    public static void setMixFactor(float mixFactor) {
        Renderer.mixFactor = mixFactor;
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
        resetAccFrames();
        showAlbedo = value;
    }

    public static boolean isShowNormals() {
        return showNormals;
    }

    public static void showNormals(boolean value) {
        resetAccFrames();
        showNormals = value;
    }

    public static boolean isShowDepth() {
        resetAccFrames();
        return showDepth;
    }

    public static void showDepth(boolean value) {
        showDepth = value;
    }
}