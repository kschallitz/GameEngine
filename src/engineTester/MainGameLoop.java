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
import java.util.List;
import java.util.Random;

/**
 * Created by Kurt on 6/25/2017.
 */
public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader(); // Loads data into VAO
        RawModel model = OBJLoader.loadObjModel("tree", loader);

        // GAME OBJECTS
        TexturedModel tikiHead = new TexturedModel(OBJLoader.loadObjModel("Moai1", loader),
                new ModelTexture(loader.loadTexture("SeamlessStoneTexture")));
        tikiHead.getTexture().setHasTransparancy(true);
        tikiHead.getTexture().setUseFakeLighting(true);
        Entity tikiHeadEntity = new Entity(tikiHead, new Vector3f( 50, -0.5f, 50), 0, 0, 0, 5f);

        // GAME PIECES
        TexturedModel navigator = new TexturedModel(OBJLoader.loadObjModel("navigator", loader),
                new ModelTexture(loader.loadTexture("white")));

        // TERRAIN
        TexturedModel tree = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));

        TexturedModel lolipopTree = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),
                new ModelTexture(loader.loadTexture("lowPolyTree")));

        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHasTransparancy(true);
        grass.getTexture().setUseFakeLighting(true);

        TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("flower")));
        flower.getTexture().setHasTransparancy(true);
        flower.getTexture().setUseFakeLighting(true);

        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),
                new ModelTexture(loader.loadTexture("fern")));
        fern.getTexture().setHasTransparancy(true);
        fern.getTexture().setUseFakeLighting(true);

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 1500; i++) {

            entities.add(new Entity(tree, new Vector3f(random.nextFloat() * 1600, 0,
                    random.nextFloat() * 800), 0, 0, 0, random.nextFloat() * 5 - .5f));
            entities.add(new Entity(lolipopTree, new Vector3f(random.nextFloat() * 1600, 0,
                    random.nextFloat() * 800), 0, 0, 0, random.nextFloat()* 0.5f));
            entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 1600 + 10f, 0,
                    random.nextFloat() * 800), 0, 0, 0, 1));
            entities.add(new Entity(flower, new Vector3f(random.nextFloat() * 1600 + 10f, 0,
                    random.nextFloat() * 800), 0, 0, 0, 1));
            entities.add(new Entity(fern, new Vector3f(random.nextFloat() * 1600, 0,
                    random.nextFloat() * 800), 0, 0, 0, 0.6f));
        }

        ModelTexture texture = tree.getTexture();
        texture.setShineDamper(20);
        texture.setReflectivity(1);

        Light light = new Light(new Vector3f(200, 200, 100), new Vector3f(1, 1, 1));
        Terrain terrain1 = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(1, 0, loader, new ModelTexture(loader.loadTexture("grass")));

        Camera camera = new Camera();

        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            camera.move();
            renderer.processTerrain(terrain1);
            renderer.processTerrain(terrain2);

            // Game pieces
            renderer.processEntity(new Entity(navigator, new Vector3f( 5, 1.5f, 5), -90, 0, 90, .5f));

            // Game Objects
            tikiHeadEntity.increaseRotation(0, 1, 0);

            renderer.processEntity(tikiHeadEntity);

            // Terrain
            for (Entity entity : entities) {
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
