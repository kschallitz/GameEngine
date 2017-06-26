package toolbox;

import entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Kurt on 6/26/2017.
 */
public class Maths {

    /**
     * Convert a 3float translation, rotation and scale into a 4 float Matrix
     *
     * @param translation - The 3D Vector of the translation
     * @param rx          - rotation x
     * @param ry          - rotation y
     * @param rz          - rotation z
     * @param scale       - the scale of the object
     * @return -
     */
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();

        matrix.setIdentity(); // resets the matrix
        Matrix4f.translate(translation, matrix, matrix);

        // Rotate the matrix
        Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);

        // Scale uniformly in all 3 dimensions
        Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);

        return matrix;
    }

    /**
     * Convert Camera position into a 4 float View Matrix
     * The idea is that we want to move the entire world opposite the camera to simulate the camera moving though
     * the world.
     * @param camera - the camera position to be translated
     * @return -
     */
    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity(); // resets the matrix

        // Rotate the matrix
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1), matrix, matrix);

        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f.translate(negativeCameraPos, matrix, matrix);

        return matrix;
    }
}
