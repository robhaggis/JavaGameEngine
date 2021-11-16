#version 400 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCamVector;
in vec3 fromLightVector;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform vec3 lightColour;

uniform float moveFactor;

const float waveStrength = 0.03;
const float shineDamper = 20.0;
const float reflectivity = 0.6;
const float waterEdgeCutoff = 5.0f;	//Higher values = water gets softer at edges
const float waterChopValue = 3.0f; //Higher values = smoother, less choppy water

void main(void) {

	vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0+0.5;
	vec2 refractTexCoords  = vec2(ndc.x, ndc.y);
	vec2 relectTexCoords = vec2(ndc.x, -ndc.y);


	//NOTE CHANGE NEAR AND FAR PLANE VALUES IN MASTERRENDERER.JAVA IF YOU CHANGE THESE
	//TODO UPLOAD TO SHADER AS UNIFORM VARIABLES
	float near = 0.1;
	float far = 1000.0;

	float depth = texture(depthMap, refractTexCoords).r;
	float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
	depth = gl_FragCoord.z;
	float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));

	//Depth for refraction detail
	float waterDepth = floorDistance - waterDistance;

	//Distortion with DUDV Map
	vec2 distortedTexCoords = texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg*0.1;
	distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+moveFactor);
	vec2 totalDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth*4.0/waterEdgeCutoff,0.0,1.0);
	refractTexCoords+= totalDistortion;
	refractTexCoords = clamp(refractTexCoords,0.001, 0.999);
	relectTexCoords+=totalDistortion;
	relectTexCoords.x = clamp(relectTexCoords.x,0.001, 0.999);
	relectTexCoords.y = clamp(relectTexCoords.y,-0.999, -0.001);

	//Calculating reflect and refract colours
	vec4 reflectColour = texture(reflectionTexture, relectTexCoords);
	vec4 refractColour = texture(refractionTexture, refractTexCoords);

	//Caluclating water surface normals after distortion
	vec4 normalMapColour = texture(normalMap, distortedTexCoords);
	vec3 normal = vec3(normalMapColour.r * 2.0-1.0, normalMapColour.b * waterChopValue, normalMapColour.g *2.0-1.0);
	normal = normalize(normal);

	//Fresnel reflectivity calculations
	vec3 viewVector = normalize(toCamVector);
	float refractiveFactor = dot(viewVector, normal);
	//refractiveFactor = pow(refractiveFactor,2);

	//Normal / Specular highlights
	vec3 reflectedLight = reflect(normalize(fromLightVector), normal);
	float specular = max(dot(reflectedLight, viewVector), 0.0);
	specular = pow(specular, shineDamper);
	vec3 specularHighlights = lightColour * specular * reflectivity * clamp(waterDepth/waterEdgeCutoff,0.0,1.0);

	//Final Colour Tweaks
	out_Color = mix(reflectColour, refractColour, refractiveFactor);
	//NOTE Blue Tint to water
	out_Color = mix(out_Color, vec4(0.0,0.3,0.5,1.0), 0.2) + vec4(specularHighlights, 0.0);
	out_Color.a = clamp(waterDepth/waterEdgeCutoff,0.0,1.0);



}