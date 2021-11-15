#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;
out vec3 toLightVector[4];		//NOTE Number is max amount of lights that can affect an entity at once
out vec3 toCameraVector;
out float visibility;


uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];	//NOTE same number as toLightVector

uniform float useFakeLighting;

uniform float numberOfRows;
uniform vec2 offset;

//TODO Move to java code so fog can be set dynamically
//NOTE HEAVY FOG
//const float fogDensity = 0.007;
//const float fogGradient = 1.5;

//NOTE Light Haze
const float fogDensity = 0.0035;
const float fogGradient = 5.0;

void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	vec4 positionRelativetoCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativetoCam;
	pass_textureCoordinates = (textureCoordinates / numberOfRows)+offset;

	vec3 actualNormal = normal;
	if(useFakeLighting > 0.5){
		actualNormal = vec3(0.0,1.0,0.0);
	}
	
	surfaceNormal = (transformationMatrix * vec4(actualNormal,0.0)).xyz;

	for(int i=0;i<4;i++){	//NOTE same number of loops as length of toLightVector
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}

	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;

	float distance = length(positionRelativetoCam.xyz);
	visibility = exp(-pow((distance*fogDensity),fogGradient));
	visibility = clamp(visibility,0.0,1.0);
	
	
}