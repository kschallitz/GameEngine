package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.newdawn.slick.opengl.Texture;
import shaders.StaticShader;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kurt on 6/28/2017.
 */
public class MasterRenderer {
    private StaticShader shader = new StaticShader();
    private Renderer renderer = new Renderer(shader);

    /**
     * A HashMap that will store the texture models as the keys and the entities that use the given key as a value list
     */
    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    /**
     * Render the game!
     *
     * @param sun - The light source object
     * @param camera - The camera object
     */
    public void render(Light sun, Camera camera) {
        renderer.prepare();
        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        entities.clear();
    }

    /**
     * Clean up the shaders when the game is terminated
     */
    public void cleanUp() {
        shader.cleanUp();
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        }
        else
        {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }
}
