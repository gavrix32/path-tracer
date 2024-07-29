#version 460 core

out vec3 out_color;

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
    out_color = post_process(color);
}