package shaders;

import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import toolbox.Maths;

/**
 * Created by Kurt on 6/28/2017.
 */
public class TerrainShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/shaders/terrainVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader.txt";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition;
    private int location_lightColor;
    private int location_shineDamper;
    private int location_reflectivity;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
    }

    @Override
    /**
     * Load the transformationMatrix Uniform Variable ID into the class object transformationMatrix
     */
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColor = super.getUniformLocation("lightColor");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
    }

    /**
     * Load the shine variables into the Uniform Variables in the fragment shader
     * The shine variables will determine how reflective and "shinny" the model will be.
     *
     * @param damper - Determines how much "scatter" a reflective surface has. The more scatter, the further away
     *                 the camera can be from the surface normal and still pick up reflected light.
     * @param reflectivity - Represents how reflective a given surface is. A value of 0 means the surface reflects no
     *                       light at all.
     */
    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    /**
     * Load the transformationMatrix by calling the super's loadMatrix method.
     */
    public void loadTransformationMatrix(Matrix4f matrix) {

        super.loadMatrix(location_transformationMatrix, matrix);
    }

    /**
     * Load the Camera view matrix into the Uniform Variable in the shader
     * @param camera-The camera containing the view to be loaded into the shader
     */
    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    /**
     * Load the projection matrix into the Uniform Variable in the shader
     * @param projection - the projection to load into the shader.
     */
    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }

    /**
     * Load light values to shaders
     * @param light - Light object
     */
    public void loadLight(Light light) {
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColor, light.getColor());
    }

}
