package textures;

/**
 * Created by Kurt on 6/25/2017.
 */
public class ModelTexture {
    private int textureID;
    private float shineDamper = 1;
    private float reflectivity = 0;

    private boolean hasTransparancy = false;
    private boolean useFakeLighting = false;

    public boolean isUseFakeLighting() {
        return useFakeLighting;
    }

    public void setUseFakeLighting(boolean useFakeLighting) {
        this.useFakeLighting = useFakeLighting;
    }

    public boolean isHasTransparancy() {
        return hasTransparancy;
    }

    public void setHasTransparancy(boolean hasTransparancy) {
        this.hasTransparancy = hasTransparancy;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(int shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(int reflectivity) {
        this.reflectivity = reflectivity;
    }

    public ModelTexture(int textureID) {
        this.textureID = textureID;
    }

    public int getID() {
        return this.textureID;
    }
}
