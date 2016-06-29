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
import android.os.PowerManager;
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
    boolean inDragMode;
    int selectedImageViewX;
    int selectedImageViewY;
    int windowwidth;
    int windowheight;
    ImageView droid,phone,home;
    RelativeLayout mRlBackground;
    int home_x,home_y;
    int[] droidpos;

    private LayoutParams layoutParams;


	public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON| WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        );

        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);

//        WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.main);
        mRlBackground = (RelativeLayout)findViewById(R.id.rl_relative_bg);

        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        mRlBackground.setBackground(Drawable.createFromPath(new File(sdcard,"/gWeather/seulgi.jpeg").getAbsolutePath()));



        if( getIntent()!=null
                && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill")==1 ){
            finish();
        }

		try{

		    startService(new Intent(this,MyService.class));

            KeyguardManager km =(KeyguardManager)getSystemService(KEYGUARD_SERVICE);
		    k1 = km.newKeyguardLock("IN");
		    k1.disableKeyguard();

		    StateListener phoneStateListener = new StateListener();
		    TelephonyManager telephonyManager =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		    telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);

		    windowwidth=getWindowManager().getDefaultDisplay().getWidth();
		    windowheight=getWindowManager().getDefaultDisplay().getHeight();




            mRlBackground.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            Log.d("gWeather", "action down");
//                            finish();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            Log.d("gWeather", "action move");
                            break;
                    }
                    return true;
                }
            });

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

    @Override
    public void onAttachedToWindow() {

//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
        super.onAttachedToWindow();
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

		super.onDestroy();
	}

}