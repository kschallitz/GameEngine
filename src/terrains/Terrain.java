package terrains;

import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Kurt on 6/28/2017.
 */
public class Terrain {
    public static final float SIZE = 800;
    private static final float MAX_HEIGHT = 40;
    private static final float MIN_HEIGHT = -40;
    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

    private float x;
    private float z;

    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap; // Not really a Terrain Texture, but it works for now.

    private float[][] heights;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap,
                   String heightMap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;

        this.model = generateTerrain(loader, heightMap);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexturePack getTexturePack() {
        return texturePack;
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;

        float gridSquareSize = SIZE / (float) (heights.length - 1f);
        int gridSquareX = (int) Math.floor(terrainX / gridSquareSize);
        int gridSquareZ = (int) Math.floor(terrainZ / gridSquareSize);

        // Check that the grid square is actually on the terrain.
        if ((gridSquareX >= heights.length - 1) || (gridSquareZ >= heights.length - 1) ||
                (gridSquareX < 0) || (gridSquareZ < 0)) {
            return 0;
        }

        // Find the distance of the player from the top left of the grid square
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;

        // Determine which triangle the player is standing (Each grid square is made up of two triangles)
        float playerHeight;
        if (xCoord <= (1 - zCoord)) {
            playerHeight = Maths.barryCentric(new Vector3f(0, heights[gridSquareX][gridSquareZ], 0),
                    new Vector3f(1, heights[gridSquareX + 1][gridSquareZ], 0),
                    new Vector3f(0, heights[gridSquareX][gridSquareZ + 1], 1),
                    new Vector2f(xCoord, zCoord));
        } else {
            playerHeight = Maths.barryCentric(new Vector3f(1, heights[gridSquareX + 1][gridSquareZ], 0),
                    new Vector3f(1, heights[gridSquareX + 1][gridSquareZ + 1], 1),
                    new Vector3f(0, heights[gridSquareX][gridSquareZ + 1], 1),
                    new Vector2f(xCoord, zCoord));
        }

        return playerHeight;
    }

    private RawModel generateTerrain(Loader loader, String heightMap) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("res/" + heightMap + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int VERTEX_COUNT = image.getHeight();
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                float height = getHeight(j, i, image);
                heights[j][i] = height;
                vertices[vertexPointer * 3 + 0] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
                vertices[vertexPointer * 3 + 1] = height;
                vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormals(j, i, image);
                normals[vertexPointer * 3 + 0] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;

                textureCoords[vertexPointer * 2 + 0] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private Vector3f calculateNormals(int x, int y, BufferedImage image) {
        float heightL = getHeight(x - 1, y, image);
        float heightR = getHeight(x + 1, y, image);
        float heightU = getHeight(x, y - 1, image);
        float heightD = getHeight(x, y + 1, image);
        Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
        normal.normalise();

        return normal;
    }

    private float getHeight(int x, int y, BufferedImage image) {
        if (((x < 0) || (x >= image.getHeight()) ||
                ((y < 0)) || (y >= image.getHeight()))) {
            return 0;
        }

        float height = image.getRGB(x, y);
        height += (MAX_PIXEL_COLOR / 2f);
        height /= (MAX_PIXEL_COLOR / 2f); // To give a range of +/- 1
        height *= MAX_HEIGHT; // To give a range of +/- max height

        return height;
    }
}
