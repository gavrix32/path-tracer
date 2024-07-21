#version 460 core

out vec3 out_color;

uniform sampler2D color_texture, normal_texture, position_texture, albedo_texture;
uniform vec2 resolution;
uniform float stepWidth, c_phi, n_phi, p_phi;

void main() {
    vec2 offset[25];
    int counter = 0;
    for (int i = -2; i <= 2; i++) {
        for (int j = -2; j <= 2; j++) {
            offset[counter] = vec2(j, i);
            counter++;
        }
    }

    float kernel[25];
    kernel[0] = 1.0f/256.0f;
    kernel[1] = 1.0f/64.0f;
    kernel[2] = 3.0f/128.0f;
    kernel[3] = 1.0f/64.0f;
    kernel[4] = 1.0f/256.0f;

    kernel[5] = 1.0f/64.0f;
    kernel[6] = 1.0f/16.0f;
    kernel[7] = 3.0f/32.0f;
    kernel[8] = 1.0f/16.0f;
    kernel[9] = 1.0f/64.0f;

    kernel[10] = 3.0f/128.0f;
    kernel[11] = 3.0f/32.0f;
    kernel[12] = 9.0f/64.0f;
    kernel[13] = 3.0f/32.0f;
    kernel[14] = 3.0f/128.0f;

    kernel[15] = 1.0f/64.0f;
    kernel[16] = 1.0f/16.0f;
    kernel[17] = 3.0f/32.0f;
    kernel[18] = 1.0f/16.0f;
    kernel[19] = 1.0f/64.0f;

    kernel[20] = 1.0f/256.0f;
    kernel[21] = 1.0f/64.0f;
    kernel[22] = 3.0f/128.0f;
    kernel[23] = 1.0f/64.0f;
    kernel[24] = 1.0f/256.0f;

    vec3 sum;
    vec3 cval = texture(color_texture, gl_FragCoord.xy / resolution).rgb;
    vec3 nval = texture(normal_texture, gl_FragCoord.xy / resolution).rgb;
    vec3 pval = texture(position_texture, gl_FragCoord.xy / resolution).rgb;
    vec3 aval = texture(albedo_texture, gl_FragCoord.xy / resolution).rgb;
    float cum_w;
    for (int i = 0; i < 25; i++) {
        vec2 uv = (gl_FragCoord.xy + offset[i] * stepWidth) / resolution;

        vec3 ctmp = texture(color_texture, uv).rgb;
        vec3 t = cval - ctmp;
        float dist2 = dot(t, t);
        float c_w = min(exp(-dist2 / c_phi), 1.0);

        vec3 ntmp = texture(normal_texture, uv).rgb;
        t = nval - ntmp;
        dist2 = max(dot(t, t) / stepWidth * stepWidth, 0.0);
        float n_w = min(exp(-dist2 / n_phi), 1.0);

        vec3 ptmp = texture(position_texture, uv).rgb;
        t = pval - ptmp;
        dist2 = dot(t, t);
        float p_w = min(exp(-(dist2) / p_phi), 1.0);

        vec3 atmp = texture(albedo_texture, uv).rgb;
        t = aval - atmp;
        dist2 = max(dot(t, t), 0.0);
        float a_w = min(exp(-dist2 / 0.5), 1.0);

        float weight = c_w * n_w * p_w * a_w;
        sum += ctmp * weight * kernel[i];
        cum_w += weight * kernel[i];
    }
    out_color = sum / cum_w;
}