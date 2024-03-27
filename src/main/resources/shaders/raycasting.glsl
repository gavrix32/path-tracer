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