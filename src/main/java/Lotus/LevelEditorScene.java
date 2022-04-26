package Lotus;

import Renderer.Shader;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene
{
    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
            //position    //color
             100.5f, -0.5f, 0.0f,    1.0f, 0.0f, 0.0f, 1.0f,   //Bottom right 0
            -0.5f,  100.5f, 0.0f,    0.0f, 1.0f, 0.0f, 1.0f,   //Top left     1
             100.5f,  100.5f, 0.0f,    1.0f, 0.0f, 1.0f, 1.0f,   //Top Right    2
            -0.5f, -0.5f, 0.0f,    1.0f, 1.0f, 0.0f, 1.0f,   //Bottom Left  3

    };
    private int[] elementArray = {
            // MUST BE IN CCW ORDER

            2, 1, 0, //Top tri
            0, 1, 3  // Bottom Tri
    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;

    public LevelEditorScene()
    {

    }

    @Override
    public void init()
    {
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("Assets/Shaders/default.glsl");
        defaultShader.compile();
        //================================
        //Generate VAO, VBO, EBO buffer object, --> GPU
        //================================

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add vertex attribute pointers
        int positionSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);

    }

    @Override
    public void Update(float dt)
    {
        camera.position.x -= dt * 50.0f;
        // Bind shader program
        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        // Bind VAO in use
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();
    }
}
