#version 330 core

in vec2 texCoord;

uniform sampler2D materialKdTexture;

out vec4 fragColor;

void main()
{
    fragColor = texture(materialKdTexture,texCoord);
}