#version 460 core

out vec3 out_color;

uniform sampler2D frame_texture;
uniform vec2 resolution;

vec3 post_process(vec3 col) {
    float exposure = 1.0;
    float gamma = 2.2;
    col = vec3(1.0) - exp(-col * exposure);
    col = pow(col, vec3(1.0 / gamma));
    return col;
}

void main() {
    vec3 col = texture(frame_texture, gl_FragCoord.xy / resolution).rgb;
    out_color = post_process(col);
}