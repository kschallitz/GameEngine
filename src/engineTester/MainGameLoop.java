package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;

import java.util.ArrayList;

/**
 * Created by Kurt on 6/25/2017.
 */
public class MainGameLoop {
    private static final int NUM_TREES = 8000;
    private static ArrayList<Entity> trees = new ArrayList<>();

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader(); // Loads data into VAO
        RawModel model = OBJLoader.loadObjModel("tree", loader);

        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(20);
        texture.setReflectivity(1);

        for (int i = 0; i < NUM_TREES; i++) {
            //Entity entity = new Entity(staticModel, new Vector3f(25f, 0, 25), 0, 0, 0, 1);
            Entity entity = new Entity(staticModel, new Vector3f((float)Math.random() * 800f, 0, (float)Math.random() * 800f), 0, 0, 0, 1);
            trees.add(entity);
        }

        Light light = new Light(new Vector3f(200, 200, 100), new Vector3f(1, 1, 1));
        Terrain terrain1 = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(1, 0, loader, new ModelTexture(loader.loadTexture("grass")));

        Camera camera = new Camera();

        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            //entity.increasePosition(0, 0, -.1f);
            for (Entity entity : trees) {
                entity.increaseRotation(0, 1, 0);
            }
            camera.move();
            renderer.processTerrain(terrain1);
            renderer.processTerrain(terrain2);
            for (Entity entity : trees) {
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
