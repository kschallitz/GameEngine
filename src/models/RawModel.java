package models;

/**
 * Created by Kurt on 6/25/2017.
 */
public class RawModel {
    private int vaoID;
    private int vertexCount;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }
}
