package renderEngine;

import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import textures.TextureData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
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

    /**
     * Main loader for 3D objects
     * @param positions
     * @param textureCoords
     * @param normals
     * @param indices
     * @return
     */
    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);

        // Positions stored in VAO 0 - are a 3d vector x,y,z so tell the method to use 3 coordinates.
        storeDataInAttributeList(0, 3, positions);

        // Textures stored in VAO 1 - coordinates are 2D
        storeDataInAttributeList(1, 2, textureCoords);

        // Normals stored in VAO 2 - normals are 3D vectors
        storeDataInAttributeList(2, 3, normals);

        unbindVAO();

        // Don't need to use the length / 3 of the positions, now we can use the length of the indices buffer.
        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(float[] positions, int dimensions) {
        int vaoID = createVAO();
        this.storeDataInAttributeList(0, dimensions, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / dimensions);
    }

    public int loadTexture(String fileName) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png"));
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
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
    public int loadCubeMap(String[] textureFiles) {
        int textID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textID);

        for (int i = 0; i < textureFiles.length; i++) {
            TextureData data = decodeTextureFile("res/" + textureFiles[i] + ".png");
            GL11.glTexImage2D(
                    // NOTE - map images must be in order starting with MAP_POSITIVE_X
                    GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                    0,
                    GL11.GL_RGBA,
                    data.getWidth(),
                    data.getHeight(),
                    0,
                    GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE,
                    data.getBuffer()
            );

            GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        }

        textures.add(textID);
        return textID;
    }

    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;

        try {
            FileInputStream f = new FileInputStream(fileName);
            PNGDecoder decoder = new PNGDecoder(f);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(width * height * 4);
            decoder.decode(buffer, width * 4, Format.RGBA);
            buffer.flip();
            f.close();
        }
        catch (FileNotFoundException fnfe) {
            System.out.println("File Not Found Error in TextureData: " + fileName);
            System.exit(-1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ", didn't work.");
            System.exit(-1);
        }
        return new TextureData(buffer, width, height);
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
