#version 420 core

out vec4 out_color;

struct Ray {
    vec3 origin, dir;
};

struct Material {
    vec3 color;
    bool is_metal;
    float emission;
    float roughness;
    float IOR;
};

struct Sphere {
    vec3 position;
    float radius;
    Material material;
};

struct Box {
    vec3 position, rotation, scale;
    Material material;
};

struct Sky {
    Material material;
};

struct Plane {
    bool exists;
    bool checkerboard;
    vec3 color1, color2;
    Material material;
};

#define PI 3.14159
#define EPSILON 0.001
#define MAX_DISTANCE 999999999
#define MAX_SPHERES 50
#define MAX_BOXES 50

uniform vec2 u_resolution;
uniform vec3 u_camera_position;
uniform mat4 view;
uniform int u_samples, u_bounces, taa, u_random_noise, u_gamma_correction, tonemapping, u_reproj,
            u_show_albedo, u_show_depth, u_show_normals, sky_has_texture,
            u_spheres_count, u_boxes_count, u_plane, u_plane_checkerboard, u_plane_is_dielectric;
uniform sampler2D sky_texture;
uniform float u_acc_frames, u_time, u_gamma, exposure;
uniform Sphere spheres[MAX_SPHERES];
uniform Box boxes[MAX_BOXES];
uniform Sky sky;
uniform Plane plane;

layout(binding = 0, rgba32f) uniform image2D frame_image;

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
    seed = pcg_hash(seed + uint(u_time * 1000));
}

float infinite_plane(Ray ray, vec4 p) {
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

/*mat4 rotation_axis_angle(vec3 v, float angle) {
    float s = sin(angle);
    float c = cos(angle);
    float ic = 1.0 - c;
    return mat4(v.x*v.x*ic + c,     v.y*v.x*ic - s*v.z, v.z*v.x*ic + s*v.y, 0.0,
                v.x*v.y*ic + s*v.z, v.y*v.y*ic + c,     v.z*v.y*ic - s*v.x, 0.0,
                v.x*v.z*ic - s*v.y, v.y*v.z*ic + s*v.x, v.z*v.z*ic + c,     0.0,
                0.0,                0.0,                0.0,                1.0);
}*/

float box(in Ray ray, vec3 boxSize, out vec3 outNormal, vec3 rotation) {
    /*mat4 rot = rotationAxisAngle(vec3(normalize(rotation.x), 0, 0), rotation.x)
    * rotationAxisAngle(vec3(0, normalize(rotation.y), 0), rotation.y)
    * rotationAxisAngle(vec3(0, 0, normalize(rotation.z)), rotation.z);*/
    vec3 ro = (/*rot **/ vec4(ray.origin, 1)).xyz;
    vec3 rd = (/*rot **/ vec4(ray.dir, 0)).xyz;
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
    if (plane.exists) {
        dist = infinite_plane(ray, vec4(0, 1, 0, 0));
        if (dist > 0 && dist < minDist) {
            hit = true;
            minDist = dist;
            material = plane.material;
            float cb = checkerboard(vec3(ray.dir * dist + ray.origin).xz * (0.06));
            if (vec3(plane.color1 * cb) != vec3(0))
                col = plane.color1;
            else
                col = plane.color2;
            normal = vec3(0, 1, 0);
        }
    }
    for (int i = 0; i < u_spheres_count; i++) {
        dist = sphere(ray, spheres[i].position, spheres[i].radius);
        if (dist > 0 && dist < minDist) {
            hit = true;
            minDist = dist;
            col = spheres[i].material.color;
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
            col = boxes[i].material.color;
            material = boxes[i].material;
            normal = normalize(norm);
        }
    }
    if (!hit) {
        switch (sky_has_texture) {
            case 1:
                vec2 uv_ = vec2(atan(ray.dir.z, ray.dir.x) / PI, asin(ray.dir.y) * 2 / PI);
                uv_ = uv_ * 0.5 + 0.5;
                col = texture(sky_texture, uv_).rgb;
                break;
            case 0:
                col = sky.material.color;
                break;
        }
        material = sky.material;
        return true;
    }
    minDistance = minDist;
    return hit;
}

Ray brdf(Ray ray, vec3 normal, Material material, float minIt) {
    vec3 diffused = random_cosine_weighted_hemisphere(normal);
    vec3 reflected = reflect(ray.dir, normal);
    vec3 refracted = refract(ray.dir, normal, material.IOR);
    if (material.is_metal) {
        ray.dir = mix(reflected, diffused, material.roughness);
    } else {
        ray.dir = mix(reflected, diffused, random() < material.roughness ? 1 : 0);
    }
    return ray;
}

vec3 raytrace(Ray ray) {
    vec3 energy = vec3(1);
    for(int i = 0; i <= u_bounces; i++) {
        Material material;
        vec3 color, normal;
        float minIt;
        if (raycast(ray, color, normal, minIt, material)) {
            /*if (material.roughness < 1) {
                ray.origin += ray.dir * (minIt + EPSILON);
            } else {
                ray.origin += ray.dir * (minIt - EPSILON);
            }*/
            ray.origin += ray.dir * (minIt - EPSILON);
            ray = brdf(ray, normal, material, minIt);
            energy *= color;
            if (material.emission > 0) return energy * material.emission;
        }
    }
    return vec3(0);
}

vec3 post_process(vec3 col) {
    if (tonemapping == 1) col = vec3(1.0) - exp(-col * exposure);
    if (u_gamma_correction == 1) col = pow(col, vec3(1.0 / u_gamma));
    return col;
}

void main() {
    if (u_show_depth == 0 && u_show_albedo == 0 && u_show_normals == 0 && (u_acc_frames > 0 || u_random_noise == 1 || u_reproj == 1)) {
        update_seed();
    } else {
        seed = pcg_hash(uint(gl_FragCoord.x * gl_FragCoord.y));
    }

    vec2 uv;

    if (u_show_depth == 0 && u_show_albedo == 0 && u_show_normals == 0) {
        uv = (2 * gl_FragCoord.xy - u_resolution) / u_resolution.y;
    } else {
        uv = (2 * gl_FragCoord.xy - u_resolution) / u_resolution.y;
    }

    // TAA
    if (taa == 1) {
        uv.x += (random() - 0.5) / (u_resolution.x * 0.5);
        uv.y += (random() - 0.5) / (u_resolution.y * 0.5);
    }

    vec3 dir = normalize(vec3(uv, 1) * mat3(view));
    Ray ray = Ray(u_camera_position, dir);

    vec3 color;
    for(int i = 0; i < u_samples; i++) {
        color += raytrace(ray);
    }
    color /= u_samples;

    if (u_show_depth == 1 || u_show_albedo == 1 || u_show_normals == 1) {
        vec3 n;
        float depth;
        Material m;
        raycast(ray, color, n, depth, m);
        if (u_show_normals == 1) color = n * 0.5 + 0.5;
        if (u_show_depth == 1) color = vec3(depth) * 0.001;
    } else {
        if (u_acc_frames > 0) {
            vec3 prev_color = imageLoad(frame_image, ivec2(gl_FragCoord.xy)).rgb;
            color = mix(color, prev_color, u_acc_frames / (u_acc_frames + 1));
            imageStore(frame_image, ivec2(gl_FragCoord.xy), vec4(color, 1));
        }
        if (u_reproj == 1) {
            vec3 prev_color = imageLoad(frame_image, ivec2(gl_FragCoord.xy)).rgb;
            color = mix(color, prev_color, 0.8);
            imageStore(frame_image, ivec2(gl_FragCoord.xy), vec4(color, 1));
        }
    }
    color = post_process(color);
    out_color.rgb = vec3(color);
    //out_color.rgb = vec3((uv * u_resolution.y + u_resolution) / 2, 0);
}