package com.iok.gfweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.iok.gfweather.service.MyService;
import com.iok.gfweather.util.SharedPrefUtil;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Button mBtnStart;
    Button mBtnSelectGoolgeDrive;
    MainActivity mThis;

    String mSelectGoogleDriveFolderId = "";

    private static final String TAG = "MainActivity";
    private static String EXISTING_FOLDER_ID = "";


    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Next available request code.
     */
    protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;



    private static final int REQUEST_CODE_OPENER = 3;



    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;


    Context mCtx;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCtx = this;
        mThis = this;

        setContentView(R.layout.activity_main);



        mBtnStart = (Button)findViewById(R.id.btn_start);
        mBtnSelectGoolgeDrive = (Button)findViewById(R.id.btn_select_google_drive);


        mSelectGoogleDriveFolderId = SharedPrefUtil.getDriveFolderInfo(mCtx);

        Log.i(TAG, "mSelectGoogleDriveFolderId:::" + mSelectGoogleDriveFolderId);

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSvc = new Intent(mCtx, MyService.class);
                iSvc.putExtra("p","main");
                startService(iSvc);
            }
        });

        Log.i(TAG, "why?? so serious???");
        mBtnSelectGoolgeDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(mCtx)
                            .addApi(Drive.API)
                            .addScope(Drive.SCOPE_FILE)
                            .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                            .addConnectionCallbacks(mThis)
                            .addOnConnectionFailedListener(mThis)
                            .build();
                }

                Log.i(TAG, "connect request!!!");
                if(mGoogleApiClient.isConnected() == true){
                    IntentSender intentSender = Drive.DriveApi
                            .newOpenFileActivityBuilder()
                            .setMimeType(new String[] { DriveFolder.MIME_TYPE })
                            .build(mGoogleApiClient);
                    try {
                        startIntentSenderForResult(
                                intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.w(TAG, "Unable to send intent", e);
                    }

                }
                else{
                    mGoogleApiClient.connect();
                }

            }
        });
    }
    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }

        if ( requestCode == REQUEST_CODE_OPENER){
            if (resultCode == RESULT_OK) {
                DriveId driveId = (DriveId) data.getParcelableExtra(
                        OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                SharedPrefUtil.setDriveFolderInfo(driveId.getResourceId() , mCtx);
                Log.i("pick", "driveId::" +driveId);
            }

        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");

        EXISTING_FOLDER_ID = Drive.DriveApi.getRootFolder(mGoogleApiClient).getDriveId().getResourceId();
        Log.i(TAG, "EXISTING_FOLDER_ID:::" + EXISTING_FOLDER_ID);

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[] { DriveFolder.MIME_TYPE })
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.w(TAG, "Unable to send intent", e);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());

        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;
        }
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }

    }
}
