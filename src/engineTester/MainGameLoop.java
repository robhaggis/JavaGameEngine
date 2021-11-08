package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;

import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import models.RawModel;
import shaders.StaticShader;
import textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;

public class MainGameLoop {

    public static void main(String[] args) {
        DisplayManager.createDisplay();
        Loader loader = new Loader();

        RawModel model = OBJLoader.loadObjMOdel("stall", loader);
        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("stallTexture")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10f);
        texture.setReflectivity(1.0f);

        List<Entity> allDragons = new ArrayList<Entity>();

        Entity entity1 = new Entity(staticModel, new Vector3f(0,-5,-25),0,180,0,1);
        allDragons.add(entity1);

        Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1));
        Camera camera = new Camera();

        MasterRenderer renderer = new MasterRenderer();

        while (!Display.isCloseRequested()) {
            camera.move();

            for(Entity dragon : allDragons){
                renderer.processEntity(dragon);
            }

            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanup();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

}
