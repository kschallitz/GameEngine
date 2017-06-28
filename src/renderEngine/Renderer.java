package renderEngine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import textures.ModelTexture;
import toolbox.Maths;

import javax.xml.soap.Text;
import java.util.List;
import java.util.Map;

/**
 * Created by Kurt on 6/25/2017.
 */
public class Renderer {

    private static final float FOV = 70;            // Field of view for camera projections
    private static final float NEAR_PLANE = 0.1f;   // Near plane distance from camera
    private static final float FAR_PLANE = 100f;    // Far plane distance from camera

    private Matrix4f projectionMatrix;
    private StaticShader shader;

    /**
     * Default constructor for Renderer
     * Sets up the projection
     */
    public Renderer(StaticShader shader) {
        this.shader = shader;

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BACK);

        createProjectionMatrix();

        // It is a static shader, so we will only ever need to load up the projection matrix into the shader once.
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Map<TexturedModel, List<Entity>> entities) {
        for (TexturedModel model : entities.keySet()) {
            prepareTexturedModels(model);
            List<Entity> batch = entities.get(model);
            for (Entity entity : batch) {
                prepareInstance(entity);
                // Render it! Tell the draw engine that we will be rendering triangles
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);

            }
            // Done using this textured model, free up the memory
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModels(TexturedModel model) {
        // Get the rawModel from the textured model object.
        RawModel rawModel = model.getRawModel();

        // Bind the model to the VAO by getting the vaoID from the model
        GL30.glBindVertexArray(rawModel.getVaoID());

        // Activate the attribute list in which our data is stored.
        GL20.glEnableVertexAttribArray(0);  // Position Vertices
        GL20.glEnableVertexAttribArray(1);  // Textures
        GL20.glEnableVertexAttribArray(2);  // Normals

        // Get specular lighting info - reflectivity values from our textured model
        ModelTexture texture = model.getTexture();

        // Load the reflectivity values into the shader for specular lighting before we render
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

    }

    private void unbindTexturedModel() {
        // We have finished using everyting, so Disable the attribute list
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        // Unbind the VAO Vertex Array (0 is the last used)
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Entity entity) {
        // Load up the transformation matrix
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

        // Load the transformation matrix into the shader
        shader.loadTransformationMatrix(transformationMatrix);
    }

    /**
     * Prepare the screen for the next frame
     * Currently sets the background to red.
     */
    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(.5f, 0, .1f, 1);
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
}

