package com.example.myapplication;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

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
    float speed = 0.4f;
    float vx = speed; //velocity x component
    float vy = speed; //velocity y component

    //float tuner=1.0f; //i use this to tune the x and y boundary on the screen

    long prevTime = 0L;

    void spriteInit(){
        //randomize the speed
        float min=0.01f;
        float max = speed;
        float delta = 0.0005f; //what is the resolution of the random values
        vx = (float) (Math.random() * (max//this is max value
                - min//this is min value
                + delta)//this is the increment rate
                + min); //this is also min value
        vy = (float) (Math.random() * (max - min + delta) + min);
    }


    void update(long _time){
        prevX = x;
        prevY = y;


        //when the symbol += is used, x automatically becomes dx
        //dx = v * dt, dt is currentTime - theTimeBefore (prevTime)
        // the following formula will make the motion time-based instead of
        //frame rate based, so object gets translated the same distance no matter
        //the frame rate

        float dx=0.0f;
        float dy=0.0f;
        //convert millis to second by dividing because formula needs to be in si unit
        float dt=0.0f;
        if (prevTime!= 0) dt = ((float)(_time-prevTime))/1000; //if prevTime is zero, sprite will jump across different worlds
        dx = vx * dt; //number that comes out of that subtraction will change to float
        dy = vy * dt;
        x += dx;
        y += dy;

        /*Log.i("Sprite.java","time="+Long.toString(_time)+","
                +"prevTime="+Long.toString(prevTime)+","
                +"dt="+Float.toString(dt)+","
                +"dx="+Float.toString(dx)+","
                +"dy="+Float.toString(dy)+","
                +"x="+Float.toString(x)+","
                +"y="+Float.toString(y));
        //Log.i("Tuner",Float.toString(tuner));

        if(_time-prevTime<0) Log.e("Sprite.java",
                "dif="+Long.toString(_time-prevTime));*/

        if (x > 0.464) {
            vx = -vx;
        }
        if (x < -0.464) {
            vx = -vx;
        }
        if (y > 0.985){
            vy = -vy;
        }
        if (y < -0.985){
            vy = -vy;
        }
        prevTime = _time;

        //tuner-=0.0001f;

    }
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
                1.0f, 1.0f, 1.0f, 1.0f,

                1.0f, -1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                1.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f};

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

        spriteInit(); //do variable initializations and other operations
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


}
