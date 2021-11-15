package skybox;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;

import org.lwjgl.util.vector.Vector3f;
import shaders.ShaderProgram;
import toolbox.Maths;

public class SkyboxShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/skybox/skyboxVertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/skybox/skyboxFragmentShader.glsl";

    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColour;

    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadFogColour(float r, float g, float b){
        super.load3DVector(location_fogColour, new Vector3f(r,g,b));
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        //NOTE Nullify last column of view matrix before loading to stop skybox translating but keep rotation
        matrix.m30 = 0;
        matrix.m31 = 0;
        matrix.m32 = 0;
        super.loadMatrix(location_viewMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColour = super.getUniformLocation("fogColour");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}