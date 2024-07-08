#version 460 core

out vec3 out_color;

uniform float gamma, exposure;
uniform sampler2D frame_texture;
uniform vec2 resolution;

vec3 post_process(vec3 col) {
    if (exposure != 0.0) col = vec3(1.0) - exp(-col * exposure);
    if (gamma != 0.0) col = pow(col, vec3(1.0 / gamma));
    return col;
}

void main() {
    vec3 col = texture(frame_texture, gl_FragCoord.xy / resolution).rgb;
    out_color = post_process(col);
}