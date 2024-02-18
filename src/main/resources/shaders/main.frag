#version 460 core

out vec4 out_color;

struct Ray {
    vec3 origin, dir;
};

struct Material {
    float emission, roughness;
};

struct Sphere {
    vec3 position;
    float radius;
    vec3 color;
    Material material;
};

struct Box {
    vec3 position, rotation, size, color;
    Material material;
};

#define PI 3.14159
#define MAX_DISTANCE 999999999
#define MAX_SPHERES 3
#define MAX_BOXES 50

uniform vec2 u_resolution;
uniform vec3 u_camera_position, u_old_camera_position, sky_color, u_plane_color;
uniform vec4 u_plane_normal;
uniform mat4 proj, view, old_view;
uniform int u_samples, u_bounces, u_aa_size, u_gamma_correction, u_aces, u_reproj,
            u_show_albedo, u_show_depth, u_show_normals, sky_has_texture,
            u_spheres_count, u_boxes_count, u_plane_checkerboard;
uniform int u_random_noise;
uniform samplerCube sky_texture;
uniform float u_acc_frames, u_time, u_gamma, u_plane_emission, u_plane_roughness;
uniform Sphere spheres[MAX_SPHERES];
uniform Box boxes[MAX_BOXES];

layout(binding = 0, rgba32f) uniform image2D frame_image;

uint seed = 0;
vec2 uv = (2 * gl_FragCoord.xy - u_resolution) / u_resolution.y;
vec3 hitPos;

// source: https://www.reedbeta.com/blog/hash-functions-for-gpu-rendering/
uint pcg_hash(uint seed) {
    uint state = seed * 747796405u + 2891336453u;
    uint word = ((state >> ((state >> 28u) + 4u)) ^ state) * 277803737u;
    return (word >> 22u) ^ word;
}

float random() {
    seed = pcg_hash(seed);
    return float(seed) * (1 / 4294967296.0);
}

vec3 random_cosine_weighted_hemisphere(vec3 normal) {
    float a = random() * 2 * PI;
    float z = random() * 2 - 1;
    float r = sqrt(1 - z * z);
    float x = r * cos(a);
    float y = r * sin(a);
    return normalize(normal + vec3(x, y, z));
}

void update_seed() {
    seed = pcg_hash(uint(gl_FragCoord.x));
    seed = pcg_hash(seed + uint(gl_FragCoord.y));
    seed = pcg_hash(seed + uint(u_time * 1000));
}

float plane(Ray ray, vec4 p) {
    return -(dot(ray.origin, p.xyz) + p.w) / dot(ray.dir, p.xyz);
}

float checkerboard(vec2 p) {
    return mod(floor(p.x) + floor(p.y), 2);
}

float sphere(Ray ray, vec3 ce, float ra) {
    vec3 oc = ray.origin - ce;
    float b = dot(oc, ray.dir);
    float c = dot(oc, oc) - ra * ra;
    float h = b * b - c;
    if(h < 0) return -1;
    return -b - sqrt(h);
}

mat4 rotation_axis_angle(vec3 v, float angle) {
    float s = sin(angle);
    float c = cos(angle);
    float ic = 1.0 - c;
    return mat4(v.x*v.x*ic + c,     v.y*v.x*ic - s*v.z, v.z*v.x*ic + s*v.y, 0.0,
                v.x*v.y*ic + s*v.z, v.y*v.y*ic + c,     v.z*v.y*ic - s*v.x, 0.0,
                v.x*v.z*ic - s*v.y, v.y*v.z*ic + s*v.x, v.z*v.z*ic + c,     0.0,
                0.0,                0.0,                0.0,                1.0);
}

float box(in Ray ray, vec3 boxSize, out vec3 outNormal, vec3 rotation) {
    /*mat4 rot = rotationAxisAngle(vec3(normalize(rotation.x), 0, 0), rotation.x)
    * rotationAxisAngle(vec3(0, normalize(rotation.y), 0), rotation.y)
    * rotationAxisAngle(vec3(0, 0, normalize(rotation.z)), rotation.z);*/
    vec3 ro = (/*rot * */vec4(ray.origin, 1)).xyz;
    vec3 rd = (/*rot * */vec4(ray.dir, 0)).xyz;
    vec3 m = 1 / rd;
    vec3 n = m * ro;
    vec3 k = abs(m) * boxSize;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    float tN = max(max(t1.x, t1.y), t1.z);
    float tF = min(min(t2.x, t2.y), t2.z);
    if(tN > tF || tF < 0) return -1;
    outNormal = (tN > 0) ? step(vec3(tN), t1) : step(t2, vec3(tF));
    outNormal *= -sign(rd);
    return tN;
}

bool raycast(inout Ray ray, out vec3 col, out vec3 normal, out float minDistance, out Material material) {
    bool hit = false;
    float dist, minDist = MAX_DISTANCE;
    dist = plane(ray, u_plane_normal);
    if (dist > 0 && dist < minDist) {
        material.emission = u_plane_emission;
        material.roughness = u_plane_roughness;
        hit = true;
        minDist = dist;
        //col = vec3(1);
        //col = vec3(checkerboard(vec3(ray.dir * dist + ray.origin).xz * (0.06)));
        col = u_plane_checkerboard == 1 ? vec3(u_plane_color * checkerboard(vec3(ray.dir * dist + ray.origin).xz * (0.06))) : u_plane_color;
        //col = u_plane_color;
        normal = vec3(0, 1, 0);
    }
    for (int i = 0; i < u_spheres_count; i++) {
        dist = sphere(ray, spheres[i].position, spheres[i].radius);
        if (dist > 0 && dist < minDist) {
            hit = true;
            minDist = dist;
            col = spheres[i].color;
            material = spheres[i].material;
            normal = normalize(ray.origin + ray.dir * dist - spheres[i].position);
        }
    }
    for (int i = 0; i < u_boxes_count; i++) {
        vec3 norm;
        dist = box(Ray(ray.origin - boxes[i].position, ray.dir), boxes[i].size, norm, boxes[i].rotation).x;
        if (dist > 0 && dist < minDist) {
            hit = true;
            minDist = dist;
            col = boxes[i].color;
            material = boxes[i].material;
            normal = normalize(norm);
        }
    }
    if (!hit) {
        switch (sky_has_texture) {
            case 1:
                col = texture(sky_texture, ray.dir).rgb;
                break;
            case 0:
                col = sky_color;
                break;
        }
        material.emission = 1;
        material.roughness = 0;
        return true;
    }
    minDistance = minDist;
    return hit;
}

vec3 raytrace(Ray ray) {
    vec3 energy = vec3(1);
    for(int i = 0; i <= u_bounces; i++) {
        Material material;
        vec3 color, normal;
        float minIt;
        if (raycast(ray, color, normal, minIt, material)) {
            hitPos = ray.origin + ray.dir * minIt;
            ray.origin += ray.dir * (minIt - 0.001);
            // random() < material.roughness ? 1.0f : 0.0f
            ray.dir = mix(random_cosine_weighted_hemisphere(normal), reflect(ray.dir, normal), material.roughness);
            energy *= color;
            if (material.emission > 0) return energy * material.emission;
        }
    }
    return vec3(0);
}

vec3 post_process(vec3 col) {
    if (u_aces == 1) col = (col * (2.51f * col + 0.03f)) / (col * (2.43f * col + 0.59f) + 0.14f);
    if (u_gamma_correction == 1) col = pow(col, vec3(1.0 / u_gamma));
    return col;
}

vec3 reproject(mat3 pcam_mat, vec3 pcam_pos, vec2 iRes, vec3 p) {
    float td = distance(pcam_pos, p);
    vec3 dir = (p - pcam_pos)/td;
    vec3 screen = dir*pcam_mat;
    return vec3(screen.xy * iRes.y / (screen.z) + 0.5 * iRes.xy, td);
}

void main() {
    if (u_show_depth == 0 && u_show_albedo == 0 && u_show_normals == 0 && (u_acc_frames > 0 || u_random_noise == 1 || u_reproj == 1)) {
        update_seed();
    } else {
        seed = pcg_hash(uint(gl_FragCoord.x * gl_FragCoord.y));
    }

    // uv blur anti-aliasing
    if (u_show_depth == 0 && u_show_albedo == 0 && u_show_normals == 0) {
        uv.x += random() / 100000 * u_aa_size;
        uv.x -= random() / 100000 * u_aa_size;
        uv.y += random() / 100000 * u_aa_size;
        uv.y -= random() / 100000 * u_aa_size;
    }

    vec3 dir = normalize((mat3(proj) * vec3(uv, 1)) * mat3(view));
    // vec3 dir = normalize(vec3(uv, 1)) * mat3(u_camera_rotation);
    Ray ray = Ray(u_camera_position, dir);

    vec3 color;
    for(int i = 0; i < u_samples; i++) {
        color += raytrace(ray);
    }
    color /= u_samples;

    vec3 rep = reproject(mat3(old_view), u_old_camera_position, u_resolution, hitPos);

    if (u_show_depth == 1 || u_show_albedo == 1 || u_show_normals == 1) {
        vec3 n;
        float depth;
        Material m;
        raycast(ray, color, n, depth, m);
        if (u_show_normals == 1) color = n * 0.5 + 0.5;
        if (u_show_depth == 1) color = vec3(depth) * 0.001;
    } else {
        if (u_acc_frames > 0) {
            vec3 old_color = imageLoad(frame_image, ivec2(gl_FragCoord.xy)).rgb;
            color = mix(color, old_color, u_acc_frames / (u_acc_frames + 1));
            imageStore(frame_image, ivec2(gl_FragCoord.xy), vec4(color, 1));
        }
        if (u_reproj == 1) {
            vec3 old_color = imageLoad(frame_image, ivec2(gl_FragCoord.xy)).rgb;
            color = mix(color, old_color, 0.8);
            imageStore(frame_image, ivec2(gl_FragCoord.xy), vec4(color, 1));
        }
    }
    color = post_process(color);
    out_color.rgb = color;
}