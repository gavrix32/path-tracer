#version 460 core

layout (location = 0) in vec3 vertices;

void main() {
    gl_Position = vec4(vertices, 1);
}