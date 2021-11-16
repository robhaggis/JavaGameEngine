#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCamVector;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;

uniform float moveFactor;

const float waveStrength = 0.01;
void main(void) {

	vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0+0.5;
	vec2 refractTexCoords  = vec2(ndc.x, ndc.y);
	vec2 relectTexCoords = vec2(ndc.x, -ndc.y);

	vec2 distortion1 = (texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg *2.0 - 1.0)*waveStrength;
	vec2 distortion2 = (texture(dudvMap, vec2(-textureCoords.x + moveFactor, textureCoords.y+moveFactor)).rg *2.0 - 1.0)*waveStrength;
	vec2 totalDistortion = distortion1 + distortion2;

	refractTexCoords+= totalDistortion;
	refractTexCoords = clamp(refractTexCoords,0.001, 0.999);

	relectTexCoords+=totalDistortion;
	relectTexCoords.x = clamp(relectTexCoords.x,0.001, 0.999);
	relectTexCoords.y = clamp(relectTexCoords.y,-0.999, -0.001);

	vec4 reflectColour = texture(reflectionTexture, relectTexCoords);
	vec4 refractColour = texture(refractionTexture, refractTexCoords);


	vec3 viewVector = normalize(toCamVector);
	float refractiveFactor = dot(viewVector, vec3(0.0,1.0,0.0));
	refractiveFactor = pow(refractiveFactor,2);

	out_Color = mix(reflectColour, refractColour, refractiveFactor);

	//NOTE Blue Tint to water
	out_Color = mix(out_Color, vec4(0.0,0.3,0.5,1.0), 0.2);

}