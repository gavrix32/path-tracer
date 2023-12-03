#version 460 core

out vec4 color;

struct Ray {
    vec3 ro, rd;
};

struct Material {
    float emission, roughness;
};

struct Sphere {
    vec3 pos;
    float rad;
    vec3 col;
    Material material;
};

struct Box {
    vec3 pos, size, col;
    Material material;
};

uniform vec2 uRes;
uniform float uTime;
uniform vec3 ro;
uniform mat4 uRotation;
uniform int samples, bounces, AASize;
uniform int randNoise;
uniform sampler2D tex;
uniform float uAccumulate;
uniform Box boxes[5];
uniform Sphere spheres[3];

#define PI 3.14159265358979323846;

uint seed;

vec2 uv = (2 * gl_FragCoord.xy - uRes) / uRes.y;

/////////////// RANDOM ///////////////
uint pcg_hash(uint seed) {
    uint state = seed * 747796405u + 2891336453u;
    uint word = ((state >> ((state >> 28u) + 4u)) ^ state) * 277803737u;
    return (word >> 22u) ^ word;
}
float rand_float() {
    seed = pcg_hash(seed);
    return float(seed) * (1 / 4294967296.0);
}
vec3 rand_vec3() {
    float r = rand_float() * 2 * PI;
    float z = rand_float() * 2 - 1;
    float z_scale = sqrt(1.0 - z * z);
    return vec3(cos(r) * z_scale, sin(r) * z_scale, z);
}
void updateSeed() {
    seed = pcg_hash(uint(gl_FragCoord.x));
    seed = pcg_hash(seed + uint(gl_FragCoord.y));
    seed = pcg_hash(seed + uint(uTime * 1000));
}
float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}
/////////////// RANDOM ///////////////

float plane(Ray ray, vec4 p) {
    return -(dot(ray.ro, p.xyz) + p.w) / dot(ray.rd, p.xyz);
}

float sphere(Ray ray, vec3 ce, float ra) {
    vec3 oc = ray.ro - ce;
    float b = dot(oc, ray.rd);
    float c = dot(oc, oc) - ra * ra;
    float h = b * b - c;
    if(h < 0) return -1;
    return -b - sqrt(h);
}

vec2 box(in Ray ray, vec3 boxSize, out vec3 outNormal) {
    vec3 m = 1 / ray.rd;
    vec3 n = m * ray.ro;
    vec3 k = abs(m) * boxSize;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    float tN = max(max(t1.x, t1.y), t1.z);
    float tF = min(min(t2.x, t2.y), t2.z);
    if(tN > tF || tF < 0) return vec2(-1);
    outNormal = (tN > 0) ? step(vec3(tN), t1) : step(t2, vec3(tF));
    outNormal *= -sign(ray.rd);
    return vec2(tN, tF);
}

bool raycast(inout Ray ray, out vec3 col, out vec3 normal, out float minDist, out Material material) {
    bool hit = false;
    float it, minIt = 99999;
    vec3 n;
    it = plane(ray, vec4(0, 1, 0, 0));
    if (it > 0 && it < minIt) {
        material.emission = 0;
        material.roughness = 0;
        hit = true;
        minIt = it;
        col = vec3(0.8);
        normal = vec3(0, 1, 0);
    }
    for (int i = 0; i < spheres.length(); i++) {
        it = sphere(ray, spheres[i].pos, spheres[i].rad);
        if (it > 0 && it < minIt) {
            hit = true;
            minIt = it;
            col = spheres[i].col;
            material = spheres[i].material;
            normal = normalize(ray.ro + ray.rd * it - spheres[i].pos);
        }
    }
    for (int i = 0; i < boxes.length(); i++) {
        vec3 norm;
        it = box(Ray(ray.ro - boxes[i].pos, ray.rd), boxes[i].size, norm).x;
        if (it > 0 && it < minIt) {
            hit = true;
            minIt = it;
            col = boxes[i].col;
            material = boxes[i].material;
            normal = norm;
        }
    }
    if (!hit) {
        col = vec3(0); // 0
        material.emission = 1;
        return true;
    }
    minDist = minIt;
    return hit;
}

vec3 raytrace(Ray ray) {
    vec3 energy = vec3(1);
    for(int i = 0; i < bounces; i++) {
        Material material;
        vec3 col, n;
        float minIt;
        if (raycast(ray, col, n, minIt, material)) {
            ray.ro += ray.rd * (minIt - 0.01);
            ray.rd = mix(rand_vec3(), reflect(ray.rd, n), material.roughness);
            energy *= col;
            if (material.emission > 0) return energy * material.emission;
        }
    }
    return vec3(0);
}

void main() {
    if (uAccumulate > 0) {
        updateSeed();
    } else if (randNoise == 1) {
        updateSeed();
    } else {
        seed = pcg_hash(uint(gl_FragCoord.x * gl_FragCoord.y));
    }

    // AA
    uv.x += rand_float() / 100000 * AASize;
    uv.x -= rand_float() / 100000 * AASize;
    uv.y += rand_float() / 100000 * AASize;
    uv.y -= rand_float() / 100000 * AASize;

    vec3 rd = normalize(vec3(uv, 1)) * mat3(uRotation);
    Ray ray = Ray(ro, rd);
    vec3 col = vec3(0);
    for(int i = 0; i < samples; i++) {
        col += raytrace(ray);
    }
    col /= samples;
    col = pow(col, vec3(1.0 / 2.2));

    if (uAccumulate > 0) col = mix(texture(tex, gl_FragCoord.xy / uRes).rgb, col, 1 / uAccumulate);
    color = vec4(col, 1);
}