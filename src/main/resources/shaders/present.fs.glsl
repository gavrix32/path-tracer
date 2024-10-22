#version 460 core

out vec3 out_color;

layout(binding = 3, rgba32f) uniform image2D color_image;
uniform sampler2D color_texture;
uniform vec2 resolution;
uniform float gamma, exposure;

vec3 post_process(vec3 col) {
    if (exposure != 0.0) col = vec3(1.0) - exp(-col * exposure);
    if (gamma != 0.0) col = pow(col, vec3(1.0 / gamma));
    return col;
}

void main() {
    vec3 color = texture(color_texture, gl_FragCoord.xy / resolution).rgb;
    //vec3 color = imageLoad(color_image, ivec2(gl_FragCoord.xy)).rgb;
    out_color = post_process(color);
}