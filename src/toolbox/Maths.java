package toolbox;

import entities.Camera;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Kurt on 6/26/2017.
 */
public class Maths {

    /**
     * Computes the height at player position, given heights of the three verticies of the triangle
     * see: https://en.wikipedia.org/wiki/Barycentric_coordinate_system
     *
     * @param p1 vertex one position
     * @param p2 vertex two position
     * @param p3 vertex three position
     * @param pos player x, y position within the triangle
     *
     * @return the float representing the player's height at the given coordinates within the triangle specificed by
     *         p1, p2, p3
     */
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = ((p2.z - p3.z) * (p1.x - p3.x)) + ((p3.x - p2.x) * (p1.z - p3.z));
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x)) + ((p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x)) + ((p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;

        return (l1 * p1.y) + (l2 * p2.y) + (l3 * p3.y);
    }

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
