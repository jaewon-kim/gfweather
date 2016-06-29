package com.iok.gfweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iok.gfweather.activity.LockScreenAppActivity;

public class LockScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn = true;

    public LockScreenReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            wasScreenOn = false;
            Intent intentLs = new Intent(context, LockScreenAppActivity.class);
            intentLs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentLs);

        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            wasScreenOn = true;
            Intent intentLs = new Intent(context, LockScreenAppActivity.class);
            intentLs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentLs);

        }
        else if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent intentLs = new Intent(context, LockScreenAppActivity.class);
            intentLs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentLs);
        }
//
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
