package com.laplasianin.duckhunt;

import android.graphics.Bitmap;
import android.graphics.Point;

public abstract class AbstractAnimatedObject extends MovableObject {

    public abstract Point getPosition();

    public abstract Bitmap getBitMap();

    public abstract void setScreenSize(int screenWidth, int screenHeight);
    
    public abstract boolean isDrawOnTop();

}
