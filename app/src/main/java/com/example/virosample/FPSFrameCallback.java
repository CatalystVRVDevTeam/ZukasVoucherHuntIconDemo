package com.example.virosample;



import android.view.Choreographer;

import java.util.ArrayList;
import java.util.List;

/**
 This Function provides a way for the program to run a function every frame, this allows the program smoothly animate things.
 */
public class FPSFrameCallback implements Choreographer.FrameCallback {

    // The main Controller.
    private  IconController thingToCallUpdateOn;

    public  FPSFrameCallback(IconController iconClass) {
        thingToCallUpdateOn = iconClass;
    }

    @Override
    public  void doFrame(long frameTimeNanos)
    {
        thingToCallUpdateOn.Update();
        Choreographer.getInstance().postFrameCallback(this);
    }

}