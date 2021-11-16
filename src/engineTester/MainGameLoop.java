package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GUIRenderer;
import guis.GuiTexture;
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
import toolbox.MousePicker;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	public static void main(String[] args) {

		//*********************WINDOW****************************
		DisplayManager.createDisplay();

		//*********************ENGINE****************************
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer(loader);

		//*********************TERRAIN****************************
		List<Terrain> terrains = new ArrayList<Terrain>();

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mossPath256"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		Terrain terrain = new Terrain(0,-1,loader,texturePack,blendMap, "heightmapLake");

		terrains.add(terrain);

		//*********************PLAYER****************************
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
		TexturedModel bunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));
		Player player = new Player(bunny, new Vector3f(100,0,-100),0,135,0, 0.6f);

		//*********************CAMERA****************************
		Camera camera = new Camera(player);


		//*********************ENTITIES****************************

		List<Entity> entities = new ArrayList<Entity>();
		entities.add(player);
		RawModel model = OBJLoader.loadObjModel("tree", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
		TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel",loader),new ModelTexture(loader.loadTexture("grassTexture")));
		TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel",loader),new ModelTexture(loader.loadTexture("flower")));

		TexturedModel lowPolyTree = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree",loader),new ModelTexture(loader.loadTexture("lowPolyTree")));
		TexturedModel bobbleTree = new TexturedModel(OBJLoader.loadObjModel("bobbleTree",loader),new ModelTexture(loader.loadTexture("bobbleTree")));
		TexturedModel toonRocks = new TexturedModel(OBJLoader.loadObjModel("toonRocks",loader),new ModelTexture(loader.loadTexture("toonRocks")));
		TexturedModel box = new TexturedModel(OBJLoader.loadObjModel("box",loader),new ModelTexture(loader.loadTexture("box")));
		TexturedModel box2 = new TexturedModel(OBJLoader.loadObjModel("box",loader),new ModelTexture(loader.loadTexture("box")));

		//Texture Atlases
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern",loader),fernTextureAtlas);

		//Fake lighting for entities with transparency
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		flower.getTexture().setHasTransparency(true);
		flower.getTexture().setUseFakeLighting(true);
		fern.getTexture().setHasTransparency(true);


		Random random = new Random(123456);
		for(int i=0;i<400;i++){
			float x,y,z;
			if(i%20==0){
				x = random.nextFloat() * terrain.getSize();
				z = random.nextFloat() *-terrain.getSize();
				y = terrain.getHeightOfTerrain(x,z);
				entities.add(new Entity(grass, new Vector3f(x ,y,z),0,0,0,0.9f));

				x = random.nextFloat() * terrain.getSize();
				z = random.nextFloat() *-terrain.getSize();
				y = terrain.getHeightOfTerrain(x,z);
				entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x,y,z),0,random.nextFloat()*360,0,0.9f));
			}
			if(i%10==0){
				x = random.nextFloat() * terrain.getSize();
				z = random.nextFloat() *-terrain.getSize();
				y = terrain.getHeightOfTerrain(x,z);
				entities.add(new Entity(bobbleTree, new Vector3f(x,y,z),0,random.nextFloat()*360,0,random.nextFloat()*0.1f+0.6f));


				x = random.nextFloat() * terrain.getSize();
				z = random.nextFloat() *-terrain.getSize();
				y = terrain.getHeightOfTerrain(x,z);
				entities.add(new Entity(toonRocks, new Vector3f(x,y,z),0,random.nextFloat()*360,0,1.5f));
			}
		}

		//*********************LIGHTING****************************
		//Note Can add as many lights as needed for the scene, but only #StaticShader.MAX_LIGHTS# will actually affect anything at any one time
		//TODO Sort this list based on distance to camera, as then only the most relevant lights will be used to render the scene
		List<Light> lights = new ArrayList<Light>();

		//Note: Sun
		//TODO: Set sun based on time of day
		lights.add(new Light(new Vector3f(0,10000,-7000),new Vector3f(0.4f,0.4f,0.4f)));

		//TODO Bundle lights and lamps together into new lamp entity
		Light light = new Light(new Vector3f(110,-5+15,-110), new Vector3f(2,2,0), new Vector3f(1,0.01f,0.002f));
		lights.add(light);

		TexturedModel lamp = new TexturedModel(OBJLoader.loadObjModel("lamp",loader),new ModelTexture(loader.loadTexture("lamp")));
		lamp.getTexture().setUseFakeLighting(true);

		Entity lampEntity = new Entity(lamp, new Vector3f(110,-5f,-110),0,0,0,1);
		entities.add(lampEntity);

		//*********************WATER****************************
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix());
		List<WaterTile> waters = new ArrayList<WaterTile>();
		waters.add(new WaterTile(125,-75,-5));

		WaterFrameBuffers fbos = new WaterFrameBuffers();


		//*********************GUI****************************
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture healthPanel = new GuiTexture(loader.loadTexture("hpbar"), new Vector2f(-0.7f, -0.9f), new Vector2f(0.4f,0.5f));
		guis.add(healthPanel);
		GUIRenderer guiRenderer = new GUIRenderer(loader);
		MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain);
		GuiTexture reflectPanel = new GuiTexture(fbos.getReflectionTexture(), new Vector2f(-0.5f,0.5f), new Vector2f(0.5f, 0.5f));
		//guis.add(reflectPanel);

		//*********************MAIN GAME LOOP****************************
		while(!Display.isCloseRequested()){
			player.move(terrain);
			camera.move();
			picker.update();

			fbos.bindReflectionFrameBuffer();
			renderer.renderScene(entities, terrains, lights, camera);
			fbos.unbindCurrentFrameBuffer();

			//Stick lamp to mouse cursor
//			Vector3f terrainPoint = picker.getCurrentTerrainPoint();
//			if(terrainPoint != null){
//				lampEntity.setPosition(terrainPoint);
//				light.setPosition(new Vector3f(terrainPoint.x, terrainPoint.y+15, terrainPoint.z));
//			}

			renderer.renderScene(entities, terrains, lights, camera);
			waterRenderer.render(waters,camera);
			guiRenderer.render(guis);
			DisplayManager.updateDisplay();
		}

		fbos.cleanUp();
		waterShader.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		guiRenderer.cleanUp();
		DisplayManager.closeDisplay();

	}
}
