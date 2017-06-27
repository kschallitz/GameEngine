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
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

/**
 * Created by Kurt on 6/25/2017.
 */
public class MainGameLoop {
    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader(); // Loads data into VAO
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);


        RawModel model = OBJLoader.loadObjModel("dragon", loader);

        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("white")));

        // Move the quad one position to the left, no rotation, no scale adjustment (i.e., 100%)
        Entity entity = new Entity(staticModel, new Vector3f(0f, -5f, -25f), 0, 0, 0, 1);
        Light light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));

        Camera camera = new Camera();


        while (!Display.isCloseRequested()) {
            //entity.increasePosition(0, 0, -.1f);
            entity.increaseRotation(0, 1, 0);
            camera.move();
            renderer.prepare();
            shader.start();
            shader.loadLight(light);        // Load the light each frame so we can adjust it
            shader.loadViewMatrix(camera);  // Load up the camera view into the shader
            renderer.render(entity, shader);
            shader.stop();
            DisplayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
