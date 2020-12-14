package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class Engine {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private GLSurfaceView mGLSurfaceView;
    private Context context;
    EngineRenderer engineRenderer = new EngineRenderer();
    private final float TOUCH_SCALE_FACTOR = 180.0f/320;
    private float previousX;
    private float previousY;


    boolean engineInit(Context _context) {
        context = _context;
        mGLSurfaceView = new GLSurfaceView(context);

        if (detectOpenGLES30()) {
            // Tell the surface view we want to create an OpenGL ES 3.0-compatible
            // context, and set an OpenGL ES 3.0-compatible renderer.
            mGLSurfaceView.setEGLContextClientVersion(3);
            // Set the renderer to our demo renderer, defined below.
            mGLSurfaceView.setRenderer(engineRenderer);

        } else {
            // This is where you could create an OpenGL ES 2.0 and/or 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            Log.e("HelloTriangle", "OpenGL ES 3.0 not supported on device. Exiting...");
            return false;
        }
        return true;
    }

    private boolean detectOpenGLES30() {
        Activity activity = (Activity) context; //use context passed into engine to retrieve
        //activity object.
        ActivityManager am =
                (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x30000);
    }

    GLSurfaceView getView() {
        return mGLSurfaceView;
    }

    void onResume() {// The activity's onResume() must call the GL surface view's onResume()
        mGLSurfaceView.onResume();
    }

    void onPause() {// The activity's onPause() must call the GL surface view's onPause()
    mGLSurfaceView.onPause();
    }

    void touchActivated(MotionEvent e){
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                engineRenderer.addAngle((dx + dy) * TOUCH_SCALE_FACTOR);

                break;

        }

        previousX = x;
        previousY= y;
    }

}
