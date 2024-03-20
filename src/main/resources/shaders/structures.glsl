struct Ray {
    vec3 pos, dir;
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