package net.gavrix32.engine.graphics;

import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.math.Vector2f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46C.*;

public class Renderer {
    private static Scene scene;
    private static Shader pathtraceShader, atrousShader, presentShader;
    private static int frames = 0, currAcc = 0, prevAcc = 1, currAtrous = 0, prevAtrous = 1, sampler, normalImage, positionImage, albedoImage;
    private static final int[] accFramebuffer = new int[2], accTexture = new int[2];
    private static final int[] atrousFramebuffer = new int[2], atrousTexture = new int[2];
    // private static int verticesBuffer, verticesTexture;

    private static int samples, bounces;
    private static float gamma, exposure, focusDistance, aperture, fov;
    private static boolean accumulation, temporalReprojection, temporalAntialiasing, atrousFilter;

    // TODO: scene in texture
    // TODO: isKeyUp method
    // TODO: try inverse view matrix on CPU

    public static void init() {
        samples = Config.getInt("samples");
        bounces = Config.getInt("bounces");
        gamma = Config.getFloat("gamma");
        exposure = Config.getFloat("exposure");
        focusDistance = Config.getFloat("focus_distance");
        aperture = Config.getFloat("aperture");
        fov = Config.getFloat("fov");
        accumulation = Config.getBoolean("accumulation");
        temporalReprojection = Config.getBoolean("temporal_reprojection");
        temporalAntialiasing = Config.getBoolean("temporal_antialiasing");
        atrousFilter = Config.getBoolean("atrous_filter");

        Quad.init();

        pathtraceShader = new Shader("shaders/quad.vert", "shaders/pathtrace.frag");
        atrousShader = new Shader("shaders/quad.vert", "shaders/atrous.frag");
        presentShader = new Shader("shaders/quad.vert", "shaders/present.frag");

        scene = new Scene();

        for (int i = 0; i < 2; i++) {
            accFramebuffer[i] = glGenFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, accFramebuffer[i]);

            accTexture[i] = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, accTexture[i]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glBindTexture(GL_TEXTURE_2D, 0);

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, accTexture[i], 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        for (int i = 0; i < 2; i++) {
            atrousFramebuffer[i] = glGenFramebuffers();
            glBindFramebuffer(GL_FRAMEBUFFER, atrousFramebuffer[i]);

            atrousTexture[i] = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, atrousTexture[i]);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glBindTexture(GL_TEXTURE_2D, 0);

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, atrousTexture[i], 0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }

        normalImage = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, normalImage);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindImageTexture(0, normalImage, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        glBindTexture(GL_TEXTURE_2D, 0);

        positionImage = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, positionImage);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindImageTexture(1, positionImage, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        glBindTexture(GL_TEXTURE_2D, 0);

        albedoImage = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, albedoImage);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindImageTexture(2, albedoImage, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        glBindTexture(GL_TEXTURE_2D, 0);

        sampler = glGenSamplers();
        glSamplerParameteri(sampler, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glSamplerParameteri(sampler, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }

    public static void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        pathtraceShader.use();
        pathtraceShader.setMat4("prev_camera_rotation", scene.camera.getRotationMatrix());
        pathtraceShader.setVec3("prev_camera_position", scene.camera.getPosition());
        scene.camera.update();
        pathtraceShader.setMat4("camera_rotation", scene.camera.getRotationMatrix());
        pathtraceShader.setVec3("camera_position", scene.camera.getPosition());
        pathtraceShader.setVec2("resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
        pathtraceShader.setFloat("time", (float) glfwGetTime());
        pathtraceShader.setInt("frames", frames);
        pathtraceShader.setInt("samples", samples);
        pathtraceShader.setInt("bounces", bounces);
        pathtraceShader.setFloat("fov", fov);
        pathtraceShader.setBool("temporal_reprojection", temporalReprojection);
        pathtraceShader.setBool("temporal_antialiasing", temporalAntialiasing);
        pathtraceShader.setFloat("focus_distance", focusDistance);
        pathtraceShader.setFloat("aperture", aperture);
        pathtraceShader.setBool("sky_has_texture", scene.sky.hasTexture());
        pathtraceShader.setBool("use_accel", true);
        if (scene.sky.hasTexture()) {
            glActiveTexture(GL_TEXTURE1);
            scene.sky.bindTexture();
            pathtraceShader.setInt("sky_texture", 1);
        } else {
            pathtraceShader.setVec3("sky.material.color", scene.sky.getColor());
        }
        pathtraceShader.setBool("sky.material.is_metal", scene.sky.getMaterial().isMetal());
        pathtraceShader.setFloat("sky.material.emission", scene.sky.getMaterial().getEmission());
        pathtraceShader.setFloat("sky.material.roughness", scene.sky.getMaterial().getRoughness());
        pathtraceShader.setBool("sky.material.is_glass", scene.sky.getMaterial().isGlass());
        pathtraceShader.setFloat("sky.material.IOR", scene.sky.getMaterial().getIOR());
        // Plane
        if (scene.plane != null) {
            pathtraceShader.setInt("plane.exists", 1);
            pathtraceShader.setBool("plane.checkerboard", scene.plane.isCheckerBoard());
            if (scene.plane.isCheckerBoard()) {
                pathtraceShader.setVec3("plane.color1", scene.plane.getFirstColor());
                pathtraceShader.setVec3("plane.color2", scene.plane.getSecondColor());
                pathtraceShader.setFloat("plane.scale", scene.plane.getScale());
            } else {
                pathtraceShader.setVec3("plane.material.color", scene.plane.getColor());
            }
            pathtraceShader.setFloat("plane.material.emission", scene.plane.getMaterial().getEmission());
            pathtraceShader.setFloat("plane.material.roughness", scene.plane.getMaterial().getRoughness());
            pathtraceShader.setBool("plane.material.is_glass", scene.plane.getMaterial().isGlass());
            pathtraceShader.setFloat("plane.material.IOR", scene.plane.getMaterial().getIOR());
            pathtraceShader.setBool("plane.material.is_metal", scene.plane.getMaterial().isMetal());
        } else {
            pathtraceShader.setInt("plane.exists", 0);
        }
        Accelerator accelerator = new Accelerator(scene);
        // Spheres
        pathtraceShader.setVec3("sphAABB.min", accelerator.getSpheresBoundingBox().min);
        pathtraceShader.setVec3("sphAABB.max", accelerator.getSpheresBoundingBox().max);
        pathtraceShader.setInt("spheres_count", scene.spheres.size());
        for (int i = 0; i < scene.spheres.size(); i++) {
            pathtraceShader.setVec3("spheres[" + i + "].position", scene.spheres.get(i).getPos());
            pathtraceShader.setFloat("spheres[" + i + "].radius", scene.spheres.get(i).getRadius());
            pathtraceShader.setVec3("spheres[" + i + "].material.color", scene.spheres.get(i).getColor());
            pathtraceShader.setBool("spheres[" + i + "].material.is_metal", scene.spheres.get(i).getMaterial().isMetal());
            pathtraceShader.setFloat("spheres[" + i + "].material.emission", scene.spheres.get(i).getMaterial().getEmission());
            pathtraceShader.setFloat("spheres[" + i + "].material.roughness", scene.spheres.get(i).getMaterial().getRoughness());
            pathtraceShader.setBool("spheres[" + i + "].material.is_glass", scene.spheres.get(i).getMaterial().isGlass());
            pathtraceShader.setFloat("spheres[" + i + "].material.IOR", scene.spheres.get(i).getMaterial().getIOR());
        }
        // Boxes
        pathtraceShader.setVec3("boxAABB.min", accelerator.getBoxesBoundingBox().min);
        pathtraceShader.setVec3("boxAABB.max", accelerator.getBoxesBoundingBox().max);
        pathtraceShader.setInt("boxes_count", scene.boxes.size());
        for (int i = 0; i < scene.boxes.size(); i++) {
            pathtraceShader.setVec3("boxes[" + i + "].position", scene.boxes.get(i).getPos());
            scene.boxes.get(i).getRotationMatrix().rotate(
                    scene.boxes.get(i).getRot().x,
                    scene.boxes.get(i).getRot().y,
                    scene.boxes.get(i).getRot().z
            );
            pathtraceShader.setMat4("boxes[" + i + "].rotation", scene.boxes.get(i).getRotationMatrix());
            pathtraceShader.setVec3("boxes[" + i + "].scale", scene.boxes.get(i).getScale());
            pathtraceShader.setVec3("boxes[" + i + "].material.color", scene.boxes.get(i).getColor());
            pathtraceShader.setBool("boxes[" + i + "].material.is_metal", scene.boxes.get(i).getMaterial().isMetal());
            pathtraceShader.setFloat("boxes[" + i + "].material.emission", scene.boxes.get(i).getMaterial().getEmission());
            pathtraceShader.setFloat("boxes[" + i + "].material.roughness", scene.boxes.get(i).getMaterial().getRoughness());
            pathtraceShader.setBool("boxes[" + i + "].material.is_glass", scene.boxes.get(i).getMaterial().isGlass());
            pathtraceShader.setFloat("boxes[" + i + "].material.IOR", scene.boxes.get(i).getMaterial().getIOR());
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

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, accTexture[prevAcc]);
        pathtraceShader.setInt("prev_frame", 2);

        glBindFramebuffer(GL_FRAMEBUFFER, accFramebuffer[currAcc]);
        Quad.draw();

        if (atrousFilter) {
            for (int i = 0; i < Gui.iterations[0]; i++) {
                glBindFramebuffer(GL_FRAMEBUFFER, atrousFramebuffer[currAtrous]);
                atrousShader.use();
                glActiveTexture(GL_TEXTURE3);
                glBindTexture(GL_TEXTURE_2D, i == 0 ? accTexture[currAcc] : atrousTexture[prevAtrous]);
                atrousShader.setInt("color_texture", 3);
                glActiveTexture(GL_TEXTURE4);
                glBindTexture(GL_TEXTURE_2D, normalImage);
                glBindSampler(4, sampler);
                atrousShader.setInt("normal_texture", 4);
                glActiveTexture(GL_TEXTURE5);
                glBindTexture(GL_TEXTURE_2D, positionImage);
                glBindSampler(5, sampler);
                atrousShader.setInt("position_texture", 5);
                glActiveTexture(GL_TEXTURE6);
                glBindTexture(GL_TEXTURE_2D, albedoImage);
                glBindSampler(6, sampler);
                atrousShader.setInt("albedo_texture", 6);
                atrousShader.setVec2("resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
                atrousShader.setFloat("stepWidth", (1 << (i + 1)) - 1 * Gui.stepWidth[0]);
                atrousShader.setFloat("c_phi", 1.0f / i * Gui.c_phi[0]);
                atrousShader.setFloat("n_phi", 1.0f / (1 << i) * Gui.n_phi[0]);
                atrousShader.setFloat("p_phi", 1.0f / (1 << i) * Gui.p_phi[0]);
                Quad.draw();
                swapAtrousFrames();
            }
        }
        presentShader.use();
        presentShader.setVec2("resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
        presentShader.setFloat("gamma", gamma);
        presentShader.setFloat("exposure", exposure);
        glActiveTexture(GL_TEXTURE7);
        if (atrousFilter) glBindTexture(GL_TEXTURE_2D, atrousTexture[currAtrous]);
        else glBindTexture(GL_TEXTURE_2D, accTexture[currAcc]);
        presentShader.setInt("color_texture", 7);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        Quad.draw();
        swapAccFrames();
        if (accumulation) frames++;
    }

    public static void resetAccFrames() {
        frames = 0;
    }

    private static void swapAccFrames() {
        currAcc = 1 - currAcc;
        prevAcc = 1 - prevAcc;
    }

    private static void swapAtrousFrames() {
        currAtrous = 1 - currAtrous;
        prevAtrous = 1 - prevAtrous;
    }

    public static void resetFramebufferTextures() {
        glBindTexture(GL_TEXTURE_2D, accTexture[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, accTexture[1]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, normalImage);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, positionImage);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, albedoImage);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, atrousTexture[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, atrousTexture[1]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /* Getters and Setters */

    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        Renderer.scene = scene;
    }

    public static int getSamples() {
        return samples;
    }

    public static void setSamples(int value) {
        samples = value;
        Config.setInt("samples", value);
    }

    public static int getBounces() {
        return bounces;
    }

    public static void setBounces(int value) {
        resetAccFrames();
        bounces = value;
        Config.setInt("bounces", value);
    }

    public static int getFrames() {
        return frames;
    }

    public static boolean isAccumulation() {
        return accumulation;
    }

    public static void useAccumulation(boolean value) {
        if (!value) resetAccFrames();
        accumulation = value;
        Config.setBoolean("accumulation", value);
    }

    public static boolean isTemporalReprojection() {
        return temporalReprojection;
    }

    public static void useTemporalReprojection(boolean value) {
        temporalReprojection = value;
        Config.setBoolean("temporal_reprojection", value);
    }

    public static float getGamma() {
        return gamma;
    }

    public static void setGamma(float value) {
        gamma = value;
        Config.setFloat("gamma", value);
    }

    public static float getExposure() {
        return exposure;
    }

    public static void setExposure(float value) {
        exposure = value;
        Config.setFloat("exposure", value);
    }

    public static boolean isTemporalAntialiasing() {
        return temporalAntialiasing;
    }

    public static void useTemporalAntialiasing(boolean value) {
        resetAccFrames();
        temporalAntialiasing = value;
        Config.setBoolean("temporal_antialiasing", value);
    }

    public static boolean isAtrousFilter() {
        return atrousFilter;
    }

    public static void useAtrousFilter(boolean value) {
        atrousFilter = value;
        Config.setBoolean("atrous_filter", value);
    }

    public static float getFocusDistance() {
        return focusDistance;
    }

    public static void setFocusDistance(float value) {
        resetAccFrames();
        focusDistance = value;
        Config.setFloat("focus_distance", value);
    }

    public static float getAperture() {
        return aperture;
    }

    public static void setAperture(float value) {
        resetAccFrames();
        aperture = value;
        Config.setFloat("aperture", value);
    }

    public static float getFOV() {
        return fov;
    }

    public static void setFOV(float value) {
        resetAccFrames();
        fov = value;
        Config.setFloat("fov", value);
    }
}