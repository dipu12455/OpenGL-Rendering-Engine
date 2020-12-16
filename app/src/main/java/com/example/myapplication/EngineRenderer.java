package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.view.MotionEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 *
 *
 * This code is borrowed from http://www.learnopengles.com/android-lesson-one-getting-started/
 *
 *
 * This code has been changed to use GLES30 instead of GLES20 to setup a render for 3.0 instead of 2.0
 *  Otherwise it remains unchanged.
 *   JW  3/18/15
 *
 */
public class EngineRenderer implements GLSurfaceView.Renderer
{
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
    private float[] mProjectionMatrix = new float[16];

    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];

    /** Store our model data in a float buffer. */
    private final FloatBuffer mTriangle1Vertices;
    private final FloatBuffer mTriangle2Vertices;
    private final FloatBuffer mTriangle3Vertices;
    float amount=0.0f;

    /** This will be used to pass in the transformation matrix. */
    private int mMVPMatrixHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model color information. */
    private int mColorHandle;

    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    /** How many elements per vertex. */
    private final int mStrideBytes = 7 * mBytesPerFloat;

    /** Offset of the position data. */
    private final int mPositionOffset = 0;

    /** Size of the position data in elements. */
    private final int mPositionDataSize = 3;

    /** Offset of the color data. */
    private final int mColorOffset = 3;

    /** Size of the color data in elements. */
    private final int mColorDataSize = 4;

    //Variables for touch input
    private float angleInDegrees=45;

    //texture data

    //context
    Context context;
    /** Store our model data in a float buffer. */
    private final FloatBuffer mCubeTextureCoordinates;

    /** This will be used to pass in the texture. */
    //Only one of this kind of variable is used
    private int mTextureUniformHandle;

    /** This will be used to pass in model texture coordinate information. */
    //Only one of this kind will be used as well, this variable is used as a mediary
            //to transfer values from texturecoordinate buffer (floatbuffer) to
            //the shader.
    private int mTextureCoordinateHandle;

    /** Size of the texture coordinate data in elements. */
    //keep all model texture coordinate true to this specification
    //so i'll keep this as not specific to models as well
    private final int mTextureCoordinateDataSize = 2;

    /** This is a handle (representer) to our texture data. */
    //this is also stored for each model
    private int mTexture01;


    //Initialize sprites
    Sprite sprite01=new Sprite();

    /**
     * Initialize the model data.
     */
    public EngineRenderer(Context _context)
    {
        // Define points for equilateral triangles.

        // This triangle is red, green, and blue.
        final float[] triangle1VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.25f, 0.0f,
                1.0f, 0.0f, 0.0f, 0.5f,

                0.5f, -0.25f, 0.0f,
                0.0f, 0.0f, 1.0f, 1.0f,

                0.0f, 0.559016994f, 0.0f,
                0.0f, 1.0f, 0.0f, 1.0f};

        // This triangle is yellow, cyan, and magenta.
        final float[] triangle2VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.25f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                0.0f, 1.0f, 1.0f, 1.0f,

                0.0f, 0.559016994f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f};

        // This triangle is white, gray, and black.
        final float[] triangle3VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -0.5f, -0.25f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                0.5f, -0.25f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f,

                0.0f, 0.559016994f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f};

        //texture coordinates
        final float[] cubeTextureCoordinateData =
                {
                        // Front face
                        0.0f, 0.0f,
                        0.0f, 1.0f,
                        1.0f, 0.0f
                };

        // Initialize the buffers.
        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangle2Vertices = ByteBuffer.allocateDirect(triangle2VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangle3Vertices = ByteBuffer.allocateDirect(triangle3VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTriangle1Vertices.put(triangle1VerticesData).position(0);
        mTriangle2Vertices.put(triangle2VerticesData).position(0);
        mTriangle3Vertices.put(triangle3VerticesData).position(0);

        mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length *
                mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

        //Receive context and then load all textures
        context = _context;

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        //Don't load texture in the class constructor, use the method onSurfaceCreated, which
        //should be used to initialize other opengl related objects as well

        // Load the texture, mTextureDataHandle is simply an int that represents a texture stored in
        //opengl
        mTexture01 = loadTexture(context, R.drawable.steve);

        //texture for the first sprite, texture is stored in itself, but loading has to be done
        //here
        sprite01.loadTextureInSprite(loadTexture(context,R.drawable.steve));

        // Set the background clear color to gray.
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 1.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        //Matrix.translateM(mViewMatrix, 0, -1.0f, -1.0f, 0.0f);
        final String vertexShader =

                "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.

                        + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
                        + "attribute vec4 a_Color;        \n"		// Per-vertex color information we will pass in.

                        + "attribute vec2 a_texCoord;     \n"       // Per-vertex texture coordinate will pass in

                        + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

                        + "varying vec2 v_texCoord;       \n"       //This will be passed into the fragment shader.

                        + "void main()                    \n"		// The entry point for our vertex shader.
                        + "{                              \n"
                        + "   v_texCoord = a_texCoord;    \n"
                        + "   v_Color = a_Color;          \n"		// Pass the color through to the fragment shader.
                        // It will be interpolated across the triangle.
                        + "   gl_Position = u_MVPMatrix   \n" 	// gl_Position is a special variable used to store the final position.
                        + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                        + "}                              \n";    // normalized screen coordinates.

        final String fragmentShader =

                "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                        // precision in the fragment shader.
                        + "uniform sampler2D u_Texture; \n" //the input texture

                        + "varying vec4 v_Color;          \n"		// This is the color from the vertex shader interpolated across the
                        + "varying vec2 v_texCoord;       \n"       // interpolated texture coordinate
                        // triangle per fragment.
                        + "void main()                    \n"		// The entry point for our fragment shader.
                        + "{                              \n"
                        + "   gl_FragColor = v_Color * texture2D(u_Texture, v_texCoord);     \n"		// Pass the color directly through the pipeline.
                        + "}                              \n";

        // Load in the vertex shader.
        int vertexShaderHandle = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES30.glShaderSource(vertexShaderHandle, vertexShader);

            // Compile the shader.
            GLES30.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(vertexShaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES30.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        if (vertexShaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }

        // Load in the fragment shader shader.
        int fragmentShaderHandle = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES30.glShaderSource(fragmentShaderHandle, fragmentShader);

            // Compile the shader.
            GLES30.glCompileShader(fragmentShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES30.glGetShaderiv(fragmentShaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                GLES30.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        if (fragmentShaderHandle == 0)
        {
            throw new RuntimeException("Error creating fragment shader.");
        }

        // Create a program object and store the handle to it.
        int programHandle = GLES30.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES30.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES30.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES30.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES30.glBindAttribLocation(programHandle, 1, "a_Color");
            GLES30.glBindAttribLocation(programHandle,2,"a_texCoord");

            // Link the two shaders together into a program.
            GLES30.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES30.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        // Set program handles. These will later be used to pass in values to the program.
        mMVPMatrixHandle = GLES30.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES30.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES30.glGetAttribLocation(programHandle, "a_Color");

        mTextureUniformHandle = GLES30.glGetUniformLocation(programHandle, "u_Texture");
        mTextureCoordinateHandle = GLES30.glGetAttribLocation(programHandle,"a_texCoord");

        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        //Any changes made to this texture unit will be picked up by shader and rendered, which means
        //we are telling the shader to look at this unit if it needs anything, via this uniform
        //variable
        GLES30.glUniform1i(mTextureUniformHandle, 0); //the number zero means texture unit zero
        //this method is also independent, no matter where the function glActiveTexture is called doens't
        //matter, the 2nd parameter still routes this method to the correct texture unit. Just be sure to
        //use the same texture unit everywhere else. This number should match with the texture unit that
        //has been activated it the glActiveTexture method

        // Tell OpenGL to use this program when rendering. when all the program handles have been
        //initialized above, then tell opengl to use this program.
        GLES30.glUseProgram(programHandle);

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // Set the OpenGL viewport to the same size as the surface.
        GLES30.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;


        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        //set an orthographic projection for 3d game rendering, we dont want size changing on z axis
        //Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        //float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        amount =0.001f;

        //the order of transformation should be translate, rotate and scale

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        float scaleAmount = 0.1f;
        Matrix.scaleM(mModelMatrix,0,scaleAmount,scaleAmount,scaleAmount);
        drawTriangle(sprite01.getTriangle1(),sprite01.getTexture(),sprite01.getTexCoord1());
        drawTriangle(sprite01.getTriangle2(),sprite01.getTexture(),sprite01.getTexCoord2());
        //drawTriangle(mTriangle1Vertices,mTexture01,mCubeTextureCoordinates);

        // Draw one translated a bit down and rotated to be flat on the ground.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 45.0f, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(mTriangle2Vertices,mTexture01,mCubeTextureCoordinates);

        // Draw one translated a bit to the right and rotated to be facing to the left.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 45.0f, 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        drawTriangle(mTriangle3Vertices,mTexture01,mCubeTextureCoordinates);
    }

    /**
     * Draws a triangle from the given vertex data.
     *
     * @param aTriangleBuffer The buffer containing the vertex data.
     */
    private void drawTriangle(final FloatBuffer aTriangleBuffer,int _textureHandle,
                              FloatBuffer _textureCoordinates)
    {
        //Now is the rendering sequence when drawing textures
        //1. set an active texture unit, for this one its unit 0. Always use only one texture unit.
        //a texture unit is like a holding place for a texture currently being used to draw.
        //Then when drawing another texture, bind that texture to this unit overwriting the
        //previous one, then shader simply continues to render that, naturally reflecting the change.

        // Set the active texture unit to texture unit 0. texture uniform has already been told to use
        //this texture unit to retrieve texture from
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);

        //Bind the texture to this unit
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, _textureHandle);
        //The above two methods are placed in the rendering loop.
        //1. render one triangle, bind texture to the unit, render the texture
        //2. render another triangle, bind texture to the unit, render the texture
        //..continue, each time it is only the mTextureDataHandle variable that changes, since
        //it is an integer pointing to different textures stored in opengl. specify what texture
        //you want right after the particular triangle has been drawn

        // Pass in the position information
        aTriangleBuffer.position(mPositionOffset);
        GLES30.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES30.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES30.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        aTriangleBuffer.position(mColorOffset);
        GLES30.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES30.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES30.glEnableVertexAttribArray(mColorHandle);

        // Pass in the texture coordinate information
        _textureCoordinates.position(0);
        GLES30.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES30.GL_FLOAT, false,
                0, _textureCoordinates);

        GLES30.glEnableVertexAttribArray(mTextureCoordinateHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES30.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
    }

    void addAngle(float _angle){
        angleInDegrees += _angle;
    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES30.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

}