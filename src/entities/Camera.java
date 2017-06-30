package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Kurt on 6/26/2017.
 */
public class Camera {
    private Vector3f position = new Vector3f(5, 5f, 100);
    private float pitch;
    private float yaw = 0f;
    private float roll;

    public Camera(){};

    public void move() {

    }
    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
