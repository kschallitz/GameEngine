package guis;

import models.RawModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import renderEngine.Loader;
import toolbox.Maths;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Kurt on 7/5/2017.
 */
public class GuiRenderer {
    private final RawModel quad;
    private GuiShader shader;

    public GuiRenderer(Loader  loader) {
        float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
        quad = loader.loadToVAO(positions, 2);
        shader = new GuiShader();
    }

    public void render(Set<GuiTexture> guis) {
        if (guis.size() == 0)
            return;

        preRender();

        // Render
        Iterator it = guis.iterator();
        while (it.hasNext()) {
            GuiTexture g = (GuiTexture) it.next();
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, g.getTexture());
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
            Matrix4f matrix = Maths.createTransformationMatrix(g.getPosition(), g.getScale());
            shader.loadTransformation(matrix);
        }

        postRender();
    }
    public void render(List<GuiTexture> guis) {
        preRender();

        // Render
        for (GuiTexture gui : guis) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTexture());
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
            Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
            shader.loadTransformation(matrix);
        }

        postRender();
    }

    private void postRender() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void preRender() {
        shader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
