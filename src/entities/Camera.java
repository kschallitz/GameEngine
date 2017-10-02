package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Kurt on 6/26/2017.
 */
public class Camera {
    private static final float DEFAULT_DFP = 38.0f;
    private static final float DEFAULT_AAP = 45.0f;
    private static final float DEFAULT_PITCH = 17.0f;
    private static final float DEFAULT_YAW = 136.0f;
    private static final float DEFAULT_ROLL = 0f;

    private static float disatanceFromPlayer = DEFAULT_DFP;
    private static float angleAroundPlayer = DEFAULT_AAP;

    private static Vector3f position = new Vector3f(5, 5f, 100);
    private static float pitch = DEFAULT_PITCH;
    private static float yaw = DEFAULT_YAW;
    private static float roll = DEFAULT_ROLL;
    private Player player;

    public Camera(Player player){
        this.player = player;
    };

    public void move() {
        calculateZoomLevel();;
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance;

    }

    private float calculateHorizontalDistance() {
        return (float) (disatanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (disatanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoomLevel() {
        float zoomLevel = Mouse.getDWheel()* 0.1f;
        disatanceFromPlayer -= zoomLevel;
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch += pitchChange;
        }
    }

    private void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
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

    public float getDisatanceFromPlayer() {
        return disatanceFromPlayer;
    }

    public float getAngleAroundPlayer() {
        return angleAroundPlayer;
    }

    public static void reset() {
        disatanceFromPlayer = DEFAULT_DFP;
        angleAroundPlayer = DEFAULT_AAP;
        position = new Vector3f(5, 5f, 100);
        pitch = DEFAULT_PITCH;
        yaw = DEFAULT_YAW;
        roll = DEFAULT_ROLL;
    }
}
