package com.example.opengles_load_obj;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Mesh {

    private List<String> mVerticesList;
    private List<String> mFacesList;

    private FloatBuffer mVerticesBuffer;
    private ShortBuffer mFacesBuffer;

    // Set mColor with red, green, blue and alpha (opacity) values
    float mColor[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    // OpenGL ES graphics code for rendering the vertices of a shape
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // OpenGL ES code for rendering the face of a shape with colors or textures
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    // OpenGL ES object that contains the shaders you want to use for drawing one or more shapes
    private int mProgram;

    public Mesh(Context context, String fileName) {
        mVerticesList = new ArrayList<>();
        mFacesList = new ArrayList<>();

        parseObjFile(context, fileName);
    }

    /**
     * Initialize the buffers and shaders.
     */
    public void init() {
        createBuffers();
        fillBuffers();
        loadShaders();
    }

    /**
     * Parses an obj file from the assets and fill the vertice list and face list.
     * @param context the context from which the object is created, used to read the assets folder
     * @param fileName the name of the obj file to parse
     */
    private void parseObjFile(Context context, String fileName) {
        // Open the OBJ file with a Scanner
        Scanner scanner = null;
        try {
            scanner = new Scanner(context.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loop through all its lines
        if (scanner != null) {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.startsWith("v ")) {
                    // Add vertex line to list of vertices
                    mVerticesList.add(line);
                } else if(line.startsWith("f ")) {
                    // Add face line to faces list
                    mFacesList.add(line);
                }
            }
            // Close the scanner
            scanner.close();
        }
    }

    /**
     * Creates the vertice and face buffers
     */
    private void createBuffers() {
        // Create buffer for vertices
        ByteBuffer buffer1 = ByteBuffer.allocateDirect(mVerticesList.size() * 3 * 4);
        buffer1.order(ByteOrder.nativeOrder());
        mVerticesBuffer = buffer1.asFloatBuffer();

        // Create buffer for faces
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(mFacesList.size() * 3 * 2);
        buffer2.order(ByteOrder.nativeOrder());
        mFacesBuffer = buffer2.asShortBuffer();
    }

    /**
     * Fills the vertice and face buffers using the vertice and face lists.
     */
    private void fillBuffers() {
        for(String vertex: mVerticesList) {
            String[] coords = vertex.split(" ");
            for (int i = 1; i < 4; i++) {
                float coord = Float.parseFloat(coords[i]);
                Log.d("TAG", "vertex: " + coord);
                mVerticesBuffer.put(coord);
            }
        }
        mVerticesBuffer.position(0);

        for (String face : mFacesList) {
            String[] facesContent = face.split(" ");

            for (int i = 1; i < 4; i++) {
                String[] vertexIndices = facesContent[i].split("/");
                Log.d("TAG", "face: " + vertexIndices[0]);
                short vertex = Short.parseShort(vertexIndices[0]);
                mFacesBuffer.put((short) (vertex - 1));
            }
        }
        mFacesBuffer.position(0);
    }

    /**
     * Creates and compiles vertex and fragment shaders from their source code.
     */
    private void loadShaders() {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);

        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);

        // add the source code to the shader and compile it
        GLES20.glCompileShader(vertexShader);
        GLES20.glCompileShader(fragmentShader);

        // Create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // Add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);
        // Add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // Create OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    /**
     * Calling this function will draw the shape on the screen.
     */
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the square vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the square coordinate data
        GLES20.glVertexAttribPointer(positionHandle, 3,
                GLES20.GL_FLOAT, false,
                3 * 4, mVerticesBuffer);

        // get handle to fragment shader's vColor member
        int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set mColor for drawing the square
        GLES20.glUniform4fv(colorHandle, 1, mColor, 0);

        // get handle to shape's transformation matrix
        int matrix = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(matrix, 1, false, mvpMatrix, 0);

        // Draw the object
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                mFacesList.size() * 3, GLES20.GL_UNSIGNED_SHORT, mFacesBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
