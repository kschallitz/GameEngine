package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
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
import terrains.TerrainManager;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.*;

/**
 * Created by Kurt on 6/25/2017.
 */
public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader(); // Loads data into VAO
        RawModel model = OBJLoader.loadObjModel("tree", loader);

        // GAME OBJECTS
        ModelData tikiHeadData = OBJFileLoader.loadOBJ("Moai1");
        RawModel tikiHeadRaw = loader.loadToVAO(tikiHeadData.getVertices(), tikiHeadData.getTextureCoords(), tikiHeadData.getNormals(),
                tikiHeadData.getIndices());
        TexturedModel tikiHead = new TexturedModel(tikiHeadRaw,
                new ModelTexture(loader.loadTexture(("SeamlessStoneTexture"))));

        tikiHead.getTexture().setHasTransparancy(true);
        tikiHead.getTexture().setUseFakeLighting(true);
        Entity tikiHeadEntity = new Entity(tikiHead, new Vector3f( 50, -0.5f, 50), 0, 0, 0, 5f);

        // GAME PIECES
        TexturedModel navigator = new TexturedModel(OBJLoader.loadObjModel("navigator", loader),
                new ModelTexture(loader.loadTexture("white")));

        Player player = new Player(navigator, new Vector3f( 5, 1.5f, 5), -90, 0, 90, .5f);

        // TERRAIN OBJECTS
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

        // *********************************** TERRAIN TEXTURE STUFF ********************************************
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        // *********************************** TERRAIN  ********************************************
        TerrainManager terrainManager = new TerrainManager();
        terrainManager.add(0, 0, new Terrain(0, 0, loader, texturePack, blendMap, "heightMap"));
        terrainManager.add(0, 1, new Terrain(1, 0, loader, texturePack, blendMap, "heightMap"));

        List<Entity> entities = new ArrayList<>();
        Random random = new Random();
        float x;
        float z;
        for (int i = 0; i < 1500; i++) {
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(tree, new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z), 0, 0, 0, random.nextFloat() * 5 - .5f));
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(lolipopTree, new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z), 0, 0, 0, random.nextFloat()* 0.5f));
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(grass, new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z), 0, 0, 0, 1));
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(flower, new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z),0, 0, 0, 1));
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(fern, new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z),  0, 0, 0, 0.6f));
        }

        ModelTexture texture = tree.getTexture();
        texture.setShineDamper(20);
        texture.setReflectivity(1);

        Light light = new Light(new Vector3f(200, 200, 100), new Vector3f(1, 1, 1));

        // This MUST be done AFTER the player is created
        Camera camera = new Camera(player);

        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            camera.move();
            int mapZ = (int)Math.floor(player.getPosition().z / Terrain.SIZE); // Row
            int mapX = (int)Math.floor(player.getPosition().x / Terrain.SIZE); // Col
            Terrain terrain = terrainManager.get(mapZ, mapX);

            player.move(terrain);

            // Render Terrains around player
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrainManager.get(mapZ- 1, mapX));
            renderer.processTerrain(terrainManager.get(mapZ + 1, mapX));
            renderer.processTerrain(terrainManager.get(mapZ, mapX - 1));
            renderer.processTerrain(terrainManager.get(mapZ, mapX + 1));
            renderer.processTerrain(terrainManager.get(mapZ + 1, mapX + 1));
            renderer.processTerrain(terrainManager.get(mapZ + 1, mapX - 1));
            renderer.processTerrain(terrainManager.get(mapZ - 1, mapX + 1));
            renderer.processTerrain(terrainManager.get(mapZ - 1, mapX - 1));

            // Game pieces
            renderer.processEntity(player);
            // renderer.processEntity(new Entity(navigator, new Vector3f( 5, 1.5f, 5), -90, 0, 90, .5f));

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
