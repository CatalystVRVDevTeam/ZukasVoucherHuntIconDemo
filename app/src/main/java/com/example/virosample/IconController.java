package com.example.virosample;

import android.graphics.Color;
import android.view.Choreographer;

import com.viro.core.Material;
import com.viro.core.Object3D;
import com.viro.core.Quaternion;
import com.viro.core.Vector;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import java.util.ArrayList;
import java.util.Random;

// The Overall controller for the animation. This provides most of the functionality for the animation that cannot be passed to the individual icon classes.
public class IconController  {

    // The big Icon in the middle.
    private Object3D centerObj;

    // The flashing effect.
    private  Object3D flashPlane;

    // The smaller spinning icons.
    private ArrayList<SmallSpinningObject> spinningObjs = new ArrayList<>();

    // The money falling from the sky.
    private ArrayList<FallingMoneyObj> cash = new ArrayList<>();

    // The class handling the Update Functionality.
    private static FPSFrameCallback fpsFrameCallback;

    // The position the big icon in the center is located at.
    private Vector centerPos;

    // The amount the central flash plane has scaled.
    private float scaleAmount;

    // Is the scale currently increasing?
    private boolean scaleIncreasing;

    // The direction the camera should be looking in (NOTE: broke atm)
    private  Vector lookTarget;

    // Timer counting the time since the beginning of the program (NOTE: unreliable due to an assumed 60fps)
    private  float timeSinceStart = 0;

    // boolean to ensure the first event is not called multiple times.
    private  boolean firstEventHappened;

    // Are the small spinning objects currently moving to the center.
    private  boolean movingToCenter;

    // Have the small spinning objects finished moving towords the center.
    private  boolean movingToCenterOver;

    //  Are the small spinning objects currently moving away from the center.
    private  boolean movingAwayFromCenter;

    // has the event to start expanding happened?
    private  boolean hasExpandedYet;

    // continuously increasing counter every frame, allows class to call 25fps function every other frame.
    private  int twentyFiveFpsCounter;

    // has the big icon started bouncing yet.
    private  boolean bigIconBounce;

    // number of frames since the twentyfivefpscounter started counting (NOTE: THIS COUNTER IS ACTUALLY 30FPS)
    private  int progressThroughBounce;

    // List of integer sizes for the bouncing big icon.
    private float[] listOfScales = new float[] {0,52,104,208,265,301,310,293,218,180,155,145,152,171,195,219,237,245,243,233,218,204,192,186,185,190,198,208,215,220,221,219,214,209,204,200,203,206,209,211,209,207};

    public IconController (Vector cameraPos) {
        fpsFrameCallback = new FPSFrameCallback(this);
        Choreographer.getInstance().postFrameCallback(fpsFrameCallback);
        lookTarget = cameraPos;
    }

    // Update function, called every frame.
    public void Update () {
       ScaleUpAndDownFlash();
        timeSinceStart += 0.0166;
        if(timeSinceStart > 20 && !firstEventHappened) {
            firstEventHappened = true;
            movingToCenter = true;
        }
        if(timeSinceStart >  24 && movingToCenterOver == false) {
        movingToCenterOver = true;
            movingAwayFromCenter = true;
            movingToCenter = false;
        }
        if(timeSinceStart > (float) 24.1 && hasExpandedYet == false) {
            hasExpandedYet = true;
            flashPlane.setScale(new Vector(0,0,0));
            flashPlane.setPosition(new Vector(100,100,100));
            bigIconBounce = true;
            for (int i = 0; i < cash.size();i ++ ) {
                cash.get(i).beginFalling(centerObj.getPositionRealtime(),ThreadLocalRandom.current().nextFloat()*4);
            }
        }
        RotateSpinningIcons();
        twentyFiveFpsCounter++;
        if(twentyFiveFpsCounter % 2 == 0 && bigIconBounce)updateTwentyFive();
    }

    // called every other frame.
    private void updateTwentyFive () {
        progressThroughBounce++;
        if(progressThroughBounce > listOfScales.length - 1) return;
        float newScale =listOfScales[progressThroughBounce] / 5000;
        centerObj.setScale(new Vector(newScale,newScale,newScale));
    }


    // Function called every frame to animate the spinning icons depending on what stage of the animation this class is currently at.
    private  void RotateSpinningIcons () {
        if(spinningObjs.size() == 0) return;
        for (int i = 0; i < cash    .size(); i++){
            cash.get(i).Update();
        }
        try {
            if (movingToCenter == true) {

                if (centerObj == null) return;
                for (int i = 0; i < spinningObjs.size(); i++) {
                    Vector target = spinningObjs.get(i).parent.getPositionRealtime().interpolate(centerObj.getPositionRealtime().add(new Vector(-0.1, 0, 0)), (timeSinceStart - 20) / 4);
                    spinningObjs.get(i).parent.setPosition(target);
                    Vector pivotTarget = spinningObjs.get(i).parent.getRotationPivot().interpolate(new Vector(0, 0, 0), (timeSinceStart - 20) / 4);
                    spinningObjs.get(i).parent.setRotationPivot(pivotTarget);
                }
            } else {
                if(movingAwayFromCenter) {
                    for (int i = 0; i < spinningObjs.size(); i++) {
                        Vector target = spinningObjs.get(i).startPoint;
                        Vector mainPoint = spinningObjs.get(i).parent.getPositionRealtime().interpolate(target.scale(10), (timeSinceStart - 24) / 4);
                        spinningObjs.get(i).parent.setPosition(mainPoint);
                    }
                }
                else {
                    for (int i = 0; i < spinningObjs.size(); i++) {
                        spinningObjs.get(i).setRotateAmount(spinningObjs.get(i).getRotateAmount() + (float) 0.05);
                        Vector vec = new Vector(0, spinningObjs.get(i).getRotateAmount(), 0);
                        spinningObjs.get(i).parent.setRotation(vec);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }


    // Called every frame to control the current scale of the big flash.
    private  void ScaleUpAndDownFlash () {
        if(flashPlane == null) return;
        if (!movingToCenter && !movingAwayFromCenter) {
            if (scaleIncreasing) {
                scaleAmount += 0.01f;
                if (scaleAmount > 1.5) scaleIncreasing = false;
            } else {
                scaleAmount -= 0.01f;
                if (scaleAmount < 0.75) scaleIncreasing = true;
            }
        }
        else {
            scaleAmount += 0.4f;
        }
        flashPlane.setScale(new Vector(scaleAmount, scaleAmount, scaleAmount));
    }

    // Initialises any 3D objects spawned into the scene based on their type.
    public void addObject (Object3D newObj ,OBJTYPE type) {
        if(centerPos == null) centerPos = newObj.getPositionRealtime();
        else newObj.setPosition(centerPos);
        if(type == OBJTYPE.flash) {
            flashPlane = newObj;
            flashPlane.setScale(new Vector(1,1,1));
            flashPlane.setPosition(flashPlane.getPositionRealtime().add(new Vector(0,0.3,0)));
            Vector rotateTo = new Vector(1,0,0);
            flashPlane.setRotation(rotateTo);
            Material mat = newObj.getMaterials().get(0);
            mat.setLightingModel(Material.LightingModel.CONSTANT);
            mat.setDiffuseColor(Color.WHITE);
            mat.setBloomThreshold(0.1f);
            newObj.getGeometry().setMaterials(Arrays.asList(mat));
            Vector direction = (lookTarget.subtract(newObj.getPositionRealtime())).normalize();
            newObj.setRotation(direction);
        }
        if (type == OBJTYPE.main) {
            centerObj =  newObj;
            centerObj.setPosition(centerObj.getPositionRealtime().add(new Vector(0,0.3,0)));
            centerObj.setScale(new Vector(0,0,0));
        }
        if (type == OBJTYPE.smallIcon) {
            SmallSpinningObject spinObj = new SmallSpinningObject( newObj);
            spinningObjs.add(spinObj);
            Vector newPos = new Vector(ThreadLocalRandom.current().nextDouble(-0.5,0.5),ThreadLocalRandom.current().nextDouble(0,0.5),ThreadLocalRandom.current().nextDouble(-0.5,0.5));
            newObj.setPosition(newObj.getPositionRealtime().add(newPos));
            newObj.setRotationPivot( new Vector(0.2,0,0));
            newObj.setScale(new Vector(0.005,0.005,0.005));
            spinObj.setRotateAmount(ThreadLocalRandom.current().nextFloat());
            spinObj.startPoint = newObj.getPositionRealtime();
        }
        if(type == OBJTYPE.money) {
            cash.add(new FallingMoneyObj(newObj));
            newObj.setScale( new Vector(.1f,0.1,0.1));
            newObj.setPosition(new Vector(100,100,100));
        }
    }

    // Allows program to differentiate between different types of 3D objects
    // This is determined by the material name (couldn't find a better way to do it).
    public enum OBJTYPE {
        main,
        flash,
        smallIcon,
        money
    }




}
