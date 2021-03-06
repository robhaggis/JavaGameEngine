package shaders;

import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import toolbox.Maths;

import java.util.List;

public class TerrainShader extends ShaderProgram{

	//NOTE MAX LIGHTS THAT CAN AFFECT AN OBJECT AT ONE TIME
	//IF THIS VALUE IS CHANGED< MAX SURE IT IS UPDATED IN ALL LIGHTING CALCULATIONS
	//IN THE TERRAINSHADER.GLSL FILE. DON'T FORGET TO UPDATE THE LOOPS AS WELL
	private static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = "src/shaders/terrainVertexShader.glsl";
	private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader.glsl";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int[] location_lightColour;
	private int[] location_attenuation;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColour;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_clippingPlane;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColour = super.getUniformLocation("skyColour");
		location_backgroundTexture = super.getUniformLocation("backgroundTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("bTexture");
		location_blendMap = super.getUniformLocation("blendMap");
		location_lightColour = new int[MAX_LIGHTS];
		location_lightPosition = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		location_clippingPlane = super.getUniformLocation("clippingPlane");

		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}

	}

	public void connectTextureUnits(){
		super.loadInt(location_backgroundTexture, 0);
		super.loadInt(location_rTexture,1);
		super.loadInt(location_gTexture,2);
		super.loadInt(location_bTexture,3);
		super.loadInt(location_blendMap,4);
	}

	public void loadSkyColour(float r, float g, float b){
		super.load3DVector(location_skyColour, new Vector3f(r,g,b));
	}

	public void loadClippingPlane(Vector4f clipPlane){
		super.load4DVector(location_clippingPlane, clipPlane);
	}
	
	public void loadShineVariables(float damper,float reflectivity){
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadLights(List<Light> lights){
		for(int i=0;i<MAX_LIGHTS;i++){
			if(i<lights.size()) {
				super.load3DVector(location_lightPosition[i], lights.get(i).getPosition());
				super.load3DVector(location_lightColour[i], lights.get(i).getColour());
				super.load3DVector(location_attenuation[i], lights.get(i).getAttenuation());
			}else{
				super.load3DVector(location_lightPosition[i], new Vector3f(0,0,0));
				super.load3DVector(location_lightColour[i],  new Vector3f(0,0,0));
				super.load3DVector(location_attenuation[i], new Vector3f(1,0,0));	//NOTE 1,0,0 = no attenuation
			}
		}

	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
	
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	

}
