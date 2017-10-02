package entities;

import engineTester.MainGameLoop;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;
import terrains.TerrainManager;

import static java.lang.System.exit;

/**
 * Created by Kurt on 6/30/2017.
 */
public class Player extends Entity {
    private static final float PLAYER_COG_X = 2.0f;
    private static final float PLAYER_COG_Y = 1.5f;
    private static final float PLAYER_COG_Z = 5.0f;

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

    public void move(Terrain terrain) {
        if (terrain == null) {
            throw new IllegalArgumentException("Terrain cannot be null!");
        }

        checkInputs();
        super.increaseRotation(0, 0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds());
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) Math.sin(Math.toRadians(super.getRotZ())) * distance;
        float dz = (float) Math.cos(Math.toRadians(super.getRotZ())) * distance;

        // determine if the new position maps to a valid terrain
        if (TerrainManager.isValidPosition(getPosition().x + dx, getPosition().z + dz)) {
            super.increasePosition(dx, 0, dz);
        }

        upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        float increaseUp = upwardSpeed * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0, increaseUp, 0);

        float yPos = super.getPosition().getY();

        // Check for terrain collision
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z) + PLAYER_COG_Y;
        if (yPos < terrainHeight) {
            upwardSpeed = 0;
            super.getPosition().y = terrainHeight;
            inAir = false;
        }

        // Determine if we need to rotate the player based on terrain slope
        float terrainHeightRear = terrain.getHeightOfTerrain(super.getPosition().x - 10f, super.getPosition().z);
        float terrainHeightFront = terrain.getHeightOfTerrain(super.getPosition().x + 10f, super.getPosition().z);
        float terrainHeightLeft =  terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z - 5f);
        float terrainHeightRight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z + 5f);

        // Change player's rotation to match slope of terrain
        float theta;
        if (terrainHeightRear != terrainHeightFront) {
            theta = (float)Math.toDegrees(Math.atan((terrainHeightRear - terrainHeightFront)/20f));
            super.setRotY(theta);
        }
        if (terrainHeightLeft != terrainHeightRight) {
            theta = (float)Math.toDegrees(Math.atan((terrainHeightLeft - terrainHeightRight)/10f));
            super.setRotX(-90.0f + theta); // Add to -90 because our player model, by default, is rotated -90 on x
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
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && (
                Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)
                )) {
            currentTurnSpeed = -TURN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && (
                Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)
        )) {
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
            exit(-1);
        }

        // Enable/Disable debug info
        // Move camera left / right
        if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
            MainGameLoop.showDebugInfo = !MainGameLoop.showDebugInfo;
        }

        // Reset the camera
        if (Keyboard.isKeyDown(Keyboard.KEY_F12)) {
            Camera.reset();
        }
    }
}
