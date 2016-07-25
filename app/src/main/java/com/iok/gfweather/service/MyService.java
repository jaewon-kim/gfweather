package com.iok.gfweather.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.iok.gfweather.R;
import com.iok.gfweather.db.DaoMaster;
import com.iok.gfweather.db.DaoSession;
import com.iok.gfweather.db.Wallpaper;
import com.iok.gfweather.db.WallpaperDao;
import com.iok.gfweather.receiver.LockScreenReceiver;
import com.iok.gfweather.view.KeyGuardView;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.util.List;

public class MyService extends Service {
    BroadcastReceiver mReceiver;
    WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    private KeyGuardView mKgvOverlay = null;
    RelativeLayout mRlBackground;
    Context mContext = null;
    String TAG = "MyService";


    DaoSession mDaoSession;

    private float mStartX;
    private float mStartY;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        KeyguardManager.KeyguardLock kl;

        KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        kl = km.newKeyguardLock(KEYGUARD_SERVICE);
        kl.disableKeyguard();
//        kl.reenableKeyguard();

        mContext = this;

        Log.d("inService", "KEY GUARD");
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new LockScreenReceiver();
        registerReceiver(mReceiver, filter);

        DaoMaster.DevOpenHelper helper  = new DaoMaster.DevOpenHelper(this, "wallpaper-db", null);
        Database db =  helper.getWritableDb();

        mDaoSession = new DaoMaster(db).newSession();

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        if(extras == null){
            return START_NOT_STICKY;
        }


        String strParent = (String) extras.get("p");

        if("lock".equalsIgnoreCase(strParent) != true){
            return START_NOT_STICKY;
        }


        mWindowManager = ((WindowManager)  getBaseContext().getSystemService(WINDOW_SERVICE));

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        mInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLockscreenView = mInflater.inflate(R.layout.main, null);

        mLockscreenView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        mRlBackground = (RelativeLayout)mLockscreenView.findViewById(R.id.rl_relative_bg);


        mKgvOverlay = (KeyGuardView) mLockscreenView.findViewById(R.id.kgv);
        List<Wallpaper> wallpapers = mDaoSession.getWallpaperDao().loadAll();
        for(Wallpaper itemWallpaper: wallpapers){
            Log.i(TAG,"ITEM::" + itemWallpaper.getId()
                    + ":::" + itemWallpaper.getPath()
                    + ":::" + itemWallpaper.getExifdate()
                    + ":::" + itemWallpaper.getWeather()
            );
        }

        Log.i(TAG, "Wallpaper Count:::" + wallpapers.size());

        int randomIdx = (int)(Math.random()*10)% wallpapers.size();

        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        mRlBackground.setBackground(Drawable.createFromPath(new File(wallpapers.get(randomIdx).getPath()).getAbsolutePath()));


        mLockscreenView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("gWeather", "action down");
                        mStartX = motionEvent.getX();
                        mStartY = motionEvent.getY();

//                            finish();
                        break;

                    case MotionEvent.ACTION_MOVE:
//                        Log.d("gWeather", "action move");
                        float fCurrX = motionEvent.getX();
                        float fCurrY = motionEvent.getY();


//                        mKgvOverlay.dispatchTouchEvent(motionEvent);

                        mKgvOverlay.mX = motionEvent.getX();
                        mKgvOverlay.mY = motionEvent.getY();

                        double fDistance = Math.sqrt(
                                Math.pow(fCurrX - mStartX, 2)
                                + Math.pow(fCurrY - mStartY , 2)
                        );
//                        Log.d("touch", "distance:::" + fDistance);
                        if(fDistance > 1000){

                            if (null != mWindowManager && null != mLockscreenView) {
                                mWindowManager.removeView(mLockscreenView);
                                mLockscreenView = null;
                                mWindowManager = null;
                                stopSelf(0);

                                Log.d("touch", "finished");
                                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(MyService.this);
                                localBroadcastManager.sendBroadcast(new Intent("com.durga.action.close"));
                                return true;
                            } else {
                                Log.d("touch", "finished?????");
                                return false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mKgvOverlay.mX = -200;
                        mKgvOverlay.mY = -200;
                        break;
                }
                return true;
            }
        });

//        mLockscreenView.
        mWindowManager.addView(mLockscreenView, mParams);

        return START_NOT_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }
}
