#version 460 core

layout(local_size_x = 8, local_size_y = 8) in;
layout(binding = 4, rgba32f) uniform image2D atrous_image;

uniform sampler2D color_texture;
uniform sampler2D normal_texture;
uniform sampler2D position_texture;
uniform sampler2D albedo_texture;

uniform int radius, step_size;
uniform float sigma_spatial;
uniform float sigma_color;
uniform float sigma_depth;
uniform float sigma_normal;

float gaussianWeight(float distance, float sigma) {
    return exp(-(distance * distance) / (2.0 * sigma * sigma));
}

void main() {
    vec2 resolution = imageSize(atrous_image);
    vec2 uv = (gl_GlobalInvocationID.xy + 0.5) / resolution;

    vec3 centerColor = texture(color_texture, uv).rgb;
    float variance = texture(position_texture, uv).a;
    vec3 centerNormal = texture(normal_texture, uv).rgb;
    float centerDepth = texture(normal_texture, uv).a;
    vec3 centerAlbedo = texture(albedo_texture, uv).rgb;

    vec3 filteredColor = vec3(0.0);
    float totalWeight = 0.0;

    variance = clamp(1.0 + variance * 10.0, 1.0, 10.0);

    for (int dy = -radius; dy <= radius; ++dy) {
        for (int dx = -radius; dx <= radius; ++dx) {
            vec2 offset = vec2(dx, dy);
            vec2 offset_uv = (gl_GlobalInvocationID.xy + 0.5 + offset * step_size) / resolution;

            float spatialWeight = gaussianWeight(length(offset), sigma_spatial);

            vec3 sampleColor = texture(color_texture, offset_uv).rgb;
            float colorDistance = min(length(sampleColor - centerColor), sigma_color * variance);
            float colorWeight = gaussianWeight(colorDistance, sigma_color * variance);

            float sampleDepth = texture(normal_texture, offset_uv).a;
            float depthDifference = abs(sampleDepth - centerDepth);
            float depthWeight = gaussianWeight(depthDifference, sigma_depth * variance);

            vec3 sampleNormal = texture(normal_texture, offset_uv).rgb;
            float normalDifference = length(sampleNormal - centerNormal);
            float normalWeight = gaussianWeight(normalDifference, sigma_normal * variance);

            vec3 sampleAlbedo = texture(albedo_texture, offset_uv).rgb;
            float albedoDifference = length(sampleAlbedo - centerAlbedo);
            float albedoWeight = gaussianWeight(albedoDifference, 1.0 * variance);

            float weight = spatialWeight * colorWeight * depthWeight * normalWeight * albedoWeight;

            filteredColor += sampleColor * weight;
            totalWeight += weight;
        }
    }
    imageStore(atrous_image, ivec2(gl_GlobalInvocationID.xy), vec4(vec3(filteredColor / totalWeight), 0.0));
}