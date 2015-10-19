package com.laplasianin.duckhunt;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;

public class Hound extends AbstractAnimatedObject { 

    private static final int MOVE_SPEED = 12;
    private static final int JUMP_SPEED = 15;
    private static final int UP_DOWN_TROPHY_SPEED = 8;
    public final static double HOUND_VERTICAL_POSITION = 0.65;

    private Point mPosition;
    private Context mContext;
    boolean mIsInverted = false;
    private Random mRnd = new Random();
    
    private int ducks;

    private boolean isOnTop = true;
    private int mHoundFrame;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mShiftX = 1;
    private int mAnimationDelay;
    private int mCurrentType = -1;
    
    private int mYForJump = -1;


    private int[] mFramesHoundWalk = { R.drawable.hound_walk_1,
            R.drawable.hound_walk_2, R.drawable.hound_walk_3,
            R.drawable.hound_walk_4 };
    private int[] mFramesHoundSmell = { R.drawable.hound_smell_1,
            R.drawable.hound_walk_2 };
    private int[] mFramesHoundJump = { R.drawable.hound_jump_1,
            R.drawable.hound_jump_2 };
    private int[] mFramesHoundGrin = { R.drawable.hound_grin_1,
            R.drawable.hound_grin_2 };
    private int[] mFramesHoundTrophy1 = { R.drawable.hound_trophy_1, 
    		R.drawable.hound_trophy_1 };
    private int[] mFramesHoundTrophy2 = { R.drawable.hound_trophy_2, 
    		R.drawable.hound_trophy_2 };

    private static final int ANIMATION_DELAY = 100;

    private static final int HOUND_TYPE_WALK = 0;
    private static final int HOUND_TYPE_SMELL = 1;
    private static final int HOUND_TYPE_JUMP = 2;
    private static final int HOUND_TYPE_CRAZY = 3;
    private static final int HOUND_TYPE_GRIN = 4;
    private static final int HOUND_TYPE_GRIN_UP = 7;
    private static final int HOUND_TYPE_GRIN_DOWN = 8;

    private static final int MESSAGE_COUNT_NEXT_FRAME = 0;
    private static final int MESSAGE_COUNT_NEW_POSITION = 1;
    private static final int MESSAGE_CHANGE_TYPE = 2;

    private static final int MIN_DELAY_FOR_WALK = 5000;
    private static final int MIN_DELAY_FOR_SMELL = 1000;
    private static final int MIN_DELAY_FOR_CRAZY = 600;
    private static final int MIN_DELAY_FOR_TROPHY = 1500;

    public Hound(Context context) {
        mContext = context;
        mAnimationDelay = ANIMATION_DELAY;  
        mScreenWidth = 200;
        mScreenHeight = 200;
        mPosition = new Point(); 
        mHoundFrame = 0;
        mCurrentType = nextType();
        mHandler.sendMessageDelayed(
        		mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION), MOVE_SPEED);
    }

	private int nextType() {
		int newType = mCurrentType;
		switch (mCurrentType) {
		case HOUND_TYPE_WALK:
			newType = setSmellType();
			break;
		case HOUND_TYPE_SMELL:
			if ((mRnd.nextInt(2) == 1) || (ducks != 0)) {
				newType = setCrazyType();
			} else {
				newType = HOUND_TYPE_WALK;
				isOnTop = true;
				mHandler.sendMessageDelayed(
						mHandler.obtainMessage(MESSAGE_CHANGE_TYPE),
						mRnd.nextInt(MIN_DELAY_FOR_WALK) + MIN_DELAY_FOR_WALK);
			}
			break;
		case HOUND_TYPE_CRAZY:
			Log.d(NesLiveWallpaperService.TAG, "Start jumpimg");
			newType = HOUND_TYPE_JUMP;
			isOnTop = true;
			mTime = 0;
			mHandler.removeMessages(MESSAGE_COUNT_NEXT_FRAME);
			mHoundFrame = 0;
			break;
		case HOUND_TYPE_JUMP: // after jump
			Log.d(NesLiveWallpaperService.TAG, "Stop jumpimg");
			isOnTop = false;
			newType = HOUND_TYPE_GRIN_UP;
			mPosition.y = (int) (mScreenHeight * 0.8);
			mHoundFrame = 0;
			// change type is handled by position change method
			break;
		case HOUND_TYPE_GRIN_UP:
			isOnTop = false;
			newType = HOUND_TYPE_GRIN;
			mHoundFrame = 0;
			break;
		case HOUND_TYPE_GRIN:
			isOnTop = false;
			newType = HOUND_TYPE_GRIN_DOWN;
			mHoundFrame = 0;
			break;
		case HOUND_TYPE_GRIN_DOWN:
		default:
			ducks = 0;
			newType = HOUND_TYPE_WALK;
			isOnTop = true;
			mHoundFrame = 0;
			setStartHoundPosition();
			mPosition.x = -150;
			mHandler.sendMessageDelayed(
					mHandler.obtainMessage(MESSAGE_CHANGE_TYPE),
					mRnd.nextInt(MIN_DELAY_FOR_WALK) + MIN_DELAY_FOR_WALK);
			if (!mHandler.hasMessages(MESSAGE_COUNT_NEXT_FRAME)) {
				mHandler.sendMessageDelayed(
						mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME),
						mAnimationDelay);
			}
			break;
		}
		return newType;
	}
    
    private int setSmellType() {
        isOnTop = true;
        mHoundFrame = 0;
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_CHANGE_TYPE),
                mRnd.nextInt(MIN_DELAY_FOR_SMELL) + MIN_DELAY_FOR_SMELL);
        return HOUND_TYPE_SMELL;
    }
    
    private int setCrazyType() {
        isOnTop = true;
        mYForJump = mPosition.y;
        mHoundFrame = 0;
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_CHANGE_TYPE), MIN_DELAY_FOR_CRAZY);
        return HOUND_TYPE_CRAZY;
    }

	public int getFrameForDog() {

		switch (mCurrentType) {
		case HOUND_TYPE_WALK:
			return mFramesHoundWalk[mHoundFrame];
		case HOUND_TYPE_SMELL:
			return mFramesHoundSmell[mHoundFrame];
		case HOUND_TYPE_JUMP:
			return mFramesHoundJump[mHoundFrame];
		case HOUND_TYPE_GRIN:
		case HOUND_TYPE_GRIN_UP:
		case HOUND_TYPE_GRIN_DOWN:
			if (ducks == 0) {
				return mFramesHoundGrin[mHoundFrame];
			} else if (ducks == 1) {
				return mFramesHoundTrophy1[0];
			} else {
				return mFramesHoundTrophy2[0];
			}
		case HOUND_TYPE_CRAZY:
			return R.drawable.hound_crazy;
		default:
			return mFramesHoundWalk[0];
		}
	}

	private void countNextHoundFrame() {

		switch (mCurrentType) {
		case HOUND_TYPE_WALK:
			if (mHoundFrame < 3) {
				mHoundFrame++;
			} else {
				mHoundFrame = 0;
			}
			mHandler.sendMessageDelayed(
					mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME),
					mAnimationDelay);
			break;
		case HOUND_TYPE_SMELL:
			if (mHoundFrame == 0) {
				mHoundFrame = 1;
			} else {
				mHoundFrame = 0;
			}
			mHandler.sendMessageDelayed(
					mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME),
					mAnimationDelay);
			break;
		case HOUND_TYPE_JUMP:
			if (mHoundFrame == 0) {
				mHoundFrame = 1;
			} else {
				mHoundFrame = 1;
			}
			mHandler.sendMessageDelayed(
					mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME),
					mAnimationDelay);
			break;
		case HOUND_TYPE_GRIN:
		case HOUND_TYPE_GRIN_UP:
		case HOUND_TYPE_GRIN_DOWN:
			if (mHoundFrame == 0) {
				mHoundFrame = 1;
			} else {
				mHoundFrame = 0;
			}
			mHandler.sendMessageDelayed(
					mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME),
					mAnimationDelay);
			break;
		case HOUND_TYPE_CRAZY:
		default:
			mHoundFrame = 0;
			mHandler.sendMessageDelayed(
					mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME), 500);
			break;
		}

	}

    public Point getPosition() {
        return mPosition;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MESSAGE_COUNT_NEXT_FRAME) {
                countNextHoundFrame();
            }
            if (msg.what == MESSAGE_COUNT_NEW_POSITION) {
                countNextPosition();
            }
            if (msg.what == MESSAGE_CHANGE_TYPE) {
                mCurrentType = nextType();
            }
        }

    };

	private int mTime;

	private int mShiftY;

    private void countNextPosition() {

        switch (mCurrentType) {
        case HOUND_TYPE_WALK:
            if (mPosition.x + mShiftX > (mScreenWidth*0.75)) {
                mShiftX = -1;
                mIsInverted = true;
            }
            if (mPosition.x + mShiftX < 0) {
                mShiftX = 1;
                mIsInverted = false;
            }
            mPosition.x += mShiftX;
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                    MOVE_SPEED);
            break;

        case HOUND_TYPE_JUMP:
            int oldShift = mShiftY;
            mShiftY = mScreenHeight/13 - (10*mTime-5);
            
            mPosition.y -= mShiftY;
            mTime += 1;
            //start falling
            if ((mShiftY < 0) && (oldShift > 0)) {
            	mHandler.sendMessage(
            			mHandler.obtainMessage(MESSAGE_COUNT_NEXT_FRAME));
            }
            //jump is over
            if ((mShiftY < 0) && (mPosition.y >= (mYForJump-mScreenHeight*0.15))) {
            	mHandler.sendMessage(
            			mHandler.obtainMessage(MESSAGE_CHANGE_TYPE));
            }
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                    JUMP_SPEED);
            break;
            
        case HOUND_TYPE_GRIN_UP:
        	if (mPosition.y > mScreenHeight*0.49) {
        		mPosition.y-= 2;
        	} else {
        		mHandler.sendMessage( // to grin
                        mHandler.obtainMessage(MESSAGE_CHANGE_TYPE)); 
        		mHandler.sendMessageDelayed(  // to grin down in future
                        mHandler.obtainMessage(MESSAGE_CHANGE_TYPE),
                        mRnd.nextInt(MIN_DELAY_FOR_TROPHY) + MIN_DELAY_FOR_TROPHY);
        	}
        	mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                    UP_DOWN_TROPHY_SPEED);
        	break;
        	
        case HOUND_TYPE_GRIN:
        	mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                    MOVE_SPEED);
        	break;
        	
        case HOUND_TYPE_GRIN_DOWN:
        	if (mPosition.y < mScreenHeight) {
        		mPosition.y+= 2;
        	} else {
        		mHandler.sendMessage(
        				mHandler.obtainMessage(MESSAGE_CHANGE_TYPE));
        	}
        	mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                    UP_DOWN_TROPHY_SPEED);
        	break;

        case HOUND_TYPE_SMELL:
        default:
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                    MOVE_SPEED);
            break;
        }
    }

    public void setScreenSize(int screenWidth, int screenHeight) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        setStartHoundPosition(); 
    }

    public boolean isInverted() {
        return mIsInverted;
    }

    private void setStartHoundPosition() {
    	mPosition.y = ((int) (mScreenHeight * HOUND_VERTICAL_POSITION));
    	if (mRnd.nextInt(2) == 0) {
    		mPosition.x = -mScreenWidth/5;
    		mIsInverted = false;
    	} else {
    		mPosition.x = mScreenWidth/5*6;
    		mIsInverted = true;
    	}
    }

    public Bitmap getBitMap() {
        int frame = getFrameForDog();
        if (isInverted()) {
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.preScale(-1, 1);
            Bitmap src = BitmapFactory.decodeResource(mContext.getResources(),
                    frame);
            Bitmap temp = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                    src.getHeight(), matrix, false);
            return Bitmap.createScaledBitmap(temp, (int) (mScreenWidth * 0.3), (int) (mScreenHeight * 0.2), false);
        }
        return Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(mContext.getResources(), frame),
                (int) (mScreenWidth * 0.3), (int) (mScreenHeight * 0.2), false);

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

	public boolean isDrawOnTop() {
		return isOnTop;
	}

	public void catchDuck() {
		if ((mCurrentType == HOUND_TYPE_WALK) || (mCurrentType == HOUND_TYPE_SMELL)) {
			ducks++;
			mHandler.removeMessages(MESSAGE_CHANGE_TYPE);
			mCurrentType = setSmellType();
		}
	}
}
