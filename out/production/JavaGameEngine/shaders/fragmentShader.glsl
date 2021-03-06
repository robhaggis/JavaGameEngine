#version 400 core

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D modelTexture;
uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;


//Cell Shading
//TODO upload cell shade as a uniform value
const float cellShadeLevels = 4;	//higher results in more detailed textures i.e less distinct cell shading

void main(void){

	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	for(int i=0;i<4;i++){
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDotl = dot(unitNormal,unitLightVector);
		float brightness = max(nDotl,0.0);

		//Cell Shading
		float level = floor(brightness * cellShadeLevels);
		brightness = level / cellShadeLevels;

		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection,unitNormal);
		float specularFactor = dot(reflectedLightDirection , unitVectorToCamera);
		specularFactor = max(specularFactor,0.0);
		float dampedFactor = pow(specularFactor,shineDamper);

		//Cell shading for specular highlights
		level = floor(dampedFactor * cellShadeLevels);
		dampedFactor = level / cellShadeLevels;

		totalDiffuse = totalDiffuse + (brightness * lightColour[i])/attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i])/attFactor;
	}
	totalDiffuse = max(totalDiffuse, 0.2);  //NOTE ensures total diffuse never goes below 0 i.e. theres an ambient light level
	

	vec4 textureColour = texture(modelTexture,pass_textureCoordinates);
	if(textureColour.a < 0.5){
		discard;
	}
	out_Color =  vec4(totalDiffuse,1.0) * textureColour + vec4(totalSpecular,1.0);

	out_Color = mix(vec4(skyColour, 1.0), out_Color, visibility);

}