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
        // Zoom in / out on Z axis
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            position.z -= 0.05;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
            position.z += 0.05f;
        }

        // Move camera left / right
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            position.x += 0.05f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            position.x -= 0.05f;
        }

        // Move camera up / down
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            position.y += 0.05f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            position.y -= 0.05f;
        }
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
