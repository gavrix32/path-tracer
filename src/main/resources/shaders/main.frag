#version 420 core

out vec3 out_color;

#include shaders/defines.glsl
#include shaders/structures.glsl
#include shaders/uniforms.glsl
#include shaders/intersections.glsl
#include shaders/raycasting.glsl
#include shaders/pathtracing.glsl

layout(binding = 0, rgba32f) uniform image2D frame_image;

vec3 post_process(vec3 col) {
    if (tonemapping == 1) col = vec3(1.0) - exp(-col * exposure);
    if (gamma_correction == 1) col = pow(col, vec3(1.0 / gamma));
    return col;
}

void main() {
    if (acc_frames > 0 || random_noise == 1 || frame_mixing == 1)
        update_seed();
    else
        seed = pcg_hash(uint(gl_FragCoord.x * gl_FragCoord.y));

    vec2 uv = (2 * gl_FragCoord.xy - resolution) / resolution.y;

    if (use_taa == 1) {
        uv.x += (random() - 0.5) * 0.002;
        uv.y += (random() - 0.5) * 0.002;
    }

    vec3 dir = normalize(vec3(uv, 1.0 / tan(radians(fov) / 2))) * mat3(view);
    Ray ray = Ray(camera_position, dir);

    // depth of field
    if (use_dof == 1) {
        float focus_dist;
        if (autofocus == 1) {
            HitInfo autofocus_hitinfo;
            Ray autofocus_ray = Ray(camera_position, normalize(vec3(vec2(0.001), 1.0 / tan(fov * (PI / 180) * 0.5)) * mat3(view)));
            raycast(autofocus_ray, autofocus_hitinfo);
            focus_dist = autofocus_hitinfo.minDistance >= MAX_DISTANCE ? 1000 : autofocus_hitinfo.minDistance;
        } else {
            focus_dist = focus_distance;
        }
        vec3 dof_position = defocus_blur * vec3(random_cosine_weighted_hemisphere(vec3(0)).xy, 0);
        vec3 dof_direction = normalize(ray.d * focus_dist - dof_position);
        ray.o += dof_position;
        ray.d = normalize(dof_direction);
    }

    vec3 color;
    for (int i = 0; i < samples; i++) color += trace(ray);
    color /= samples;

    if (show_depth == 1 || show_albedo == 1 || show_normals == 1) {
        HitInfo hitInfo;
        raycast(ray, hitInfo);
        color = hitInfo.material.color;
        if (show_normals == 1) color = hitInfo.normal * 0.5 + 0.5;
        if (show_depth == 1) color = vec3(hitInfo.minDistance) * 0.001;
    } else {
        if (acc_frames > 0) {
            vec3 prev_color = imageLoad(frame_image, ivec2(gl_FragCoord.xy)).rgb;
            color = mix(color, prev_color, acc_frames / (acc_frames + 1));
            imageStore(frame_image, ivec2(gl_FragCoord.xy), vec4(color, 1));
        }
        if (frame_mixing == 1) {
            vec3 prev_color = imageLoad(frame_image, ivec2(gl_FragCoord.xy)).rgb;
            color = mix(color, prev_color, 0.8);
            imageStore(frame_image, ivec2(gl_FragCoord.xy), vec4(color, 1));
        }
    }
    out_color = post_process(color);
}