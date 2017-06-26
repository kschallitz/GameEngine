package models;

import textures.ModelTexture;

/**
 * Created by Kurt on 6/25/2017.
 */
public class TexturedModel {
    private RawModel rawModel;
    private ModelTexture texture;

    public TexturedModel(RawModel rawModel, ModelTexture modelTexture) {
        this.rawModel = rawModel;
        this.texture = modelTexture;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public ModelTexture getTexture() {
        return texture;
    }
}
