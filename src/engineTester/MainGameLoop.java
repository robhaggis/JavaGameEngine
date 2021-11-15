package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	public static void main(String[] args) {

		DisplayManager.createDisplay();
		Loader loader = new Loader();

		//Player Character
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel bunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));
		Player player = new Player(bunny, new Vector3f(100,0,-50),0,180,0, 0.6f);

		//*********************TERRAIN****************************
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mossPath256"));

		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

		//Environment stuff
		Light light = new Light(new Vector3f(20000,40000,2000),new Vector3f(1,1,1));
		Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap, "heightmap");
		Camera camera = new Camera(player);


		//*********************MODELS****************************

		RawModel model = OBJLoader.loadObjModel("tree", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel",loader),new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel",loader),new ModelTexture(loader.loadTexture("flower")));
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern",loader),new ModelTexture(loader.loadTexture("fern")));
		TexturedModel lowPolyTree = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree",loader),new ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel box = new TexturedModel(OBJLoader.loadObjModel("box",loader),new ModelTexture(loader.loadTexture("box")));
		TexturedModel box2 = new TexturedModel(OBJLoader.loadObjModel("box",loader),new ModelTexture(loader.loadTexture("box")));

		//Fake lighting for entities with transparency
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		fern.getTexture().setHasTransparency(true);

		List<Entity> entities = new ArrayList<Entity>();
		Random random = new Random(123456);
		for(int i=0;i<400;i++){
			float x,y,z;
			if(i%20==0){
				x = random.nextFloat() * 800-400;
				z = random.nextFloat() *-600;
				y = terrain.getHeightOfTerrain(x,z);
				//entities.add(new Entity(grass, new Vector3f(x ,y,z),0,0,0,0.9f));
				//entities.add(new Entity(flower, new Vector3f(x,y,z),0,0,0,2.3f));
				entities.add(new Entity(fern, new Vector3f(x,y,z),0,random.nextFloat()*360,0,0.9f));
			}
			if(i%5==0){
				x = random.nextFloat() * 800-400;
				z = random.nextFloat() *-600;
				y = terrain.getHeightOfTerrain(x,z);
				entities.add(new Entity(lowPolyTree, new Vector3f(x,y,z),0,random.nextFloat()*360,0,random.nextFloat()*0.1f+0.6f));


				x = random.nextFloat() * 800-400;
				z = random.nextFloat() *-600;
				y = terrain.getHeightOfTerrain(x,z);
				entities.add(new Entity(staticModel, new Vector3f(x,y,z),0,random.nextFloat()*360,0,random.nextFloat()*1+4));
			}
		}

		//****************************************************




		MasterRenderer renderer = new MasterRenderer();

		//MAIN GAME LOOP
		while(!Display.isCloseRequested()){
			camera.move();
			player.move(terrain);
			renderer.processEntity(player);
			
			renderer.processTerrain(terrain);
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
