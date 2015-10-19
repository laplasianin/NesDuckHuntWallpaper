package com.laplasianin.duckhunt;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

public class Drawer {

    private final int DELAY_FOR_BIG_BIRD_ANIMATION = 300;
    private final int DELAY_FOR_HOUND_ANIMATION = 200;

    private Bitmap mBackGround;
    private Bitmap mDuck1Bitmap;
    private Bitmap mDuck2Bitmap;
    private Bitmap mHoundBitmap;

    private AbstractAnimatedObject mHound;
    private ArrayList<AbstractAnimatedObject> mDucks = new ArrayList<AbstractAnimatedObject>();

    private Context mContext;

    private int mMode;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mNumberOfDucks;

    public Drawer(Context context, int style) {

        mContext = context;
        mMode = style;
        mHound = new Hound(mContext, mMode);
        prepareBitmaps();
    }

    void prepareNewSizes(int screenWidth, int screenHeight) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mHound.setScreenSize(screenWidth, screenHeight);
        prepareBitmaps();
    }

    private void prepareBitmaps() {
        mBackGround = Bitmap.createBitmap(BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.background));
    }

    public Canvas draw(Canvas canvas) {

        // BACKGROUND
        canvas.drawBitmap(mBackGround, 0, 0, new Paint());


//        if (mNumberOfDucks > 0) {
//            // BIG BIRD 1
//            Point positionBigBird1 = mDucks.get(0).getPositionForHound();
//            if (mDuck1Bitmap != null) {
//                mDuck1Bitmap.recycle();
//            }
//            mDuck1Bitmap = mDucks.get(0).getBitMap();
//            canvas.drawBitmap(mDuck1Bitmap, positionBigBird1.x,
//                    positionBigBird1.y, new Paint());
//        }
//
//        if (mNumberOfDucks > 1) {
//            // BIG BIRD 2
//            Point positionBigBird2 = mDucks.get(1).getPositionForHound();
//            if (mDuck2Bitmap != null) {
//                mDuck2Bitmap.recycle();
//            }
//            mDuck2Bitmap = mDucks.get(1).getBitMap();
//            canvas.drawBitmap(mDuck2Bitmap, positionBigBird2.x,
//                    positionBigBird2.y, new Paint());
//        }

        // HOUND
        Point positionHound = mHound.getPositionForHound();
        if (mHoundBitmap != null) {
            mHoundBitmap.recycle();
        }
        mHoundBitmap = mHound.getBitMap();
        canvas.drawBitmap(mHoundBitmap, positionHound.x, positionHound.y,
                new Paint());
        return canvas;
    }

    public void performTouchEvent(int x, int y) {
        // TODO Auto-generated method stub
    }

    public void setNewNumberOfBigBirds(int numberOfBigBirds) {
        mNumberOfDucks = numberOfBigBirds;
    }

    public void stopAllObjects() {
//        for (AbstractAnimatedObject d : mDucks) {
//            d.stop();
//        }
        mHound.stop();
    }

    public void startAllObjects() {
//        for (AbstractAnimatedObject d : mDucks) {
//            d.start();
//        }
        mHound.start();
    }
}
