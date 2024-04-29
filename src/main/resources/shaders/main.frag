#version 420 core

out vec3 out_color;

#define PI 3.14159
#define EPSILON 0.001
#define MAX_DISTANCE 999999999
#define MAX_SPHERES 16
#define MAX_BOXES 16
#define MAX_TRIANGLES 16

struct Ray {
    vec3 o, d;
};

struct Material {
    vec3 color;
    bool is_metal;
    float emission;
    float roughness;
    bool is_glass;
    float IOR;
};

struct HitInfo {
    Ray ray;
    vec3 normal;
    float minDistance;
    Material material;
};

struct Sphere {
    vec3 position;
    float radius;
    Material material;
};

struct Box {
    vec3 position, scale;
    mat4 rotation;
    Material material;
};

struct Triangle {
    vec3 v1, v2, v3;
    mat4 rotation;
    Material material;
};

struct Sky {
    Material material;
};

struct Plane {
    bool exists, checkerboard;
    vec3 color1, color2;
    Material material;
};

uniform vec2 resolution;
uniform vec3 camera_position;
uniform mat4 euler_rotation;
uniform int samples, bounces, use_taa, use_dof, autofocus, random_noise, gamma_correction, tonemapping, frame_mixing,
show_albedo, show_depth, show_normals, sky_has_texture, spheres_count, boxes_count, triangles_count;
uniform sampler2D sky_texture;
uniform float acc_frames, time, gamma, exposure, fov, focus_distance, defocus_blur;
uniform Sphere spheres[MAX_SPHERES];
uniform Box boxes[MAX_BOXES];
uniform Triangle triangles[MAX_TRIANGLES];
uniform Sky sky;
uniform Plane plane;

vec3 p;

float intersect_plane(Ray ray, vec4 p) {
    return -(dot(ray.o, p.xyz) + p.w) / dot(ray.d, p.xyz);
}

float intersect_sphere(Ray ray, vec3 ce, float ra) {
    vec3 oc = ray.o - ce;
    float b = dot(oc, ray.d);
    float c = dot(oc, oc) - ra * ra;
    float h = b * b - c;
    if (h < 0) return -1;
    return -b - sqrt(h);
}

float intersect_box(Ray ray, vec3 scale, out vec3 normal, mat4 rotation) {
    vec3 ro = (rotation * vec4(ray.o, 1)).xyz;
    vec3 rd = (rotation * vec4(ray.d, 0)).xyz;
    vec3 m = 1 / rd;
    vec3 n = m * ro;
    vec3 k = abs(m) * scale;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    float tN = max(max(t1.x, t1.y), t1.z);
    float tF = min(min(t2.x, t2.y), t2.z);
    if (tN > tF || tF < 0) return -1;
    normal = (tN > 0) ? step(vec3(tN), t1) : step(t2, vec3(tF));
    normal *= -sign(rd);
    normal *= mat3(rotation);
    return tN;
}

vec3 intersect_triangle(Ray ray, in vec3 v0, in vec3 v1, in vec3 v2, out vec3 normal, mat4 rotation) {
    vec3 ro = (rotation * vec4(ray.o, 1)).xyz;
    vec3 rd = (rotation * vec4(ray.d, 0)).xyz;
    vec3 v1v0 = v1 - v0;
    vec3 v2v0 = v2 - v0;
    vec3 rov0 = ro - v0;
    vec3 n = cross(v1v0, v2v0);
    vec3 q = cross(rov0, rd);
    float d = 1.0 / dot(rd, n);
    float u = d * dot(-q, v2v0);
    float v = d * dot(q, v1v0);
    float t = d * dot(-n, rov0);
    if (u < 0.0 || v < 0.0 || (u + v) > 1.0) t = -1.0;
    normal = n * mat3(rotation);
    return vec3(t, u, v);
}

float checkerboard(vec2 p) {
    return mod(floor(p.x) + floor(p.y), 2);
}

bool raycast(inout Ray ray, out HitInfo hitInfo) {
    hitInfo.minDistance = MAX_DISTANCE;
    bool hit = false;
    float dist;
    if (plane.exists) {
        dist = intersect_plane(ray, vec4(0, 1, 0, 0));
        if (dist > 0 && dist < hitInfo.minDistance) {
            hit = true;
            hitInfo.minDistance = dist;
            hitInfo.material = plane.material;
            hitInfo.normal = vec3(0, 1, 0);
            if (plane.checkerboard) {
                float cb = checkerboard(vec3(ray.d * dist + ray.o).xz * (0.06));
                if (vec3(plane.color1 * cb) != vec3(0))
                hitInfo.material.color = plane.color1;
                else
                hitInfo.material.color = plane.color2;
            } else {
                hitInfo.material.color = plane.material.color;
            }
        }
    }
    for (int i = 0; i < spheres_count; i++) {
        dist = intersect_sphere(ray, spheres[i].position, spheres[i].radius);
        if (dist > 0 && dist < hitInfo.minDistance) {
            hit = true;
            hitInfo.minDistance = dist;
            hitInfo.material = spheres[i].material;
            hitInfo.normal = normalize(ray.o + ray.d * dist - spheres[i].position);
        }
    }
    for (int i = 0; i < boxes_count; i++) {
        vec3 normal;
        dist = intersect_box(Ray(ray.o - boxes[i].position, ray.d), boxes[i].scale, normal, boxes[i].rotation).x;
        if (dist > 0 && dist < hitInfo.minDistance) {
            hit = true;
            hitInfo.minDistance = dist;
            hitInfo.material = boxes[i].material;
            hitInfo.normal = normalize(normal);
        }
    }
    for (int i = 0; i < triangles_count; i++) {
        vec3 normal;
        dist = intersect_triangle(Ray(ray.o, ray.d), triangles[i].v1, triangles[i].v2, triangles[i].v3, normal, triangles[i].rotation).x;
        if (dist > 0 && dist < hitInfo.minDistance) {
            hit = true;
            hitInfo.minDistance = dist;
            hitInfo.material = triangles[i].material;
            hitInfo.normal = normalize(normal);
        }
    }
    if (!hit) {
        hitInfo.material = sky.material;
        hitInfo.minDistance = MAX_DISTANCE;
        switch (sky_has_texture) {
            case 1:
            vec2 uv_ = vec2(atan(ray.d.z, ray.d.x) / PI, asin(ray.d.y) * 2 / PI);
            uv_ = uv_ * 0.5 + 0.5;
            hitInfo.material.color = texture(sky_texture, uv_).rgb;
            break;
            case 0:
            hitInfo.material.color = sky.material.color;
            break;
        }
        return true;
    }
    return hit;
}

uint seed = 0;

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
    seed = pcg_hash(seed + uint(time * 1000));
}

float fresnel(vec3 dir, vec3 n, float ior) {
    float cosi = dot(dir, n);
    float etai = 1.0;
    float etat = ior;
    if (cosi > 0.0) {
        float tmp = etai;
        etai = etat;
        etat = tmp;
    }
    float sint = etai / etat * sqrt(max(0.0, 1.0 - cosi * cosi));
    if (sint >= 1.0) return 1.0;
    float cost = sqrt(max(0.0, 1.0 - sint * sint));
    cosi = abs(cosi);
    float sqrtRs = ((etat * cosi) - (etai * cost)) / ((etat * cosi) + (etai * cost));
    float sqrtRp = ((etai * cosi) - (etat * cost)) / ((etai * cosi) + (etat * cost));
    return (sqrtRs * sqrtRs + sqrtRp * sqrtRp) / 2.0;
}

Ray brdf(Ray ray, HitInfo hitInfo) {
    if (hitInfo.material.is_glass)
    ray.o += ray.d * (hitInfo.minDistance + EPSILON);
    else
    ray.o += ray.d * (hitInfo.minDistance - EPSILON);
    vec3 diffused = random_cosine_weighted_hemisphere(hitInfo.normal);
    vec3 reflected = reflect(ray.d, hitInfo.normal);
    vec3 refracted = refract(ray.d, hitInfo.normal, 1 / hitInfo.material.IOR);
    if (hitInfo.material.is_glass) {
        if (fresnel(ray.d, hitInfo.normal, hitInfo.material.IOR) > random()) refracted = reflected;
        if (hitInfo.material.is_metal)
        ray.d = mix(refracted, diffused, hitInfo.material.roughness > 0.5 ? 0.5 : hitInfo.material.roughness);
        else
        ray.d = mix(refracted, diffused, random() < hitInfo.material.roughness ? 1 : 0);
    } else {
        if (hitInfo.material.is_metal)
        ray.d = mix(reflected, diffused, hitInfo.material.roughness);
        else
        ray.d = mix(reflected, diffused, random() < hitInfo.material.roughness ? 1 : 0);
    }
    return ray;
}

vec3 trace(Ray ray) {
    vec3 energy = vec3(1);
    for(int i = 0; i <= bounces; i++) {
        HitInfo hitInfo;
        if (raycast(ray, hitInfo)) {
            ray = brdf(ray, hitInfo);
            energy *= hitInfo.material.color;
            if (hitInfo.material.emission > 0) return energy * hitInfo.material.emission;
        }
    }
    return vec3(0);
}

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

    vec3 dir = normalize(vec3(uv, 1.0 / tan(radians(fov) / 2))) * mat3(euler_rotation);
    Ray ray = Ray(camera_position, dir);

    // depth of field
    if (use_dof == 1) {
        float focus_dist;
        if (autofocus == 1) {
            HitInfo autofocus_hitinfo;
            Ray autofocus_ray = Ray(camera_position, normalize(vec3(vec2(0.001), 1.0 / tan(radians(fov) / 2)) * mat3(euler_rotation)));
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