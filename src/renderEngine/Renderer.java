package renderEngine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix4f;
import shaders.StaticShader;
import toolbox.Maths;

import javax.xml.soap.Text;

/**
 * Created by Kurt on 6/25/2017.
 */
public class Renderer {

    private static final float FOV = 70;            // Field of view for camera projections
    private static final float NEAR_PLANE = 0.1f;   // Near plane distance from camera
    private static final float FAR_PLANE = 100f;    // Far plane distance from camera

    private Matrix4f projectionMatrix;

    /**
     * Default constructor for Renderer
     * Sets up the projection
     */
    public Renderer(StaticShader shader) {
        createProjectionMatrix();

        // It is a static shader, so we will only ever need to load up the projection matrix into the shader once.
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * Prepare the screen for the next frame
     * Currently sets the background to red.
     */
    public void prepare() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClearColor(1, 0, 0, 1);
    }

    /**
     * Renders entities to the screen
     * @param entity - The entity to be rendered.
     * @param shader - The shader to apply to the entity
     */
    public void render(Entity entity, StaticShader shader) {

        // Get the textured model from the Entity object
        TexturedModel model = entity.getModel();

        // Get the rawModel from the textured model object.
        RawModel rawModel = model.getRawModel();

        // Bind the model to the VAO by getting the vaoID from the model
        GL30.glBindVertexArray(rawModel.getVaoID());

        // Activate the attribute list in which our data is stored. We used attribute list 0
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        // Load up the tranformation matrix
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());

        // Load the transformation matrix into the shader
        shader.loadTransformationMatrix(transformationMatrix);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

        // Render it! Tell the draw engine that we will be rendering triangles
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // We have finished using everyting, so Disable the attribute list
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        // Unbind the VAO Vertex Array (0 is the last used)
        GL30.glBindVertexArray(0);
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

