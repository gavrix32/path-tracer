#version 460 core

out vec4 out_color;

struct Ray {
    vec3 origin, dir;
};

struct Material {
    float emission, roughness;
    bool isMetal;
};

struct Sphere {
    vec3 position;
    float radius;
    vec3 color;
    Material material;
};

struct Box {
    vec3 position, rotation, scale, color;
    Material material;
};

struct Sky {
    vec3 color;
    Material material;
};

#define PI 3.14159
#define MAX_DISTANCE 999999999
#define MAX_SPHERES 50
#define MAX_BOXES 50

uniform vec2 u_resolution;
uniform vec3 u_camera_position, u_old_camera_position, u_plane_color;
uniform mat4 proj, view, old_view;
uniform int u_samples, u_bounces, u_aa_size, u_random_noise, u_gamma_correction, u_aces, u_reproj,
            u_show_albedo, u_show_depth, u_show_normals, sky_has_texture,
            u_spheres_count, u_boxes_count, u_plane_checkerboard, u_plane_is_dielectric;
uniform samplerCube sky_texture;
uniform float u_acc_frames, u_time, u_gamma, u_plane_emission, u_plane_roughness;
uniform Sphere spheres[MAX_SPHERES];
uniform Box boxes[MAX_BOXES];
uniform Sky sky;

layout(binding = 0, rgba32f) uniform image2D frame_image;

uint seed = 0;
vec2 uv = (2 * gl_FragCoord.xy - u_resolution) / u_resolution.y;
vec3 hitPos;
float minD;

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

vec3 triangle(in vec3 ro, in vec3 rd, in vec3 v0, in vec3 v1, in vec3 v2) {
    vec3 v1v0 = v1 - v0;
    vec3 v2v0 = v2 - v0;
    vec3 rov0 = ro - v0;
    vec3 n = cross(v1v0, v2v0);
    vec3 q = cross(rov0, rd);
    float d = 1.0 / dot(rd, n);
    float u = d * dot(-q, v2v0);
    float v = d * dot( q, v1v0);
    float t = d * dot(-n, rov0);
    if(u < 0.0 || v < 0.0 || (u + v) > 1.0) t = -1.0;
    return vec3(t, u, v);
}

bool raycast(inout Ray ray, out vec3 col, out vec3 normal, out float minDistance, out Material material) {
    bool hit = false;
    float dist, minDist = MAX_DISTANCE;
    dist = plane(ray, vec4(0, 1, 0, 0));
    if (dist > 0 && dist < minDist) {
        material.emission = u_plane_emission;
        material.roughness = u_plane_roughness;
        hit = true;
        minDist = dist;
        col = u_plane_checkerboard == 1 ? vec3(u_plane_color * checkerboard(vec3(ray.dir * dist + ray.origin).xz * (0.06))) : u_plane_color;
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
        dist = box(Ray(ray.origin - boxes[i].position, ray.dir), boxes[i].scale, norm, boxes[i].rotation).x;
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
                col = sky.color;;
                break;
        }
        material = sky.material;
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
            minD = minIt;
            ray.origin += ray.dir * (minIt - 0.001);
            vec3 diffused = random_cosine_weighted_hemisphere(normal);
            vec3 reflected = reflect(ray.dir, normal);
            if (material.isMetal) {
                ray.dir = mix(reflected, diffused, material.roughness);
            } else {
                ray.dir = mix(reflected, diffused, random() < material.roughness ? 1 : 0);
            }
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

vec3 project2Screen(const mat4 view, const mat4 proj, vec3 v) {
    vec4 p = proj * (view * vec4(v, 1.0));
    p /= p.w;
    p.xy += 0.5;
    p.z *= 2.0;
    p.z -= 1.0;
    return p.xyz;
}

mat4 getProjMatrix(vec2 size, float near, float far) {
    float fn = far + near;
    float f_n = far - near;

    return mat4(
        1.0,      0.0,    0.0,                         0.0,
        0.0,      1.0,    0.0,                         0.0,
        0.0,      0.0,    fn / f_n,                    1.0,
        0.0,      0.0,    (2.0 * far * near) / f_n,    0.0);
}

void main() {
    if (u_show_depth == 0 && u_show_albedo == 0 && u_show_normals == 0 && (u_acc_frames > 0 || u_random_noise == 1 || u_reproj == 1)) {
        update_seed();
    } else {
        seed = pcg_hash(uint(gl_FragCoord.x * gl_FragCoord.y));
    }

    // uv blur anti-aliasing
    if (u_show_depth == 0 && u_show_albedo == 0 && u_show_normals == 0) {
        uv.x += (random() - 0.5) / 100000 * u_aa_size;
        uv.y += (random() - 0.5) / 100000 * u_aa_size;
    }

    //vec3 dir = normalize(vec3(uv, 1) * mat3(view));
    mat4 proj = getProjMatrix(u_resolution, 1, 100);
    vec3 dir = normalize(vec3(uv, 1) * mat3(proj) * mat3(view));
    Ray ray = Ray(u_camera_position, dir);

    vec3 color;
    for(int i = 0; i < u_samples; i++) {
        color += raytrace(ray);
    }
    color /= u_samples;

    vec3 worldPos = ray.origin + ray.dir * minD;
    /*vec3 projPos = project2Screen(old_view, proj, worldPos);*/

    vec3 cpos = ((old_view * vec4(worldPos, 1))).xyz;
    vec2 npos = cpos.xy / cpos.z;
    vec2 spos = 0.5 + 0.5 * npos * vec2(u_resolution.y / u_resolution.x, 1.0);
    vec2 rpos = spos * u_resolution.xy - .5;
    ivec2 ipos = ivec2(floor(rpos));
    vec2 fuv = rpos - vec2(ipos);

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
            vec3 old_color = imageLoad(frame_image, ivec2(gl_FragCoord.xy/* - fuv * u_resolution*/)).rgb;
            color = mix(color, old_color, 0.8);
            imageStore(frame_image, ivec2(gl_FragCoord.xy), vec4(color, 1));
        }
    }
    color = post_process(color);
    //out_color.rgb = vec3(fuv * u_resolution, 0);
    out_color.rgb = vec3(color);
}