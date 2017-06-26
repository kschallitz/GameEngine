package engineTester;

import entities.Entity;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import models.RawModel;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

/**
 * Created by Kurt on 6/25/2017.
 */
public class MainGameLoop {
    public static void main(String[] args) {

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);

        float[] vertices = {
                -0.5f, 0.5f, 0f,    // V0
                -0.5f, -0.5f, 0f,   // V1
                0.5f, -0.5f, 0f,    // V2
                0.5f, 0.5f, 0f      // V3
        };

        int[] indices = {
                0, 1, 3, // Top left triangle (V0, V1, V3)
                3, 1, 2  // Bottom right triangle (V3, V1, V2)
        };

        float[] textureCoords = {
            0, 0,   // V0
            0, 1,   // V1
            1, 1,   // V2
            1, 0    // V3
        };

        RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
        ModelTexture texture = new ModelTexture(loader.loadTexture("chihuahua"));
        TexturedModel staticModel = new TexturedModel(model, texture);

        // Move the quad one position to the left, no rotation, no scale adjustment (i.e., 100%)
        Entity entity = new Entity(staticModel, new Vector3f(0f, 0f, -1f), 0, 0, 0, 1);

        while (!Display.isCloseRequested()) {
            entity.increasePosition(0, 0, -.1f);
            renderer.prepare();
            shader.start();
            renderer.render(entity, shader);
            shader.stop();
            DisplayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
