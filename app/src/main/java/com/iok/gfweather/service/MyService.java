package com.iok.gfweather.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.iok.gfweather.R;
import com.iok.gfweather.receiver.LockScreenReceiver;

import java.io.File;

public class MyService extends Service {
    BroadcastReceiver mReceiver;
    WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private LayoutInflater mInflater = null;
    private View mLockscreenView = null;
    RelativeLayout mRlBackground;
    Context mContext = null;

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

        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        mRlBackground.setBackground(Drawable.createFromPath(new File(sdcard,"/gWeather/seulgi.jpeg").getAbsolutePath()));


        mWindowManager.addView(mLockscreenView, mParams);

        return START_NOT_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }
}
