package net.gavrix32.engine.graphics;

import net.gavrix32.app.scenes.BVHTest;
import net.gavrix32.engine.io.Window;
import net.gavrix32.engine.math.Matrix4f;
import net.gavrix32.engine.math.Vector2f;
import net.gavrix32.engine.math.Vector3f;
import org.tinylog.Logger;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46C.*;
import static org.lwjgl.stb.STBImage.*;

public class Renderer {
    private static Scene scene;
    private static Shader pathtraceShader;
    private static Shader atrousShader;
    private static Shader presentShader;
    private static int accumulatedSamples = 0;
    private static int prevAcc = 1, currAcc = 0;
    private static int prevAtrous = 1, currAtrous = 0;
    private static Texture albedoImage, positionImage, normalImage;
    private static final Framebuffer[] accFramebuffer = new Framebuffer[2];
    private static final Texture[] accTexture = new Texture[2];
    private static final Framebuffer[] atrousFramebuffer = new Framebuffer[2];
    private static final Texture[] atrousTexture = new Texture[2];
    public static Vector3f triangles_offset = new Vector3f(0, 0, 0), triangles_rotation = new Vector3f(0, 0, 0);

    // Config variables
    private static int samples, maxAccumulatedSamples, bounces;
    private static float gamma, exposure, focusDistance, aperture, fov;
    private static boolean accumulation, temporalReprojection, temporalAntialiasing, atrousFilter;

    private static int modelTexture;

    public static void init() {
        samples = Config.getInt("samples");
        maxAccumulatedSamples = Config.getInt("max_accumulated_samples");
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

        pathtraceShader = new Shader();
        pathtraceShader.load("shaders/pathtrace.cs.glsl", GL_COMPUTE_SHADER);
        pathtraceShader.initProgram();

        atrousShader = new Shader();
        atrousShader.load("shaders/atrous.cs.glsl", GL_COMPUTE_SHADER);
        atrousShader.initProgram();

        presentShader = new Shader();
        presentShader.load("shaders/quad.vs.glsl", GL_VERTEX_SHADER);
        presentShader.load("shaders/present.fs.glsl", GL_FRAGMENT_SHADER);
        presentShader.initProgram();

        scene = new Scene();

        for (int i = 0; i < 2; i++) {
            accFramebuffer[i] = new Framebuffer();
            accFramebuffer[i].bind();

            accTexture[i] = new Texture();
            accTexture[i].bind();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
            accTexture[i].linearFiltering();
            accTexture[i].clampToEdge();
            glBindImageTexture(3, accTexture[i].id, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
            accTexture[i].unbind();

            accFramebuffer[i].unbind();
        }

        for (int i = 0; i < 2; i++) {
            atrousFramebuffer[i] = new Framebuffer();
            atrousFramebuffer[i].bind();

            atrousTexture[i] = new Texture();
            atrousTexture[i].bind();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
            atrousTexture[i].linearFiltering();
            atrousTexture[i].clampToEdge();
            glBindImageTexture(4, atrousTexture[i].id, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
            atrousTexture[i].unbind();

            atrousFramebuffer[i].unbind();
        }

        normalImage = new Texture();
        normalImage.bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindImageTexture(0, normalImage.id, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        normalImage.unbind();

        positionImage = new Texture();
        positionImage.bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindImageTexture(1, positionImage.id, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        positionImage.unbind();

        albedoImage = new Texture();
        albedoImage.bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        glBindImageTexture(2, albedoImage.id, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        albedoImage.unbind();

        // Send BVH data to GPU
        BoundingVolumeHierarchy bvh = BVHTest.bvh;

        float[] bvh_node_data = new float[12 * bvh.nodes.size()];
        int index = 0;
        for (int i = 0; i < bvh.nodes.size(); i++) {
            bvh_node_data[index++] = bvh.nodes.get(i).bounds.min.x;
            bvh_node_data[index++] = bvh.nodes.get(i).bounds.min.y;
            bvh_node_data[index++] = bvh.nodes.get(i).bounds.min.z;
            bvh_node_data[index++] = 0.0f;
            bvh_node_data[index++] = bvh.nodes.get(i).bounds.max.x;
            bvh_node_data[index++] = bvh.nodes.get(i).bounds.max.y;
            bvh_node_data[index++] = bvh.nodes.get(i).bounds.max.z;
            bvh_node_data[index++] = 0.0f;
            bvh_node_data[index++] = bvh.nodes.get(i).triangleStartIndex;
            bvh_node_data[index++] = bvh.nodes.get(i).trianglesCount;
            bvh_node_data[index++] = bvh.nodes.get(i).childIndex;
            bvh_node_data[index++] = 0.0f;
        }
        int bvh_nodes_ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, bvh_nodes_ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bvh_node_data, GL_STATIC_DRAW);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, bvh_nodes_ssbo);

        float[] bvh_triangles_data = new float[24 * bvh.triangles.size()];
        index = 0;
        for (int i = 0; i < bvh.triangles.size(); i++) {
            bvh_triangles_data[index++] = bvh.triangles.get(i).v1.x;
            bvh_triangles_data[index++] = bvh.triangles.get(i).v1.y;
            bvh_triangles_data[index++] = bvh.triangles.get(i).v1.z;
            bvh_triangles_data[index++] = 0.0f;
            bvh_triangles_data[index++] = bvh.triangles.get(i).v2.x;
            bvh_triangles_data[index++] = bvh.triangles.get(i).v2.y;
            bvh_triangles_data[index++] = bvh.triangles.get(i).v2.z;
            bvh_triangles_data[index++] = 0.0f;
            bvh_triangles_data[index++] = bvh.triangles.get(i).v3.x;
            bvh_triangles_data[index++] = bvh.triangles.get(i).v3.y;
            bvh_triangles_data[index++] = bvh.triangles.get(i).v3.z;
            bvh_triangles_data[index++] = 0.0f;
            bvh_triangles_data[index++] = bvh.triangles.get(i).uv1.x;
            bvh_triangles_data[index++] = bvh.triangles.get(i).uv1.y;
            bvh_triangles_data[index++] = 0.0f;
            bvh_triangles_data[index++] = 0.0f;
            bvh_triangles_data[index++] = bvh.triangles.get(i).uv2.x;
            bvh_triangles_data[index++] = bvh.triangles.get(i).uv2.y;
            bvh_triangles_data[index++] = 0.0f;
            bvh_triangles_data[index++] = 0.0f;
            bvh_triangles_data[index++] = bvh.triangles.get(i).uv3.x;
            bvh_triangles_data[index++] = bvh.triangles.get(i).uv3.y;
            bvh_triangles_data[index++] = 0.0f;
            bvh_triangles_data[index++] = 0.0f;
        }
        int bvh_triangles_ssbo = glGenBuffers();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, bvh_triangles_ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, bvh_triangles_data, GL_STATIC_DRAW);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, bvh_triangles_ssbo);

        modelTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, modelTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        int[] width = new int[1], height = new int[1];
        /*byte[] bytes = Utils.loadBytes("models/breakfast_room/picture3.jpg");
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
        buffer.put(bytes).flip();*/
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer data = stbi_load_from_memory(BVHTest.getModel().data, width, height, new int[1], 3);
        if (data == null) Logger.error("Failed to load texture: \"\"" + " " + stbi_failure_reason());
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width[0], height[0], 0, GL_RGB, GL_UNSIGNED_BYTE, data);
        glBindTexture(GL_TEXTURE_2D, 0);
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
        pathtraceShader.setInt("samples", samples);
        pathtraceShader.setInt("accumulated_samples", accumulatedSamples);
        pathtraceShader.setInt("max_accumulated_samples", maxAccumulatedSamples);
        pathtraceShader.setInt("bounces", bounces);
        pathtraceShader.setFloat("fov", fov);
        pathtraceShader.setBool("temporal_reprojection", temporalReprojection);
        pathtraceShader.setBool("temporal_antialiasing", temporalAntialiasing);
        pathtraceShader.setFloat("focus_distance", focusDistance);
        pathtraceShader.setFloat("aperture", aperture);
        pathtraceShader.setBool("sky_has_texture", scene.sky.hasTexture());
        pathtraceShader.setBool("debug_bvh", Gui.debugBVH.get());
        pathtraceShader.setInt("bounds_test_threshold", Gui.boundsTestThreshold[0]);
        pathtraceShader.setInt("triangle_test_threshold", Gui.triangleTestThreshold[0]);
        if (scene.sky.hasTexture()) {
            glActiveTexture(GL_TEXTURE1);
            scene.sky.bindTexture();
            pathtraceShader.setInt("sky_texture", 1);
        } else {
            pathtraceShader.setVec3("sky.material.albedo", scene.sky.getColor());
        }
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, modelTexture);
        pathtraceShader.setInt("model_texture", 2);
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
                pathtraceShader.setVec3("plane.material.albedo", scene.plane.getColor());
            }
            pathtraceShader.setFloat("plane.material.emission", scene.plane.getMaterial().getEmission());
            pathtraceShader.setFloat("plane.material.roughness", scene.plane.getMaterial().getRoughness());
            pathtraceShader.setBool("plane.material.is_glass", scene.plane.getMaterial().isGlass());
            pathtraceShader.setFloat("plane.material.IOR", scene.plane.getMaterial().getIOR());
            pathtraceShader.setBool("plane.material.is_metal", scene.plane.getMaterial().isMetal());
        } else {
            pathtraceShader.setInt("plane.exists", 0);
        }
        // Spheres
        BoundingBox sphBounds = new BoundingBox();
        pathtraceShader.setInt("spheres_count", scene.spheres.size());
        for (int i = 0; i < scene.spheres.size(); i++) {
            pathtraceShader.setVec3("spheres[" + i + "].position", scene.getSphere(i).getPos());
            pathtraceShader.setFloat("spheres[" + i + "].radius", scene.getSphere(i).getRadius());
            pathtraceShader.setVec3("spheres[" + i + "].material.albedo", scene.getSphere(i).getColor());
            pathtraceShader.setBool("spheres[" + i + "].material.is_metal", scene.getSphere(i).getMaterial().isMetal());
            pathtraceShader.setFloat("spheres[" + i + "].material.emission", scene.getSphere(i).getMaterial().getEmission());
            pathtraceShader.setFloat("spheres[" + i + "].material.roughness", scene.getSphere(i).getMaterial().getRoughness());
            pathtraceShader.setBool("spheres[" + i + "].material.is_glass", scene.getSphere(i).getMaterial().isGlass());
            pathtraceShader.setFloat("spheres[" + i + "].material.IOR", scene.getSphere(i).getMaterial().getIOR());
            sphBounds.addSphere(scene.getSphere(i));
        }
        pathtraceShader.setVec3("sph_bounds.min", sphBounds.min);
        pathtraceShader.setVec3("sph_bounds.max", sphBounds.max);
        // Boxes
        BoundingBox boxBounds = new BoundingBox();
        pathtraceShader.setInt("boxes_count", scene.boxes.size());
        for (int i = 0; i < scene.boxes.size(); i++) {
            pathtraceShader.setVec3("boxes[" + i + "].position", scene.boxes.get(i).getPos());
            scene.boxes.get(i).getRotationMatrix().rotate(
                    scene.boxes.get(i).getRot().x,
                    scene.boxes.get(i).getRot().y,
                    scene.boxes.get(i).getRot().z
            );
            pathtraceShader.setMat4("boxes[" + i + "].rotation", scene.getBox(i).getRotationMatrix());
            pathtraceShader.setVec3("boxes[" + i + "].scale", scene.getBox(i).getScale());
            pathtraceShader.setVec3("boxes[" + i + "].material.albedo", scene.getBox(i).getColor());
            pathtraceShader.setBool("boxes[" + i + "].material.is_metal", scene.getBox(i).getMaterial().isMetal());
            pathtraceShader.setFloat("boxes[" + i + "].material.emission", scene.getBox(i).getMaterial().getEmission());
            pathtraceShader.setFloat("boxes[" + i + "].material.roughness", scene.getBox(i).getMaterial().getRoughness());
            pathtraceShader.setBool("boxes[" + i + "].material.is_glass", scene.getBox(i).getMaterial().isGlass());
            pathtraceShader.setFloat("boxes[" + i + "].material.IOR", scene.getBox(i).getMaterial().getIOR());
            boxBounds.addBox(scene.getBox(i));
        }
        pathtraceShader.setVec3("box_bounds.min", boxBounds.min);
        pathtraceShader.setVec3("box_bounds.max", boxBounds.max);
        pathtraceShader.setVec3("triangles_offset", triangles_offset);
        pathtraceShader.setMat4("triangles_rotation", new Matrix4f().rotate(triangles_rotation));
        glActiveTexture(GL_TEXTURE3);
        accTexture[prevAcc].bind();
        pathtraceShader.setInt("prev_frame", 3);

        glBindImageTexture(3, accTexture[currAcc].id, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
        pathtraceShader.invokeCompute();

        if (atrousFilter) {
            for (int i = 0; i < Gui.iterations[0]; i++) {
                atrousShader.use();

                atrousShader.setVec2("resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
                atrousShader.setFloat("stepWidth", (1 << (i + 1)) - 1 * Gui.stepWidth[0]);
                atrousShader.setFloat("c_phi", 1.0f / i * Gui.c_phi[0]);
                atrousShader.setFloat("n_phi", 1.0f / (1 << i) * Gui.n_phi[0]);
                atrousShader.setFloat("p_phi", 1.0f / (1 << i) * Gui.p_phi[0]);

                glActiveTexture(GL_TEXTURE4);
                if (i == 0) accTexture[currAcc].bind();
                else atrousTexture[prevAtrous].bind();
                atrousShader.setInt("color_texture", 4);

                glActiveTexture(GL_TEXTURE5);
                normalImage.bind();
                atrousShader.setInt("normal_texture", 5);

                glActiveTexture(GL_TEXTURE6);
                positionImage.bind();
                atrousShader.setInt("position_texture", 6);

                glActiveTexture(GL_TEXTURE7);
                albedoImage.bind();
                atrousShader.setInt("albedo_texture", 7);

                glBindImageTexture(4, atrousTexture[currAtrous].id, 0, false, 0, GL_READ_WRITE, GL_RGBA32F);
                atrousShader.invokeCompute();

                swapAtrous();
            }
        }
        presentShader.use();
        presentShader.setVec2("resolution", new Vector2f(Window.getWidth(), Window.getHeight()));
        presentShader.setFloat("gamma", gamma);
        presentShader.setFloat("exposure", exposure);
        glActiveTexture(GL_TEXTURE8);
        if (atrousFilter) {
            atrousTexture[currAtrous].bind();
        } else {
            accTexture[currAcc].bind();
        }
        presentShader.setInt("color_texture", 8);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        Quad.draw();
        swapAccFrames();
        if (accumulation && accumulatedSamples != maxAccumulatedSamples)
            accumulatedSamples++;
    }

    public static void resetAccFrames() {
        accumulatedSamples = 0;
    }

    private static void swapAccFrames() {
        currAcc = 1 - currAcc;
        prevAcc = 1 - prevAcc;
    }

    private static void swapAtrous() {
        currAtrous = 1 - currAtrous;
        prevAtrous = 1 - prevAtrous;
    }

    public static void resetTextures() {
        accTexture[0].bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        accTexture[1].bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        normalImage.bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        positionImage.bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        albedoImage.bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        atrousTexture[0].bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, Window.getWidth(), Window.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        atrousTexture[1].bind();
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

    public static int getMaxAccumulatedSamples() {
        return maxAccumulatedSamples;
    }

    public static void setMaxAccumulatedSamples(int value) {
        resetAccFrames();
        maxAccumulatedSamples = value;
        Config.setInt("max_accumulated_samples", value);
    }

    public static int getBounces() {
        return bounces;
    }

    public static void setBounces(int value) {
        resetAccFrames();
        bounces = value;
        Config.setInt("bounces", value);
    }

    public static int getAccumulatedSamples() {
        return accumulatedSamples;
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