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
public class EntityRenderer {
    private StaticShader shader;

    /**
     * Default constructor for EntityRenderer
     * Sets up the projection
     */
    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;

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
        shader.loadNumberOfRows(model.getTexture().getNumberOfRowsInAtlas());

        if (texture.isHasTransparancy()) {
            MasterRenderer.disableCulling();
        }

        // If the texture uses fake lighting (set all its normals straight up), then set it now.
        shader.loadFakeLighting(texture.isUseFakeLighting());

        // Load the reflectivity values into the shader for specular lighting before we render
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();

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
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
    }
}

