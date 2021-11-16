#version 400 core

in vec4 clipSpace;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;

void main(void) {

	vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0+0.5;
	vec2 refractTexCoords  = vec2(ndc.x, ndc.y);
	vec2 relectTexCoords = vec2(ndc.x, -ndc.y);
	vec4 reflectColour = texture(reflectionTexture, relectTexCoords);
	vec4 refractColour = texture(refractionTexture, refractTexCoords);
	out_Color = mix(reflectColour, refractColour, 0.5);

}