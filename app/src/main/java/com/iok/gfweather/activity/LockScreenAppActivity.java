package com.iok.gfweather.activity;
import com.iok.gfweather.R;
import com.iok.gfweather.receiver.LockScreenReceiver;
import com.iok.gfweather.service.MyService;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import java.io.File;

public class LockScreenAppActivity extends Activity {

	/** Called when the activity is first created. */
    KeyguardManager.KeyguardLock k1;

    int windowwidth;
    int windowheight;
    RelativeLayout mRlBackground;


    private LayoutParams layoutParams;


    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.durga.action.close")){
                Log.d("fff","finished");
                finish();
            }
        }
    };


	public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.main);



        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.durga.action.close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

        if( getIntent()!=null
                && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill")==1 ){
            finish();
        }

		try{

            Intent iSvc = new Intent(this, MyService.class);
            iSvc.putExtra("p","lock");

		    startService(iSvc);


		    StateListener phoneStateListener = new StateListener();
		    TelephonyManager telephonyManager =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		    telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

		    windowwidth=getWindowManager().getDefaultDisplay().getWidth();
		    windowheight=getWindowManager().getDefaultDisplay().getHeight();

			super.onCreate(savedInstanceState);

//			finish();

		}catch (Exception e) {
			// TODO: handle exception
            e.printStackTrace();
		}

	}
	class StateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch(state){
				case TelephonyManager.CALL_STATE_RINGING:
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					System.out.println("call Activity off hook");
					finish();



					break;
				case TelephonyManager.CALL_STATE_IDLE:
					break;
			}
		}
	};



    @Override
	public void onBackPressed() {
		// Don't allow back to dismiss.
		return;
	}

	//only used in lockdown mode
	@Override
	protected void onPause() {
		super.onPause();

		// Don't hang around.
	   // finish();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Don't hang around.
	   // finish();
	}

	@Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)||(keyCode == KeyEvent.KEYCODE_POWER)||(keyCode == KeyEvent.KEYCODE_VOLUME_UP)||(keyCode == KeyEvent.KEYCODE_CAMERA)) {
			//this is where I can do my stuff
			return true; //because I handled the event
		}
	   if((keyCode == KeyEvent.KEYCODE_HOME)){

		   return false;
		}

	return false;

	}


	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_POWER ||(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)||(event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
			//Intent i = new Intent(this, NewActivity.class);
			//startActivity(i);
			return false;
		}
		 if((event.getKeyCode() == KeyEvent.KEYCODE_HOME)){

		   System.out.println("alokkkkkkkkkkkkkkkkk");
		   return false;
		 }
	return false;
	}

	/*public void unloack(){

		  finish();

	}*/
	public void onDestroy(){
	   // k1.reenableKeyguard();
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

}