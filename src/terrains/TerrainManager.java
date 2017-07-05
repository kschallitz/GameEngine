package terrains;

import org.lwjgl.util.vector.Vector2f;

import java.util.Iterator;

/**
 * Created by Kurt on 7/4/2017.
 */
public class TerrainManager {
    private static final int TERRAIN_ROWS = 1;
    private static final int TERRAIN_COLS = 2;
    private static final int TERRAIN_SIZE = 800;

    private Terrain[][] terrains = (Terrain[][]) new Terrain[TERRAIN_ROWS][TERRAIN_COLS];


    /**
     * Places a terrain object into the terrain array.
     *
     * @param row - row at which the terrain will be added
     * @param col - col at which the terrain will be added
     * @param terrain - the terrain to be added
     */
    public void add(int row, int col, Terrain terrain) {
        if (row < 0 || row > TERRAIN_ROWS - 1) {
            // TODO: Better to throw an invalid argument exception
            return;
        }
        if (col < 0 || col > TERRAIN_COLS - 1) {
            // TODO: Better to throw an invalid argument exception
            return;
        }

        terrains[row][col] = terrain;

    }

    /**
     * returns the terrain height given world coordinates
     *
     *
     * @param worldX
     * @param worldZ
     * @return
     */
    public float getHeightAtPosition(float worldX, float worldZ) {
        Terrain terrain = getTerrainAtPosition(worldX, worldZ);
        if (terrain == null) {
            return 0;
        }

        return terrain.getHeightOfTerrain(worldX, worldZ);
    }

    /**
     * returns a Terrain from the terrains array at the given indexes (row, col)
     * @param row
     * @param col
     * @return - Terrain at the given indices, or null if indices are out of bounds.
     */
    public Terrain get(int row, int col) {
        if (row < 0 || row > TERRAIN_ROWS - 1) {
            return null;
        }
        if (col < 0 || col > TERRAIN_COLS - 1) {
            return null;
        }
        return terrains[row][col];
    }

    public Vector2f getTextureCoords(Terrain terrain) {
        return null;
    }
    /**
     * Returns the terrain at the given world coordinates.
     * Returns null if the coordinates do not map to a valid terrain.
     * @param worldX -
     * @param worldZ -
     * @return - Terrain - the terrain at the given coordinates
     */
    public Terrain getTerrainAtPosition(float worldX, float worldZ) {
        // Compute in which terrain square the coordinates map
        int xCoord = (int)Math.floor(worldX / TERRAIN_SIZE);
        int zCoord = (int)Math.floor(worldZ / TERRAIN_SIZE);

        if (xCoord < 0 || xCoord > TERRAIN_COLS - 1) {
            return null;
        }
        if (zCoord < 0 || zCoord > TERRAIN_ROWS - 1) {
            return null;
        }

        return terrains[zCoord][xCoord];
    }

    /**
     * Determines if a position will map to a valid terrain.
     * @param worldX
     * @param worldZ
     * @return
     */
    public static boolean isValidPosition(float worldX, float worldZ) {
        int xCoord = (int)Math.floor(worldX / TERRAIN_SIZE);
        int zCoord = (int)Math.floor(worldZ / TERRAIN_SIZE);

        if (xCoord < 0 || xCoord > TERRAIN_COLS - 1) {
            return false;
        }
        if (zCoord < 0 || zCoord > TERRAIN_ROWS - 1) {
                return false;
        }
        return true;
    }
}
