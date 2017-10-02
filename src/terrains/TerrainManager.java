package terrains;

import entities.Player;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;

/**
 * Created by Kurt on 7/4/2017.
 */
public class TerrainManager {
    private static HashMap<Integer, HashMap<Integer, Terrain>> terrains = new HashMap<Integer, HashMap<Integer, Terrain>>();
    private static int terrainRow;
    private static int terrainCol;

    /**
     * Places a terrain object into the terrain array.
     *
     * @param terrain - the terrain to be added
     */
    public void add(Terrain terrain) {
        HashMap<Integer, Terrain> sub = new HashMap<Integer, Terrain>();
        sub.put((int)Math.floor(terrain.getZ()/Terrain.SIZE), terrain);
        terrains.put((int)Math.floor(terrain.getX()/Terrain.SIZE), sub);
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
     * returns a Terrain from the terrains array at the player's location
     * @param player
     * @return - Terrain at the given indices, or null if indices are out of bounds.
     */
    public Terrain get(Player player) {
        Terrain terrain = null;

        terrainRow = (int)Math.floor(player.getPosition().x / Terrain.SIZE);
        terrainCol = (int)Math.floor(player.getPosition().z / Terrain.SIZE);

        if (terrains.get(terrainRow) != null) {
            terrain = terrains.get(terrainRow).get(terrainCol);
        }

        return terrain;
    }

    /**
     * returns a Terrain from the terrains array at the player's location
     * @return - Terrain at the given indices, or null if indices are out of bounds.
     */
    public Terrain get(int row, int col) {
        Terrain terrain = null;

        if (terrains.get(row) != null) {
            terrain = terrains.get(row).get(col);
        }
        return terrain;
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
    public static Terrain getTerrainAtPosition(float worldX, float worldZ) {
        Terrain terrain = null;

        // Compute in which terrain square the coordinates map
        int xCoord = (int)Math.floor(worldX / Terrain.SIZE);
        int zCoord = (int)Math.floor(worldZ / Terrain.SIZE);

        if (terrains.get(xCoord) != null) {
            terrain = terrains.get(xCoord).get(zCoord);
        }
        return terrain;
    }

    /**
     * Determines if a position will map to a valid terrain.
     * @param worldX
     * @param worldZ
     * @return
     */
    public static boolean isValidPosition(float worldX, float worldZ) {
        if (getTerrainAtPosition(worldX, worldZ) != null) {
            return true;
        }

        return false;
    }

    public static int getTerrainRow() {
        return terrainRow;
    }

    public static int getTerrainCol() {
        return terrainCol;
    }
}
