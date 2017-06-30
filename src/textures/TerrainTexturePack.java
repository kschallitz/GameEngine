package textures;

/**
 * Contains all the textures that we will want to render on a given terrain.
 * The textures are parsed based on the r,g,b settings of a blend map.
 * The r component of the blend map will coorespond to texture1, g to texture 2, etc.
 *
 * Created by Kurt on 6/29/2017.
 */
public class TerrainTexturePack {
    private TerrainTexture backgroundTexture;
    private TerrainTexture rTexture;   // Texture represented by the r component of the blend map
    private TerrainTexture gTexture;   // Texture represented by the g component of the blend map
    private TerrainTexture bTexture;   // Texture represented by the b component of the blend map

    public TerrainTexturePack(TerrainTexture backgroundTexture, TerrainTexture rTexture, TerrainTexture gTexture, TerrainTexture bTexture) {
        this.backgroundTexture = backgroundTexture;
        this.rTexture = rTexture;
        this.gTexture = gTexture;
        this.bTexture = bTexture;
    }

    public TerrainTexture getBackgroundTexture() {
        return backgroundTexture;
    }

    public TerrainTexture getrTexture() {
        return rTexture;
    }

    public TerrainTexture getgTexture() {
        return gTexture;
    }

    public TerrainTexture getbTexture() {
        return bTexture;
    }
}
