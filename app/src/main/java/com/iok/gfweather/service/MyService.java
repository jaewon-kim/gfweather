package com.iok.gfweather.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.iok.gfweather.receiver.LockScreenReceiver;

public class MyService extends Service {
    BroadcastReceiver mReceiver;

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
}
