package renderEngine;

import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kurt on 6/26/2017.
 */
public class OBJLoader {

    public static RawModel loadObjModel(String fileName, Loader loader) {

        float[] verticesArray = null;
        float[] texturesArray = null;
        float[] normalsArray = null;
        int[] indicesArray = null;

        try (FileReader fr = new FileReader(new File("res/" + fileName + ".obj"));
             BufferedReader reader = new BufferedReader(fr);) {

            String line;
            List<Vector3f> vertices = new ArrayList<Vector3f>();
            List<Vector2f> textures = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();

            // read Vertecies, textures and normals
            while (true) {
                line = reader.readLine();
                String[] currentLine = line.split(" ");
                if (line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);

                } else if (line.startsWith("vt ")) {
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    // per the format of our file faces always come last, therefore we will have the size of all verts.
                    texturesArray = new float[vertices.size() * 2];
                    normalsArray = new float[vertices.size() * 3];

                    // Exit WHILE Loop
                    break;
                }
            }

            // Faces loop
            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }
                line = line.substring(1, line.length());
                line = line.trim();

                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[0].split("/");
                String[] vertex2 = currentLine[1].split("/");
                String[] vertex3 = currentLine[2].split("/");

                processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);

                line = reader.readLine();
            }

            verticesArray = new float[vertices.size() * 3];
            indicesArray = new int[indices.size()];

            int vertexPointer = 0;
            for (Vector3f vertex : vertices) {
                verticesArray[vertexPointer++] = vertex.x;
                verticesArray[vertexPointer++] = vertex.y;
                verticesArray[vertexPointer++] = vertex.z;

            }

            for (int i=0; i < indices.size(); i++) {
                indicesArray[i] = indices.get(i);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load object file: " + fileName);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // handle any kind of file formatting issue
            e.printStackTrace();
        }

        return loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
    }

    /**
     * sort out the correct positions of the texture and normals for the current vertex.
     * Places them into the correct position in the corresponding arrays.
     * @param vertexData
     * @param indices
     * @param textures
     * @param normals
     * @param textureArray
     * @param normalArray
     */
    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
                                      List<Vector3f> normals, float[] textureArray, float[] normalArray) {

        try {
            // Remember to convert to zero based index.
            int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
            indices.add(currentVertexPointer);

            Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);

            textureArray[currentVertexPointer * 2] = currentTex.x;

            // Need to subtract from 1 because OpenGL starts from top left, blender starts from bottom left.
            textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;

            Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
            normalArray[currentVertexPointer * 3 + 0] = currentNorm.x;
            normalArray[currentVertexPointer * 3 + 1] = currentNorm.y;
            normalArray[currentVertexPointer * 3 + 2] = currentNorm.z;
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
    }
}
