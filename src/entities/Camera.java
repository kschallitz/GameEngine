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
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            //position.z -= 0.05;
            position.z -= 1;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            //position.z += 0.05f;
            position.z += 1;
        }

        // Move camera left / right
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            //position.x += 0.05f;
            position.x += 1;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            //position.x -= 0.05f;
            position.x -= 1;
        }

        // Move camera up / down
        if (Keyboard.isKeyDown(Keyboard.KEY_HOME)) {
            //position.y += 0.05f;
            position.y += 0.02f;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_END)) {
            //position.y -= 0.05f;
            position.y -= 0.02f;
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
