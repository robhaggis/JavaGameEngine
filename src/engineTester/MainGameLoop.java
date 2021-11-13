package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;

import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		//Simple Tree
		ModelData tree = OBJFileLoader.loadOBJ("tree");
		RawModel treeModel = loader.loadToVAO(tree.getVertices(), tree.getTextureCoords(), tree.getNormals(), tree.getIndices());
		TexturedModel T_treeModel = new TexturedModel(OBJLoader.loadObjModel("tree", loader), new ModelTexture(loader.loadTexture("tree")));

		//Low Poly Round Tree
		ModelData tree2 = OBJFileLoader.loadOBJ("lowPolyTree");
		RawModel tree2Model = loader.loadToVAO(tree2.getVertices(), tree2.getTextureCoords(), tree2.getNormals(), tree2.getIndices());
		TexturedModel T_tree2Model = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader), new ModelTexture(loader.loadTexture("lowPolyTree")));

		//Ferns
		ModelData fern = OBJFileLoader.loadOBJ("fern");
		RawModel fernModel = loader.loadToVAO(fern.getVertices(), fern.getTextureCoords(), fern.getNormals(), fern.getIndices());
		TexturedModel T_fernModel= new TexturedModel(OBJLoader.loadObjModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
		T_fernModel.getTexture().setUseFakeLighting(true);
		T_fernModel.getTexture().setHasTransparency(true);

		//Grass
		ModelData grass = OBJFileLoader.loadOBJ("grass");
		RawModel grassModel = loader.loadToVAO(grass.getVertices(), grass.getTextureCoords(), grass.getNormals(), grass.getIndices());
		TexturedModel T_grassModel= new TexturedModel(OBJLoader.loadObjModel("grass", loader), new ModelTexture(loader.loadTexture("grassTexture")));
		T_grassModel.getTexture().setUseFakeLighting(true);
		T_grassModel.getTexture().setHasTransparency(true);


		
		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random();
		for(int i=0;i<500;i++){
			entities.add(new Entity(T_treeModel, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
			entities.add(new Entity(T_tree2Model, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,random.nextFloat()*1.5f-0.9f));
			entities.add(new Entity(T_fernModel, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,random.nextFloat()*1f-0.3f));
			entities.add(new Entity(T_grassModel, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,random.nextFloat()*2f-0.3f));
		}
		
		Light light = new Light(new Vector3f(20000,20000,2000),new Vector3f(1,1,1));
		
		Terrain terrain = new Terrain(0,-1,loader,new ModelTexture(loader.loadTexture("grass")));
		Terrain terrain2 = new Terrain(-1,-1,loader,new ModelTexture(loader.loadTexture("grass")));
		
		Camera camera = new Camera();	
		MasterRenderer renderer = new MasterRenderer();
		
		while(!Display.isCloseRequested()){
			camera.move();
			
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			for(Entity entity:entities){
				renderer.processEntity(entity);
			}
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
