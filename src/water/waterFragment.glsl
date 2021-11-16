#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;

const float waveStrength = 0.02;
void main(void) {

	vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0+0.5;
	vec2 refractTexCoords  = vec2(ndc.x, ndc.y);
	vec2 relectTexCoords = vec2(ndc.x, -ndc.y);

	vec2 distortion1 = (texture(dudvMap, vec2(textureCoords.x, textureCoords.y)).rg *2.0 - 1.0)*waveStrength;

	refractTexCoords+= distortion1;
	refractTexCoords = clamp(refractTexCoords,0.001, 0.999);

	relectTexCoords+=distortion1;
	relectTexCoords.x = clamp(relectTexCoords.x,0.001, 0.999);
	relectTexCoords.y = clamp(relectTexCoords.y,-0.999, -0.001);

	vec4 reflectColour = texture(reflectionTexture, relectTexCoords);
	vec4 refractColour = texture(refractionTexture, refractTexCoords);
	out_Color = mix(reflectColour, refractColour, 0.5);

}