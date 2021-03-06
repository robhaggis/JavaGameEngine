#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform vec4 clippingPlane;

//TODO Move to java code so fog can be set dynamically
//NOTE HEAVY FOG
//const float fogDensity = 0.007;
//const float fogGradient = 1.5;

//NOTE Light Haze
//const float fogDensity = 0.0035;
//const float fogGradient = 5.0;

//NOTE No Fog
const float fogDensity = 0;
const float fogGradient = 5.0;



void main(void){
	vec4 worldPosition = transformationMatrix * vec4(position,1.0);
	gl_ClipDistance[0] = dot(worldPosition,clippingPlane);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_textureCoordinates = textureCoordinates;
	
	surfaceNormal = (transformationMatrix * vec4(normal,0.0)).xyz;
	for(int i=0;i<4;i++){
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}

	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCamera.xyz);
	visibility = exp(-pow((distance*fogDensity),fogGradient));
	visibility = clamp(visibility,0.0,1.0);
}