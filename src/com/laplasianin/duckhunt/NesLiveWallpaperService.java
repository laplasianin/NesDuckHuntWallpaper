package com.laplasianin.duckhunt;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class NesLiveWallpaperService extends WallpaperService {

    public static final String TAG = "NesDuckHuntLiveWallpaper";
    public static final String SHARED_PREFS_NAME = "NES SETTINGS";

    public static final int CLOUD_MANY_NUMBER = 15;
    public static final int CLOUD_MEDIUM_NUMBER = 10;
    public static final int CLOUD_FEW_NUMBER = 5;
    public static final int CLOUD_NO_NUMBER = 0;

    public static final int CLOUD_MANY = 0;
    public static final int CLOUD_MEDIUM = 1;
    public static final int CLOUD_FEW = 2;
    public static final int CLOUD_NO = 3;
    
    public static final int DUCKS_0 = 0;
    public static final int DUCKS_1 = 1;
    public static final int DUCKS_2 = 2;

    private Context mContext;
    private Settings mSettings;
    private SharedPreferences mPrefs;

    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    public void onCreate() {
        super.onCreate();
//        android.os.Debug.waitForDebugger(); 
        mSettings = new Settings();
        mContext = getApplicationContext(); 
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new NesEngine();
    }

    class NesEngine extends Engine {

        private static final int MESSAGE_DRAW_NEXT_FRAME = 0;
        protected static final int MESSAGE_DUCKS_SETTINGS_CHANGED = 1;
        protected static final int MESSAGE_MODE_SETTINGS_CHANGED = 2;

        private int mNumberOfDucks;

        private Drawer mDrawer;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "onCreate NesEngine");
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
            mDrawer = new Drawer(mContext);
            mDrawer.setNewNumberOfDucks(mNumberOfDucks);
            mHandler.sendEmptyMessage(MESSAGE_DRAW_NEXT_FRAME);
        }

        public NesEngine() {
            Log.d(TAG, "Nes Engine");
            mPrefs = PreferenceManager
                    .getDefaultSharedPreferences(NesLiveWallpaperService.this);
            mSettings.setTouchSettings(mPrefs.getBoolean(
                    getResources().getString(R.string.touch_key), true));
            mSettings.setDucksSettings(Integer.valueOf(mPrefs.getString(
                    getResources().getString(R.string.DucksStyle_key), "2")));

            mNumberOfDucks = getDucksNumber(mSettings.getDucksSettings());
            mPrefs.registerOnSharedPreferenceChangeListener(prefListener);
        }

        Handler mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MESSAGE_DRAW_NEXT_FRAME) {
                    drawFrame();
                    mHandler.sendEmptyMessageDelayed(MESSAGE_DRAW_NEXT_FRAME,
                            1000 / 35);
                } else if (msg.what == MESSAGE_MODE_SETTINGS_CHANGED) {
                } else if (msg.what == MESSAGE_DUCKS_SETTINGS_CHANGED) {
                    mNumberOfDucks = getDucksNumber((mSettings
                            .getDucksSettings()));
                    mDrawer.setNewNumberOfDucks(mNumberOfDucks);
                }
            };
        };

        private int getDucksNumber(int prop) {
            switch (prop) {
            case DUCKS_0:
                return DUCKS_0;
            case DUCKS_1:
                return DUCKS_1;
            case DUCKS_2:
                return DUCKS_2;
            default:
                return DUCKS_1;
            }
        }

        SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs,
                    String key) {
                if (key.equals(getResources().getString(R.string.touch_key))) {
                    mSettings.setTouchSettings(prefs.getBoolean(getResources()
                            .getString(R.string.touch_key), true));
                } else if (key.equals(getResources().getString(
                        R.string.DucksStyle_key))) {
                    mSettings.setDucksSettings(Integer.valueOf(prefs.getString(
                            getResources().getString(R.string.DucksStyle_key),
                            "1")));
                    mHandler.sendEmptyMessage(MESSAGE_DUCKS_SETTINGS_CHANGED);
                }
            }
        };

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d(TAG, "VISIBILITY CHANGED");
            if (!visible) {
                mHandler.removeMessages(MESSAGE_DRAW_NEXT_FRAME);
                mDrawer.stopAllObjects();
            } else {
                if (!mHandler.hasMessages(MESSAGE_DRAW_NEXT_FRAME)) {
                    mHandler.sendEmptyMessageDelayed(MESSAGE_DRAW_NEXT_FRAME,
                            1000 / 35);
                    mDrawer.startAllObjects();
                }
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                int width, int height) {
            Log.d(TAG, "onSurfaceChanged");
            mScreenHeight = height;
            mScreenWidth = width;
            mDrawer.prepareNewSizes(mScreenWidth, mScreenHeight);
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xOffsetStep, float yOffsetStep, int xPixelOffset,
                int yPixelOffset) {
            // TODO Auto-generated method stub
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
                    xPixelOffset, yPixelOffset);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            if ((mDrawer != null) && (mSettings.getTouchSettings())) {
                mDrawer.performTouchEvent((int) event.getX(),
                        (int) event.getY());
            }
            super.onTouchEvent(event);
        }

        public void drawFrame() {
            Canvas c = null;
            final SurfaceHolder holder = getSurfaceHolder();

            try {
                c = holder.lockCanvas();
                if (c != null) {
                	c.drawColor(Color.rgb(70, 99, 243));
                    mDrawer.draw(c);
                }
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }
        }
    }

}

class Settings {
    private boolean mTouch;
    private int mDucksNumber;

    public Settings() {
        mTouch = true;
        mDucksNumber = 0;
    }

    public int getDucksSettings() {
        return mDucksNumber;
    }

    public void setDucksSettings(int newSettings) {
        mDucksNumber = newSettings;
    }

    public boolean getTouchSettings() {
        return mTouch;
    }

    public void setTouchSettings(boolean newTouch) {
        mTouch = newTouch;
    }
}

class WallPaperMode {

    public static final int STATIC_MODE = 0;

    private int mMode;

    public WallPaperMode() {
        mMode = STATIC_MODE;
    }

    public WallPaperMode(int newMode) {
        mMode = newMode;
    }

    public int getMode() {
        return mMode;
    }
}
