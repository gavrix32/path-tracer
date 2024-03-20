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
    vec3 diffused = random_cosine_weighted_hemisphere(hitInfo.normal);
    vec3 reflected = reflect(ray.dir, hitInfo.normal);
    vec3 refracted = refract(ray.dir, hitInfo.normal, 1 / hitInfo.material.IOR);
    if (hitInfo.material.is_glass) {
        if (fresnel(ray.dir, hitInfo.normal, hitInfo.material.IOR) > random()) refracted = reflected;
        if (hitInfo.material.is_metal)
        ray.dir = mix(refracted, diffused, hitInfo.material.roughness > 0.5 ? 0.5 : hitInfo.material.roughness);
        else
        ray.dir = mix(refracted, diffused, random() < hitInfo.material.roughness ? 1 : 0);
    } else {
        if (hitInfo.material.is_metal)
        ray.dir = mix(reflected, diffused, hitInfo.material.roughness);
        else
        ray.dir = mix(reflected, diffused, random() < hitInfo.material.roughness ? 1 : 0);
    }
    return ray;
}

vec3 trace(Ray ray) {
    vec3 energy = vec3(1);
    for(int i = 0; i <= bounces; i++) {
        HitInfo hitInfo;
        if (raycast(ray, hitInfo)) {
            if (hitInfo.material.is_glass)
            ray.pos += ray.dir * (hitInfo.minDistance + EPSILON);
            else
            ray.pos += ray.dir * (hitInfo.minDistance - EPSILON);
            ray = brdf(ray, hitInfo);
            energy *= hitInfo.material.color;
            if (hitInfo.material.emission > 0) return energy * hitInfo.material.emission;
        }
    }
    return vec3(0);
}