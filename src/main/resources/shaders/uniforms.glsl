uniform vec2 resolution;
uniform vec3 camera_position;
uniform mat4 view, rot;
uniform int samples, bounces, use_taa, use_dof, autofocus, random_noise, gamma_correction, tonemapping, frame_mixing,
            show_albedo, show_depth, show_normals, sky_has_texture, spheres_count, boxes_count, triangles_count;
uniform sampler2D sky_texture;
uniform float acc_frames, time, gamma, exposure, fov, focus_distance, defocus_blur;
uniform Sphere spheres[MAX_SPHERES];
uniform Box boxes[MAX_BOXES];
uniform Triangle triangles[MAX_TRIANGLES];
uniform Sky sky;
uniform Plane plane;