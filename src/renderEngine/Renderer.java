package renderEngine;

import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Created by Kurt on 6/25/2017.
 */
public class Renderer {

    public void prepare() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClearColor(1, 0, 0, 1);
    }

    public void render(TexturedModel texturedModel) {
        // Get the rawModel from the textured model object.
        RawModel model = texturedModel.getRawModel();

        // Bind the model to the VAO by getting the vaoID from the model
        GL30.glBindVertexArray(model.getVaoID());

        // Activate the attribute list in which our data is stored. We used attribute list 0
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getID());

        // Render it! Tell the draw engine that we will be rendering triangles
        //GL11.glDrawArrays(GL11.GL_TRIANGLES,    0, model.getVertexCount());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        // We have finished using everyting, so Disable the attribute list
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        // Unbind the VAO Vertex Array (0 is the last used)
        GL30.glBindVertexArray(0);
    }
}

