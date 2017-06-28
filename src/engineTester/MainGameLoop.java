package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import shaders.StaticShader;
import textures.ModelTexture;

/**
 * Created by Kurt on 6/25/2017.
 */
public class MainGameLoop {
    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader(); // Loads data into VAO
        RawModel model = OBJLoader.loadObjModel("dragon", loader);

        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("white")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(20);
        texture.setReflectivity(1);

        Entity entity = new Entity(staticModel, new Vector3f(0f, -5, -25), 0, 0, 0, 1);
        Light light = new Light(new Vector3f(200, 200, 100), new Vector3f(1, 0, 1));

        Camera camera = new Camera();

        MasterRenderer renderer = new MasterRenderer();
        while (!Display.isCloseRequested()) {
            //entity.increasePosition(0, 0, -.1f);
            entity.increaseRotation(0, 0, 0);
            camera.move();
            renderer.processEntity(entity);
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
