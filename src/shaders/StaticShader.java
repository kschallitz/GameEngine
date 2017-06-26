package shaders;

import com.sun.prism.ps.Shader;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Created by Kurt on 6/25/2017.
 */
public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
    private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";

    private int location_transformationMatrix;

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
    }

    /**
     * Load the transformationMatrix by calling the super's loadMatrix method.
     */
    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }
}
