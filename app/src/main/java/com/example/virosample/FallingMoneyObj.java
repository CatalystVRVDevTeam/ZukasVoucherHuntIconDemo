package com.example.virosample;

import com.viro.core.Object3D;
import com.viro.core.Vector;
import java.util.concurrent.ThreadLocalRandom;

// This class allows the Money objects to fall after a Random amount of time has passed.
public class FallingMoneyObj
{
    // The Actual ingame object referenced by this class
    private Object3D parent;

    // The place the money intends to land.
    private Vector target;

    // Is the money currently falling?
    private  boolean falling;

    // The start point of the class.
    private  Vector start;

    // How long have  you waited so far?
    private float time = 0;

    // Has the money hit the floor yet?
    private  boolean hitFloor;

    // How long since the money began falling.
    private  float timeSinceFalling;

    // How long must you wait?
    private  float timeToWait = 2;

    public FallingMoneyObj (Object3D input) {
        parent= input;
    }

    // Prepare to begin falling.
    public void beginFalling (Vector center, float timeUntilFloor) {
        start = center.add( new Vector(ThreadLocalRandom.current().nextDouble(-0.5,0.5),2,ThreadLocalRandom.current().nextDouble(-0.5,0.5)));
            parent.setPosition(start);
            parent.setScale(new Vector(0.05,0.05,0.05));
            falling = true;
            target = start.subtract(new Vector(0,3,0));
            parent.setRotation(new Vector(ThreadLocalRandom.current().nextFloat(),ThreadLocalRandom.current().nextFloat(),ThreadLocalRandom.current().nextFloat()));
            timeToWait = timeUntilFloor;
    }

    // wait until enough time has passed.
    public void Update () {
        if(!falling) return;
        time += 0.023;
        if ( hitFloor || time < timeToWait) return;
        timeSinceFalling += 0.023;
        if(timeSinceFalling > 4) {
         hitFloor = true;
         parent.setRotation(new Vector(0,0,0));
        }
        parent.setPosition(start.interpolate(target,timeSinceFalling / 4));
    }
}
