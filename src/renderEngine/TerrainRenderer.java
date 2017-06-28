package renderEngine;

import models.RawModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.ModelTexture;
import toolbox.Maths;

import java.util.List;

/**
 * Created by Kurt on 6/28/2017.
 */
public class TerrainRenderer {
    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.start();
    }

    public void render(List<Terrain> terrains) {
        for (Terrain terrain : terrains) {
            prepareTexturedModels(terrain);
            loadModelMatrix(terrain);
            // Render it! Tell the draw engine that we will be rendering triangles
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
                    GL11.GL_UNSIGNED_INT, 0);
            unbindTexturedModel();
        }
    }

    private void prepareTexturedModels(Terrain terrain) {
        // Get the rawModel from the textured model object.
        RawModel rawModel = terrain.getModel();

        // Bind the model to the VAO by getting the vaoID from the model
        GL30.glBindVertexArray(rawModel.getVaoID());

        // Activate the attribute list in which our data is stored.
        GL20.glEnableVertexAttribArray(0);  // Position Vertices
        GL20.glEnableVertexAttribArray(1);  // Textures
        GL20.glEnableVertexAttribArray(2);  // Normals

        // Get specular lighting info - reflectivity values from our textured model
        ModelTexture texture = terrain.getTexture();

        // Load the reflectivity values into the shader for specular lighting before we render
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());

    }

    private void unbindTexturedModel() {
        // We have finished using everyting, so Disable the attribute list
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        // Unbind the VAO Vertex Array (0 is the last used)
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(Terrain terrain) {
        // Load up the transformation matrix
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()),
                0, 0, 0, 1);

        // Load the transformation matrix into the shader
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
