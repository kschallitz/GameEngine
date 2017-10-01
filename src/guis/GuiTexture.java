package guis;

import org.lwjgl.util.vector.Vector2f;

/**
 * Created by Kurt on 7/5/2017.
 */
public class GuiTexture {
    private int texture;
    private Vector2f position;
    private Vector2f scale;

    public GuiTexture(int texture, Vector2f position, Vector2f scale) {
        this.texture = texture;
        this.position = position;   // Center of the 2D quad
        this.scale = scale;         // x & y size of the quad in relation to the size of the screen
    }

    public int getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }
    public Vector2f getScale() {
        return scale;
    }
}
