package entities;

import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

/**
 * Created by Kurt on 6/30/2017.
 */
public class Player extends Entity {
    private static final float TERRAIN_HEIGHT = 1.5f;

    private static final float RUN_SPEED = 20f;       // Units / second
    private static final float TURN_SPEED = 160f;     // Degrees / second

    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;
    private boolean inAir = false;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardSpeed = 0;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move() {
        checkInputs();
        super.increaseRotation(0, 0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds());
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) Math.sin(Math.toRadians(super.getRotZ())) * distance;
        float dz = (float) Math.cos(Math.toRadians(super.getRotZ())) * distance;
        super.increasePosition(dx, 0, dz);

        upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        float increaseUp = upwardSpeed * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0, increaseUp, 0);

        float yPos = super.getPosition().getY();

        if (yPos < TERRAIN_HEIGHT) {
            upwardSpeed = 0;
            super.getPosition().y = TERRAIN_HEIGHT;
            inAir = false;
        }
    }
    private void jump() {
        upwardSpeed = JUMP_POWER;
    }

    private void checkInputs() {
        // Adjust player speed
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            currentSpeed = RUN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            currentSpeed = -RUN_SPEED;
        } else {
            currentSpeed = 0;
        }

        // Move camera left / right
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            currentTurnSpeed = -TURN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            currentTurnSpeed = TURN_SPEED;
        } else {
            currentTurnSpeed = 0;
        }

        // JUMP
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            if (!inAir) {
                jump();
                inAir = true;
            }
        }


        // END GAME
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            System.exit(-1);
        }
    }
}
