package com.example.virosample;

import com.viro.core.Object3D;
import com.viro.core.Vector;

// This class allows each spinning icon to hold data about itself.
public class SmallSpinningObject {

    // The main object this class is controlling.
    public Object3D parent;

    // The amount the object has rotated so far.
    private  float rotateAmount;

    // The point this object started at.
    public Vector startPoint;

    public SmallSpinningObject (Object3D source) {
        parent = source;
    }

    public void setRotateAmount (float input) {
        rotateAmount = input;
    }

    public float getRotateAmount () {return rotateAmount;}
}
