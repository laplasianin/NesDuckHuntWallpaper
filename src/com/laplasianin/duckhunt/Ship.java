package com.laplasianin.duckhunt;

import java.util.Random;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;

public class Ship extends MovableObject {

    private static final int MOVE_SPEED = 500;
    private static final int MESSAGE_COUNT_NEW_POSITION = 1;
    private static final int MESSAGE_START_NEW_TRIP = 2;
    private static final double TOP_POSITION_SHIP = 0.57;
    private static final double MAX_SHIFT_SHIP_TO_BOTTOM = 0.04;

    private boolean mMovable;
    private Point mPosition;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mShiftX;
    private int mShiftY;
    private int mDestinationX = 0;
    private int mDestinationY = 0;
    private int mDelayBetweenTrips;

    public Ship(Context context, boolean movable) {
        mScreenWidth = 200;
        mScreenHeight = 200;
        mPosition = new Point();
        mDelayBetweenTrips = 5000;
        setStartShipPosition();
        mMovable = movable;
        mShiftX = 1;
        mShiftY = 1;
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_START_NEW_TRIP),
                mDelayBetweenTrips);
    }

    public Point getPositionForShip() {
        return mPosition;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MESSAGE_COUNT_NEW_POSITION) {
                countNextPosition();
            }
            if (msg.what == MESSAGE_START_NEW_TRIP) {
                setNewDestination();
            }
        }
    };

    private void setNewDestination() {
        if ((mDestinationX == 0) && (mDestinationY == 0)) {
            Random rnd = new Random();
            mDestinationX = rnd.nextInt(mScreenWidth - 200) + 50;
            mDestinationY = (int) (rnd
                    .nextInt((int) (mScreenHeight * MAX_SHIFT_SHIP_TO_BOTTOM)) + mScreenHeight
                    * TOP_POSITION_SHIP);
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                    MOVE_SPEED);
        }
    }

    private void countNextPosition() {
        if (!mMovable) {
            return;
        }

        if ((mDestinationX != 0) && (mDestinationY != 0)) {
            int halfWidth = 30;
            int halfHeight = 30;
            if (mPosition.x + halfWidth > mDestinationX) {
                mPosition.x -= mShiftX;
            } else if (mPosition.x + halfWidth < mDestinationX) {
                mPosition.x += mShiftX;
            }
            if (mPosition.y + halfHeight > mDestinationY) {
                mPosition.y -= mShiftY;
            } else if (mPosition.y + halfHeight < mDestinationY) {
                mPosition.y += mShiftY;
            }
            if ((mPosition.x + halfWidth == mDestinationX)
                    && (mPosition.y + halfHeight == mDestinationY)) {
                mDestinationX = 0;
                mDestinationY = 0;
                mHandler.sendMessageDelayed(
                        mHandler.obtainMessage(MESSAGE_START_NEW_TRIP),
                        mDelayBetweenTrips);
            } else {
                mHandler.sendMessageDelayed(
                        mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION),
                        MOVE_SPEED);
            }
        }
    }

    public void setScreenSize(int screenWidth, int screenHeight) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        setStartShipPosition();
    }

    public boolean isInverted() {
        if (mShiftX > 0) {
            return false;
        }
        return true;
    }

    private void setStartShipPosition() {
        if (!mMovable) {
            mPosition.x = 100;
            mPosition.y = 100;
            return;
        }
        Random rnd = new Random();
        mPosition.x = rnd.nextInt(mScreenWidth - 200) + 50;
        mPosition.y = (int) (rnd
                .nextInt((int) (mScreenHeight * MAX_SHIFT_SHIP_TO_BOTTOM)) + mScreenHeight
                * TOP_POSITION_SHIP);
    }

    @Override
    public void stop() {
        mHandler.removeMessages(MESSAGE_COUNT_NEW_POSITION);
    }

    @Override
    public void start() {
        mHandler.sendMessageDelayed(
                mHandler.obtainMessage(MESSAGE_COUNT_NEW_POSITION), MOVE_SPEED);
    }
}
