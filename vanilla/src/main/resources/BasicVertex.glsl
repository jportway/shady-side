#version 330 core
layout (location = 0) in vec3 aPos; // the position variable has attribute position 0

struct World {
    //case class WorldData(light: Vec3[Float], eye: Vec3[Float], view: Matrix4[Float], projection: Matrix4[Float]) derives ShaderUniformLoadable
    vec3 light;
    vec3 eye;
    mat4 view;
    mat4 projection;
};

uniform World world;
uniform mat4 modelMatrix;
out vec4 vertexColor; // specify a color output to the fragment shader

void main()
{
    gl_Position = world.projection * world.view * modelMatrix * vec4(aPos, 1.0); // see how we directly give a vec3 to vec4's constructor
    vertexColor = vec4(0.5, 0.0, 0.0, 1.0); // set the output variable to a dark-red color
}