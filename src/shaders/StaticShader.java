package shaders;

import com.sun.prism.ps.Shader;
import entities.Camera;
import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix4f;
import toolbox.Maths;

/**
 * Created by Kurt on 6/25/2017.
 */
public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
    }

    @Override
    /**
     * Load the transformationMatrix Uniform Variable ID into the class object transformationMatrix
     */
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    /**
     * Load the transformationMatrix by calling the super's loadMatrix method.
     */
    public void loadTransformationMatrix(Matrix4f matrix) {

        super.loadMatrix(location_transformationMatrix, matrix);
    }

    /**
     * Load the Camera view matrix into the Unifrom Variable in the shader
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

}
