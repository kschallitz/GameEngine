package toolbox;

import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Kurt on 6/26/2017.
 */
public class Maths {

    /**
     * Convert a 3float translation, rotation and scale into a 4 float Matrix
     * @param translation - The 3D Vector of the translation
     * @param rx - rotation x
     * @param ry - rotation y
     * @param rz - rotation z
     * @param scale - the scale of the object
     * @return -
     */
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();

        matrix.setIdentity(); // resets the matrix
        Matrix4f.translate(translation, matrix, matrix);

        // Rotate the matrix
        Matrix4f.rotate((float)Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
        Matrix4f.rotate((float)Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
        Matrix4f.rotate((float)Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);

        // Scale uniformly in all 3 dimensions
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

        return matrix;
    }
}
