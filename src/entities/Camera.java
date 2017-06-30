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
        // Adjust camera pitch
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            pitch -= 0.3f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
            pitch += 0.3f;
        }

        // Adjust camera Yaw
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            yaw -= 0.3f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            yaw += 0.3f;
        }

        // Zoom in / out on Z axis
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            position.z -= .5f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            position.z += .5f;
        }

        // Move camera left / right
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            position.x += .5f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            position.x -= .5f;
        }

        // Move camera up / down
        if (Keyboard.isKeyDown(Keyboard.KEY_HOME)) {
            position.y += 0.5f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_END)) {
            position.y -= 0.5f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            System.exit(-1);
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
