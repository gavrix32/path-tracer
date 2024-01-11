#version 460 core

out vec4 out_color;

struct Ray {
    vec3 origin, direction;
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
    vec3 position, size, color;
    Material material;
};

uniform vec2 u_resolution;
uniform float u_time;
uniform vec3 u_camera_position;
uniform mat4 u_camera_rotation;
uniform int u_samples, u_bounces, u_aa_size, u_aces;
uniform int u_random_noise;
uniform sampler2D tex;
uniform samplerCube sky;
uniform float u_acc_frames;
uniform Box boxes[5];
uniform Sphere spheres[3];

#define PI 3.14159265358979323846;

uint seed;

vec2 uv = (2 * gl_FragCoord.xy - u_resolution) / u_resolution.y;

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
    seed = pcg_hash(seed + uint(u_time * 1000));
}
float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233))) * 43758.5453);
}
/////////////// RANDOM ///////////////

float plane(Ray ray, vec4 p) {
    return -(dot(ray.origin, p.xyz) + p.w) / dot(ray.direction, p.xyz);
}

float checkerBoard(vec2 p) {
    return mod(floor(p.x) + floor(p.y), 2);
}

float sphere(Ray ray, vec3 ce, float ra) {
    vec3 oc = ray.origin - ce;
    float b = dot(oc, ray.direction);
    float c = dot(oc, oc) - ra * ra;
    float h = b * b - c;
    if(h < 0) return -1;
    return -b - sqrt(h);
}

vec2 box(in Ray ray, vec3 boxSize, out vec3 outNormal) {
    vec3 m = 1 / ray.direction;
    vec3 n = m * ray.origin;
    vec3 k = abs(m) * boxSize;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    float tN = max(max(t1.x, t1.y), t1.z);
    float tF = min(min(t2.x, t2.y), t2.z);
    if(tN > tF || tF < 0) return vec2(-1);
    outNormal = (tN > 0) ? step(vec3(tN), t1) : step(t2, vec3(tF));
    outNormal *= -sign(ray.direction);
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
        //col = vec3(1);
        col = vec3(checkerBoard(vec3(ray.origin + ray.direction * it).xz * (1. / 16.)));
        normal = vec3(0, 1, 0);
    }
    for (int i = 0; i < spheres.length(); i++) {
        it = sphere(ray, spheres[i].position, spheres[i].radius);
        if (it > 0 && it < minIt) {
            hit = true;
            minIt = it;
            col = spheres[i].color;
            material = spheres[i].material;
            normal = normalize(ray.origin + ray.direction * it - spheres[i].position);
        }
    }
    for (int i = 0; i < boxes.length(); i++) {
        vec3 norm;
        it = box(Ray(ray.origin - boxes[i].position, ray.direction), boxes[i].size, norm).x;
        if (it > 0 && it < minIt) {
            hit = true;
            minIt = it;
            col = boxes[i].color;
            material = boxes[i].material;
            normal = norm;
        }
    }
    // Chunk
    /*for (int x = 0; x < 8; x++) {
        for (int y = 0; y < 8; y++) {
            for (int z = 0; z < 8; z++) {
                vec3 norm;
                it = box(Ray(ray.origin - vec3((x*16)+8, (y*16)+8, (z*16)+8), ray.direction), vec3(4), norm).x;
                if (it > 0 && it < minIt) {
                    hit = true;
                    minIt = it;
                    col = vec3(x/8., y/8., z/8.);
                    material.roughness = 0;
                    material.emission = 0;
                    normal = norm;
                }
            }
        }
    }*/
    if (!hit) {
        // col = vec3(0, 0.17, 0.20); // 0
        col = texture(sky, ray.direction).rgb;
        material.emission = 1;
        return true;
    }
    minDist = minIt;
    return hit;
}

vec3 raytrace(Ray ray) {
    vec3 energy = vec3(1);
    for(int i = 0; i <= u_bounces; i++) {
        Material material;
        vec3 color, normal;
        float minIt;
        if (raycast(ray, color, normal, minIt, material)) {
            ray.origin += ray.direction * (minIt - 0.01);
            ray.direction = mix(rand_vec3(), reflect(ray.direction, normal), material.roughness);
            energy *= color;
            if (material.emission > 0) return energy * material.emission;
        }
    }
    return vec3(0);
}

vec3 ACESFilm(vec3 col) {
    return (col * (2.51f * col + 0.03f)) / (col * (2.43f * col + 0.59f) + 0.14f);
}

void main() {
    if (u_acc_frames > 0) {
        updateSeed();
    } else if (u_random_noise == 1) {
        updateSeed();
    } else {
        seed = pcg_hash(uint(gl_FragCoord.x * gl_FragCoord.y));
    }

    // AA
    uv.x += rand_float() / 100000 * u_aa_size;
    uv.x -= rand_float() / 100000 * u_aa_size;
    uv.y += rand_float() / 100000 * u_aa_size;
    uv.y -= rand_float() / 100000 * u_aa_size;

    /*float yaw = 0, pitch = u_time;
    float x = cos(yaw * 3.14 / 180) * cos(pitch * 3.14 / 180);
    float y = sin(pitch * 3.14 / 180);
    float z = sin(yaw * 3.14 / 180) * cos(pitch * 3.14 / 180);
    vec3 d = vec3(x, y, z);*/

    /*m.y = -m.y;
    vec2 s = sin(m);
    vec2 c = cos(m);
    mat3 rotX = mat3(1.0, 0.0, 0.0, 0.0, c.y, s.y, 0.0, -s.y, c.y);
    mat3 rotY = mat3(c.x, 0.0, -s.x, 0.0, 1.0, 0.0, s.x, 0.0, c.x);*/


    //vec3 dir = normalize(vec3(uv, 1)) * rotX;
    vec3 dir = normalize(vec3(uv, 1)) * mat3(u_camera_rotation);
    Ray ray = Ray(u_camera_position, dir);

    // DOF
    /*vec3 fp = ray.ro + ray.dir * 3;
    ray.ro = ray.ro + mat3(uRotation) * vec3(rand_vec3().xy, 0) * 0.05;
    ray.dir = normalize(fp - ray.ro);*/

    // Path Tracing
    vec3 color;
    for(int i = 0; i < u_samples; i++) {
        color += raytrace(ray);
    }
    color /= u_samples;

    // Ray Casting
    /*vec3 n;
    float d;
    Material m;
    raycast(ray, color, n, d, m);*/

    if (u_aces == 1) color = ACESFilm(color);
    if (u_acc_frames > 0) color = mix(texture(tex, gl_FragCoord.xy / u_resolution).rgb, color, 1 / u_acc_frames);
    out_color = vec4(color, 1);
}