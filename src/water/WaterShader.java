package water;

import org.lwjgl.examples.spaceinvaders.Texture;
import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;
import toolbox.Maths;
import entities.Camera;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/water/waterVertex.glsl";
	private final static String FRAGMENT_FILE = "src/water/waterFragment.glsl";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectTexture;
	private int location_refractTexture;
	private int location_dudvMap;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectTexture = getUniformLocation("reflectionTexture");
		location_refractTexture = getUniformLocation("refractionTexture");
		location_dudvMap = getUniformLocation("dudvMap");

	}

	public void connectTextureUnits(){
		super.loadInt(location_reflectTexture, 0);
		super.loadInt(location_refractTexture, 1);
		super.loadInt(location_dudvMap, 2);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
