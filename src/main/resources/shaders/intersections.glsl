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