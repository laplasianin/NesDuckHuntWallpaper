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

	private Bitmap mBackGround;

	private Hound mHound;
	private ArrayList<Bird> mDucks = new ArrayList<Bird>();

	private final int DELAY_FOR_DUCK_ANIMATION = 150;

	private Context mContext;

	private int mScreenWidth;
	private int mScreenHeight;

	public Drawer(Context context) {
		mContext = context;
		mScreenWidth = 100;
		mScreenHeight = 100;
		mHound = new Hound(mContext);
		mDucks.add(new Bird(mContext, DELAY_FOR_DUCK_ANIMATION));
//		prepareBitmaps();
	}

	void prepareNewSizes(int screenWidth, int screenHeight) {
		mScreenWidth = screenWidth;
		mScreenHeight = screenHeight;
		mHound.setScreenSize(screenWidth, screenHeight);
		for (AbstractAnimatedObject duck : mDucks) {
			duck.setScreenSize(screenWidth, screenHeight);
		} 
		prepareBitmaps();
	}

	private void prepareBitmaps() {
		mBackGround = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.background), mScreenWidth, mScreenHeight, false);

	}

	public Canvas draw(Canvas canvas) {

		// DUCK
		for (AbstractAnimatedObject duck : mDucks) {
			Point positionDuck1 = duck.getPosition();
			canvas.drawBitmap(duck.getBitMap(), positionDuck1.x, positionDuck1.y,
					new Paint());
		}

		// BACKGROUND if dog in front
		if (mHound.isDrawOnTop()) {
			canvas.drawBitmap(mBackGround, 0, 0, new Paint());
		}

		// HOUND
		Point positionHound = mHound.getPosition();
		canvas.drawBitmap(mHound.getBitMap(), positionHound.x, positionHound.y,
				new Paint());

		// BACKGROUND in the case if dog at back
		if (!mHound.isDrawOnTop()) {
			canvas.drawBitmap(mBackGround, 0, 0, new Paint());
		}

		return canvas;
	}

	public void performTouchEvent(int x, int y) {
		for (Bird duck : mDucks) {
			Point duckPoint = duck.getPosition();
			if ((duck.getState() == Bird.STATES.LIVE) && (x >= duckPoint.x)
					&& (y >= duckPoint.y)
					&& (x <= duckPoint.x + duck.getDuckLenght())
					&& (y <= duckPoint.y + duck.getDuckHeight())) {
				duck.killInnocentDuck();
				mHound.catchDuck();
			}
		}
	}

	public void setNewNumberOfDucks(int numberOfDucks) {
		if (mDucks.size() < numberOfDucks) {
			while (mDucks.size() != numberOfDucks) {
				mDucks.add(new Bird(mContext, DELAY_FOR_DUCK_ANIMATION));
				mDucks.get(mDucks.size()-1).setScreenSize(mScreenWidth, mScreenHeight);
			}
		} else if (mDucks.size() > numberOfDucks) {
			while (mDucks.size() != numberOfDucks) {
				mDucks.remove(mDucks.size()-1);
			}
		}
	}

	public void stopAllObjects() {
		for (AbstractAnimatedObject d : mDucks) {
			d.stop();
		}
		mHound.stop();
	}

	public void startAllObjects() {
		for (AbstractAnimatedObject d : mDucks) {
			d.start();
		}
		mHound.start();
	}
}
