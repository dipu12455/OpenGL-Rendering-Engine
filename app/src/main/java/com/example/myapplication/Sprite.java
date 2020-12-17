package com.example.myapplication;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Sprite {

    //Some data size intializations
    /** How many bytes per float. */
    private final int mBytesPerFloat = 4;

    //A sprite consists of two triangles that will resemble together as a square.

    //Triangle vertices are stored in FloatBuffer as follows
    private final FloatBuffer mTriangle1Vertices;
    private final FloatBuffer mTriangle2Vertices;

    //With the diagonal line on the primitive running from top left to bottom right,
    //triangle 1 is the bottom left triangle, and triangle 2 is the top right triangle.

    //FloatBuffer for texture coordinate storage. For my specific sprite arrangement consisting of
    //two triangles, the texture coordinate specification will vary a little from
    //tradition

    private final FloatBuffer mTriangle1TextureCoordinate;
    private final FloatBuffer mTriangle2TextureCoordinate;

    /** This is a handle to our texture data. */
    private int textureHandle;

    float prevX = 0;
    float prevY = 0;
    float x = 0.0f;
    float y = 0.0f;
    float hspeed = 0.025f;
    float vspeed = 0.025f;

    public Sprite(){

        // Define points for these two triangles, these triangles are differently colored for now
        //making color white because it has texture, i don't texture to appear colored

        //triangle 1
        final float[] triangle1VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                -1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f};

        //triangle 2
        final float[] triangle2VerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f,

                1.0f, -1.0f, 0.0f,
                0.0f, 1.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f};

        //texture coordinates
        //image the triangle laying over your texture, assign positions of
        //the texture that you want the triangle's vertices to be in.
        //after that negate all the values, then the texture will appear right side
        //up
        final float[] triangle1TextureCoordinateData =
                {
                        // Front face
                        -0.0f, -1.0f,
                        -0.0f, -0.0f,
                        -1.0f, -0.0f
                };

        final float[] triangle2TextureCoordinateData =
                {
                        // Front face
                        -0.0f, -1.0f,
                        -1.0f, -0.0f,
                        -1.0f, -1.0f
                };

        //this allocates data for storage
        mTriangle1Vertices = ByteBuffer.allocateDirect(triangle1VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangle2Vertices = ByteBuffer.allocateDirect(triangle2VerticesData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        //this is where data is stored in that allocation
        mTriangle1Vertices.put(triangle1VerticesData).position(0);
        mTriangle2Vertices.put(triangle2VerticesData).position(0);

        //this allocates data for storage
        mTriangle1TextureCoordinate = ByteBuffer.allocateDirect(triangle1TextureCoordinateData.length
        * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangle2TextureCoordinate = ByteBuffer.allocateDirect(triangle2TextureCoordinateData.length
                * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();

        //this is where data is stored in that allocation
        mTriangle1TextureCoordinate.put(triangle1TextureCoordinateData).position(0);
        mTriangle2TextureCoordinate.put(triangle2TextureCoordinateData).position(0);
    }

    void loadTextureInSprite(int _textureHandle){
        textureHandle = _textureHandle;
    }

    int getTexture(){
        return textureHandle;
    }
    FloatBuffer getTriangle1(){
        return mTriangle1Vertices;
    }
    FloatBuffer getTriangle2(){
        return mTriangle2Vertices;
    }
    FloatBuffer getTexCoord1(){
        return mTriangle1TextureCoordinate;
    }
    FloatBuffer getTexCoord2(){
        return mTriangle2TextureCoordinate;
    }

    float getX(){
        return x;
    }

    float getY(){
        return y;
    }

    void update(){
        prevX = x;
        prevY = y;

        x += hspeed;
        y += vspeed;

        if (x > 0.7f) {
            hspeed = -hspeed;
        }
        if (x < -0.7f) {
            hspeed = -hspeed;
        }
        if (y > 1.5f){
            vspeed = -vspeed;
        }
        if (y < -1.5f){
            vspeed = -vspeed;
        }
    }
}
