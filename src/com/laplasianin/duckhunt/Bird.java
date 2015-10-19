package com.laplasianin.duckhunt;

import java.util.Random;

import com.laplasianin.duckhunt.Bird.STATES;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Bird extends AbstractAnimatedObject {

    private static final int MESSAGE_COUNT_NEXT_FRAME = 0;
    private static final int MESSAGE_COUNT_NEW_POSITION = 1;
    private static final int MESSAGE_SWITCH_STATE_TO = 2;

    private static final double SPACE_FOR_BIRDS = 0.5;
    
    public static enum STATES{
    	LIVE, AGONY, DIED;
    }
    
    private STATES state = STATES.LIVE;
    
    private static final int DUCK_SPEED = 5;
	private static final int FALL_SPEED = 50;
	private static final long AGONY_TIME = 500;

    private Point mPosition;
    private Context mContext;

    private int mBirdFrameIfLives;
    private int mMultiplier;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mShiftX;
    private int mShiftY;
    private int mAnimationDelay;
    
    private int[] mFramesLive = { R.drawable.duck1,
            R.drawable.duck2, R.drawable.duck3 };
    
    private int[] mFramesDie = { R.drawable.duck_agony,
            R.drawable.duck_died};
    
    public Bird(Context context, int animationDelay) { 
        mContext = context;
        mAnimationDelay = animationDelay;
        mScreenWidth = 200;
        mScreenHeight = 200;
        mPosition = new Point();
        setStartBirdPosition();
        mBirdFrameIfLives = 0;
        mMultiplier = 1;
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME),
                mAnimationDelay);
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                mAnimationDelay);
    }

	public int getFrameForBird() {
		
		int frame = mFramesLive[0];
		
		switch (state) {
		case AGONY:
			frame = mFramesDie[0];
			break;
			
		case DIED:
			frame = mFramesDie[1];
			break;
			
		case LIVE:
		default:
	        switch (mBirdFrameIfLives) {
	        case 0:
	            frame = mFramesLive[0];
	            break;
	        case 1:
	            frame = mFramesLive[1];
	            break;
	        case 2:
	            frame = mFramesLive[2];
	            break;
	        default:
	            break;
	        }
			break;
		}
        return frame;
    }

    private void countNextBirdFrame() {
		switch (state) {
		case LIVE:
			mBirdFrameIfLives += mMultiplier;
			if (mBirdFrameIfLives == (mFramesLive.length - 1)
					|| (mBirdFrameIfLives == 0)) {
				mMultiplier *= -1;
			}
			break;
		}

		Random rnd = new Random();
		if (rnd.nextInt(20) == 2) {
			mHandler.sendMessageDelayed(
					mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME),
					rnd.nextInt(1000)); 
		} else {
			mHandler.sendMessageDelayed(
					mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME),
					mAnimationDelay);
		}
    }

    public Point getPosition() {
        return mPosition;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MESSAGE_COUNT_NEXT_FRAME) {
                countNextBirdFrame();
                return;
            }
            if (msg.what == MESSAGE_COUNT_NEW_POSITION) {
                countNextPosition();
                return;
            }
            if (msg.what == MESSAGE_SWITCH_STATE_TO) {
                state = (STATES) msg.obj;
                return;
            }
        }
    };

    private void countNextPosition() {
    	switch (state) {
		case LIVE:
			countPositionIfLive();
			break;
		case AGONY:
			countPositionIfAgony();
			break;
		case DIED:
			countPositionIfDied();
		}

        mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                    50);
    }

    private void countPositionIfAgony() {
		// no need to change position, just let state 
    	// switch to DIED after AGONY_TIME
    	Message m = new Message();
    	mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_SWITCH_STATE_TO, STATES.DIED),
                AGONY_TIME);
	}

	private void countPositionIfDied() {
    	mPosition.y += FALL_SPEED;
    	if (mPosition.y > mScreenHeight) {
    		state = STATES.LIVE;
    		setStartBirdPosition();
    	}
	}

	private void countPositionIfLive() {
        Random rnd = new Random(); 
        
        int x = mPosition.x + mShiftX;
        int y = mPosition.y + mShiftY;

        if ((x + mScreenWidth/5) > mScreenWidth) {
        	mShiftX = -rnd.nextInt(DUCK_SPEED)-2;
            mPosition.x += mShiftX + 2;
        } else if (x < mScreenWidth/20) {
        	mShiftX = rnd.nextInt(DUCK_SPEED)+2;
            mPosition.x += mShiftX + 2;
        } else {
            mPosition.x = x;
        }
        
        if (y > mScreenHeight/2) {
        	mShiftY = -rnd.nextInt(DUCK_SPEED)-2;
            mPosition.y += mShiftY + 2;
        } else if (y < 30) {
        	mShiftY = rnd.nextInt(DUCK_SPEED)+2;
            mPosition.y += mShiftY + 2;
        } else {
            mPosition.y = y;
        }
	}

	public void setScreenSize(int screenWidth, int screenHeight) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        setStartBirdPosition();
    }

    public boolean isInverted() {
        if (mShiftX > 0) {
            return false;
        }
        return true;
    }

    private void setStartBirdPosition() {
    	
    	Random rnd = new Random(); 
        if (rnd.nextInt(2) == 0) {
            mPosition.x= mScreenWidth + mScreenWidth/4;
            mShiftX = -rnd.nextInt(DUCK_SPEED)-2;
        } else {
        	mShiftX = rnd.nextInt(DUCK_SPEED)+2;
        	mPosition.x = -mScreenWidth/3;
        }
        mShiftY = rnd.nextInt(DUCK_SPEED)+2;
        mPosition.y = rnd.nextInt(mScreenHeight/2);
    }

    public Bitmap getBitMap() {
        int frame = getFrameForBird();
        if (isInverted()) {
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.preScale(-1, 1);
            Bitmap src = BitmapFactory.decodeResource(mContext.getResources(),
                    frame);
            Bitmap temp =  Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                    src.getHeight(), matrix, false);
            
            return Bitmap.createScaledBitmap(temp, (int) (mScreenWidth * 0.2), (int) (mScreenHeight * 0.15), false);
        }
       
        return Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(mContext.getResources(), frame),
                (int) (mScreenWidth * 0.2), (int) (mScreenHeight * 0.15), false);

    }

    @Override
    public void stop() {
        mHandler.removeMessages(MESSAGE_COUNT_NEXT_FRAME);
        mHandler.removeMessages(MESSAGE_COUNT_NEW_POSITION);
    }

    @Override
    public void start() {
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME),
                mAnimationDelay);
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                mAnimationDelay);
    }

	@Override
	public boolean isDrawOnTop() {
		return false;
	}

	public STATES getState() {
		return state;
	}

	public int getDuckLenght() {
		return mScreenWidth/7;
	}

	public int getDuckHeight() {
		return mScreenHeight/7; 
	}

	public void killInnocentDuck() {
		mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SWITCH_STATE_TO, STATES.AGONY));
	}
}
