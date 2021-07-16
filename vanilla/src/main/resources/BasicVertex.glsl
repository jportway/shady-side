#version 330 core

layout (location = 0) in vec3 inPosition; // the position variable has attribute position 0
layout (location = 1) in vec3 inNormal;
layout (location = 2) in vec2 texPosition;

struct World {
    //case class WorldData(light: Vec3[Float], eye: Vec3[Float], view: Matrix4[Float], projection: Matrix4[Float]) derives ShaderUniformLoadable
    vec3 light;
    vec3 eye;
    mat4 view;
    mat4 projection;
};

uniform World world;
uniform mat4 modelMatrix;

out vec2 texCoord;


void main()
{
    gl_Position = world.projection * world.view * modelMatrix * vec4(inPosition, 1.0); // see how we directly give a vec3 to vec4's constructor
    texCoord = texPosition;
}