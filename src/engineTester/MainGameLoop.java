package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
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

    private static TerrainTexturePack texturePack;
    private static TerrainTexture blendMap;
    private static TerrainManager terrainManager;
    private static Loader loader;
    private static Entity tikiHeadEntity;
    private static Player player;
    private static TexturedModel tree;
    private static TexturedModel lolipopTree;
    private static TexturedModel lamp;
    private static TexturedModel grass;
    private static TexturedModel flower;
    private static TexturedModel fern;
    private static List<Entity> entities;
    private static List<Light> lights;
    //private static List<GuiTexture> guis;
    private static HashMap<GuiTexture, Long> mapGUIs = new HashMap<>();

    public static void main(String[] args) {

        DisplayManager.createDisplay();
        // Loads data into VAO
        loader = new Loader();
        entities = new ArrayList<>();

        loadGameObjects();
        loadGamePieces();

        loadTerrainObjects();
        loadTerrainTextures();
        loadTerrain();
        placeTerrainObjects();

        createLightSource();
        addLamps();

        loadGUIs();

        // This MUST be done AFTER the player is created
        Camera camera = new Camera(player);

        MasterRenderer renderer = new MasterRenderer(loader);
        GuiRenderer guiRenderer = new GuiRenderer(loader);

        while (!Display.isCloseRequested()) {
            camera.move();

            int mapZ = (int) Math.floor(player.getPosition().z / Terrain.SIZE); // Row
            int mapX = (int) Math.floor(player.getPosition().x / Terrain.SIZE); // Col
            Terrain terrain = terrainManager.get(mapZ, mapX);

            player.move(terrain);

            // Render Terrains around player
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrainManager.get(mapZ - 1, mapX));
            renderer.processTerrain(terrainManager.get(mapZ + 1, mapX));
            renderer.processTerrain(terrainManager.get(mapZ, mapX - 1));
            renderer.processTerrain(terrainManager.get(mapZ, mapX + 1));
            renderer.processTerrain(terrainManager.get(mapZ + 1, mapX + 1));
            renderer.processTerrain(terrainManager.get(mapZ + 1, mapX - 1));
            renderer.processTerrain(terrainManager.get(mapZ - 1, mapX + 1));
            renderer.processTerrain(terrainManager.get(mapZ - 1, mapX - 1));

            // Game pieces
            renderer.processEntity(player);

            // Game Objects
            tikiHeadEntity.increaseRotation(0, 1, 0);

            renderer.processEntity(tikiHeadEntity);

            // Terrain Entities
            for (Entity entity : entities) {
                renderer.processEntity(entity);
            }

            // RENDER ALL ITEMS
            renderer.render(lights, camera);
            manageGUIs();
            guiRenderer.render(mapGUIs.keySet());

            DisplayManager.updateDisplay();
        }

        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

    /**
     * Handle GUI objects, removing them from our list when their lifetime is up.
     */
    private static void manageGUIs() {
        Iterator it = mapGUIs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if ((Long)pair.getValue() < System.currentTimeMillis()) {
                it.remove();
            }
        }
    }


    private static void loadGUIs() {
        // Introduce the GUIs
        GuiTexture tobagoLogoGUI = new GuiTexture(loader.loadTexture("tabago1"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        mapGUIs.put(tobagoLogoGUI, System.currentTimeMillis() + 5000L);
    }

    private static void addLamps() {
        // Add lamps
        entities.add(new Entity(lamp, new Vector3f(185, terrainManager.getHeightAtPosition(185, 293), 293), 0, 0, 0, 1));
        //entities.add(new Entity(lamp, new Vector3f(185, -4.7f, 293), 0, 0, 0, 1));
        entities.add(new Entity(lamp, new Vector3f(370, terrainManager.getHeightAtPosition(370, 300), 300), 0, 0, 0, 1));
        //entities.add(new Entity(lamp, new Vector3f(370, 4.2f, 300), 0, 0, 0, 1));
        entities.add(new Entity(lamp, new Vector3f(293, terrainManager.getHeightAtPosition(293, 305), 305), 0, 0, 0, 1));
        //entities.add(new Entity(lamp, new Vector3f(293, -6.8f, 305), 0, 0, 0, 1));
    }

    private static void createLightSource() {
        // Create the light source for the world - let there be light!
        lights = new ArrayList<>();
        lights.add(new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.4f, 0.4f, 0.4f)));
        lights.add(new Light(new Vector3f(185, terrainManager.getHeightAtPosition(185, 293) + 17, 293),   new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(370, terrainManager.getHeightAtPosition(370, 300) + 17, 300),   new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(293, terrainManager.getHeightAtPosition(293, 305) + 17, 305),    new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
    }

    private static void placeTerrainObjects() {
        Random random = new Random();
        float x;
        float z;
        for (int i = 0; i < 1500; i++) {
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(tree, new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z), 0, 0, 0, random.nextFloat() * 5 - .5f));
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(lolipopTree, new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z), 0, 0, 0, random.nextFloat() * 0.5f));
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(grass, new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z), 0, 0, 0, 1));
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(flower, new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z), 0, 0, 0, 1));
            x = random.nextFloat() * 1600;
            z = random.nextFloat() * 800;
            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, terrainManager.getHeightAtPosition(x, z), z), 0, 0, 0, 0.6f));
        }

        ModelTexture texture = tree.getTexture();
        texture.setShineDamper(20);
        texture.setReflectivity(1);
    }

    private static void loadTerrain() {
        // *********************************** TERRAIN  ********************************************
        terrainManager = new TerrainManager();
        terrainManager.add(0, 0, new Terrain(0, 0, loader, texturePack, blendMap, "heightMap"));
        terrainManager.add(0, 1, new Terrain(1, 0, loader, texturePack, blendMap, "heightMap"));

    }

    private static void loadTerrainTextures() {
        // *********************************** TERRAIN TEXTURE STUFF ********************************************
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mud"));

        texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
    }

    private static void loadTerrainObjects() {
        // TERRAIN OBJECTS
        RawModel model = OBJLoader.loadObjModel("tree", loader);
        tree = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));

        lolipopTree = new TexturedModel(OBJLoader.loadObjModel("lowPolyTree", loader),
                new ModelTexture(loader.loadTexture("lowPolyTree")));

        grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHasTransparancy(true);
        grass.getTexture().setUseFakeLighting(true);

        flower = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("flower")));
        flower.getTexture().setHasTransparancy(true);
        flower.getTexture().setUseFakeLighting(true);

        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRowsInAtlas(2);

        fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), fernTextureAtlas);
        fern.getTexture().setHasTransparancy(true);
        fern.getTexture().setUseFakeLighting(true);

        lamp = new TexturedModel(OBJLoader.loadObjModel("lamp", loader),
                new ModelTexture(loader.loadTexture("lamp")));
    }

    private static void loadGamePieces() {
        TexturedModel navigator = new TexturedModel(OBJLoader.loadObjModel("navigator", loader),
                new ModelTexture(loader.loadTexture("white")));

        //Player player = new Player(navigator, new Vector3f(5, 1.5f, 5), -90, 0, 90, .5f);
        player = new Player(navigator, new Vector3f(370, 10f, 300), -90, 0, 90, .5f);

    }

    private static void loadGameObjects() {
        ModelData tikiHeadData = OBJFileLoader.loadOBJ("Moai1");
        RawModel tikiHeadRaw = loader.loadToVAO(
                tikiHeadData.getVertices(),
                tikiHeadData.getTextureCoords(),
                tikiHeadData.getNormals(),
                tikiHeadData.getIndices());
        TexturedModel tikiHead = new TexturedModel(tikiHeadRaw,
                new ModelTexture(loader.loadTexture(("SeamlessStoneTexture"))));

        tikiHead.getTexture().setHasTransparancy(false);
        tikiHead.getTexture().setUseFakeLighting(true);
        tikiHeadEntity = new Entity(tikiHead, new Vector3f(50, -0.5f, 50), 0, 0, 0, 5f);
    }
}
