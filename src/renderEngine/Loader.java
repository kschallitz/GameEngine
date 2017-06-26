package renderEngine;

import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kurt on 6/25/2017.
 */
public class Loader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public RawModel loadToVAO(float[] positions, float[] textureCoords, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);

        // Positions are a 3d vector x,y,z so tell the method to use 3 coordinates.
        storeDataInAttributeList(0, 3, positions);

        // Texture coordinates are 2D
        storeDataInAttributeList(1, 2, textureCoords);

        unbindVAO();

        // Don't need to use the length / 3 of the positions, now we can use the length of the indices buffer.
        return new RawModel(vaoID, indices.length);
    }

    public int loadTexture(String fileName) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png"));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        int textureId = texture.getTextureID();
        textures.add(textureId);

        return textureId;
    }

    public void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }

        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }

        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();

        // Keep track of all the VAOs we create so that we can delete them all later
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);

        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers();

        // Keep track of any VBOs we create so that we can destroy them all later
        vbos.add(vboID);

        // Bind the buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        // Covert from float[] to FloatBuffer
        FloatBuffer buffer = storeDataInFloatBuffer(data);

        // Populate the VBO with the vertex data - static draw means we will not be editing the data.
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        // Fill the vertex pointer with info about our vertex buffer in the VBO
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

        // Unbind the current VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);

        // Right now the buffer is in a write state, let's make sure to flip it to a read state
        buffer.flip();

        return buffer;
    }

}
