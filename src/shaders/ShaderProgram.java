package shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

/**
 * Created by Kurt on 6/25/2017.
 */
public abstract class ShaderProgram {
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16); // 4 x 4 floats for matrix

    /**
     * Constructor for abstract class ShaderProgram
     * <p>
     * Takes a vertexFile and a fragmentFile and loads them into memory
     * Creates a shader program and returns a ProgramID of that shader program which is stored within the ShaderProgram
     * object.
     * Attaches the vertexShader and fragmentShaders to the shader
     * Binds the attributes
     * Links the shader programID to the shaderProgram
     * Validates that the shader program compiled correctly.
     * Gets all the uniform location variables from the shaders.
     *
     * @param vertexFile   - the name of the vertexFile to load
     * @param fragmentFile - the name of the fragmentFile to load
     */
    public ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        getAllUniformLocations();
    }

    /**
     * Ensures that all derived classes will have a method to get the Uniform variable location IDs.
     */
    protected abstract void getAllUniformLocations();

    /**
     * Retrieve the int corresponding to a uniform variable in a shader.
     *
     * @param uniformVariableName - naame of variable in shader to locate
     * @return int - an int representing the variable within the shader
     */
    protected int getUniformLocation(String uniformVariableName) {
        return GL20.glGetUniformLocation(programID, uniformVariableName);
    }

    /**
     * starts the shader program running.
     */
    public void start() {
        GL20.glUseProgram(programID);
    }

    /**
     * stops the shader program
     */
    public void stop() {
        GL20.glUseProgram(0);
    }

    /**
     * cleans up resources used by all shader programs
     */
    public void cleanUp() {
        stop();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }

    /**
     * abstract - must be overriden in concrete classes.
     * Binds attribute to a shader.
     */
    protected abstract void bindAttributes();

    /**
     * Bind a given variable name to the specified attribute
     *
     * @param attribute    - ID of attribute to bind to.
     * @param variableName - name of varible in the shader.txt program to bind.
     */

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    /**
     * Loads a Uniform Variable of type float into a given location
     *
     * @param location - Location into which the float is to be placed.
     * @param value    - a float value to be loaded into the given locaiton.
     */
    protected void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    /**
     * Loads an int into the Uniform Variable at the given location.
     * @param location
     * @param value
     */
    protected void loadInt(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    /**
     * Loads a Uniform Variable of type vector into a given location
     *
     * @param location - Location into which the vector is to be placed.
     * @param vector   - a 3 vertex vector to be loaded into the given location.
     */
    protected void loadVector(int location, Vector3f vector) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    /**
     * Loads a boolean uniform variable into a specified location.
     * NOTE: There aren't actually booleans in the shader programs, so we simulate it using a float.
     *
     * @param location - Location into which the boolean is to be placed.
     * @param value    - the boolean to be loaded into the given location
     */
    protected void loadBoolean(int location, boolean value) {
        float toLoad = 0;
        if (value) {
            toLoad = 1;
        }
        GL20.glUniform1f(location, toLoad);
    }

    /**
     * Load a Uniform Variable of type Matrix into a given location
     *
     * @param location - location into which the matrix will be placed.
     * @param matrix   - 4 float Matrix to be loaded into the given location.
     */
    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4(location, false, matrixBuffer);
    }

    /**
     * Loads a .txt shader program from disk into memory and compiles the code
     *
     * @param file - name of shader file to load.
     * @param type - type of shader file (e.g., Vertex, Fragment)
     * @return int - the int representing the specific shaderID.
     */
    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Could not read shader file!");
            e.printStackTrace();
            System.exit(-1);
        }

        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader.");
            System.exit(-1);
        }

        return shaderID;
    }

}
