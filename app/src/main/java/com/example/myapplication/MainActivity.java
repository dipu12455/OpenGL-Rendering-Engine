package com.example.myapplication;

import android.os.Bundle;

import android.app.Activity;
import android.view.View;

public class MainActivity extends Activity {

    Engine engine= new Engine(); //create an engine

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!engine.engineInit(this))finish(); //initialization sequence to be launched when activity
        //is created, if failed close activity.
        setContentView(engine.getView()); //set the activity's content view to context provided
        //by engine

    }

    @Override
    protected void onResume() {
        super.onResume();
        engine.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        engine.onPause();
    }

    /**
     * turns on immersive mode.  to get the navigation back, the user needs to swipe up
     * from the bottom of the screen.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);  //this line is api 19+

        }
    }

}
