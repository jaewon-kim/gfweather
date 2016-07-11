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
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
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

    TextView mTvSelectedFolderName;


    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Next available request code.
     */
    protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

    private static final int REQUEST_CODE_SELECT_FOLDER = 3;
    private static final int REQUEST_CODE_GET_FOLDER_NAME = 4;

    private static int nCurrentRequest = 0;

    private ResultCallback<DriveResource.MetadataResult> mMetaResult = null;



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
        mTvSelectedFolderName = (TextView)findViewById(R.id.tv_selected_foldername);

        mSelectGoogleDriveFolderId = SharedPrefUtil.getDriveFolderInfo(mCtx);

        Log.i(TAG, "mSelectGoogleDriveFolderId:::" + mSelectGoogleDriveFolderId);

        if(SharedPrefUtil.getDriveFolderInfo(mCtx) != null
                && SharedPrefUtil.getDriveFolderInfo(mCtx).length() > 0 ){
            connectGoogleDriveApi(REQUEST_CODE_GET_FOLDER_NAME);
        }

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iSvc = new Intent(mCtx, MyService.class);
                iSvc.putExtra("p","main");
                startService(iSvc);
            }
        });

        mMetaResult = new ResultCallback<DriveResource.MetadataResult>() {
            @Override
            public void onResult(@NonNull DriveResource.MetadataResult metadataResult) {
                if(metadataResult.getStatus().isSuccess() != true){
                    Log.i(TAG, "fail to retrieve");
                    return;
                }

                Metadata metadata = metadataResult.getMetadata();
                mTvSelectedFolderName.setText(metadata.getTitle());
            }
        };

        Log.i(TAG, "why?? so serious???");
        mBtnSelectGoolgeDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connectGoogleDriveApi(REQUEST_CODE_SELECT_FOLDER);

            }
        });
    }

    protected void connectGoogleDriveApi(int nRequestCode){

        nCurrentRequest = nRequestCode;
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
        if(mGoogleApiClient.isConnected() == true
                && nCurrentRequest == REQUEST_CODE_SELECT_FOLDER
                ){
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[] { DriveFolder.MIME_TYPE })
                    .build(mGoogleApiClient);
            try {
                startIntentSenderForResult(
                        intentSender, nRequestCode, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "Unable to send intent", e);
            }

        }
        else{
            mGoogleApiClient.connect();
        }
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

        if ( requestCode == REQUEST_CODE_SELECT_FOLDER){
            if (resultCode == RESULT_OK) {
                DriveId driveId = (DriveId) data.getParcelableExtra(
                        OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                SharedPrefUtil.setDriveFolderInfo(driveId.toString() , mCtx);
                Log.i("pick", "driveId::" +driveId);
                DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, DriveId.decodeFromString(driveId.toString()));
                file.getMetadata(mGoogleApiClient).setResultCallback(mMetaResult);

            }
        }

//        if( requestCode == REQUEST_CODE_GET_FOLDER_NAME){
//            if(resultCode == RESULT_OK){
//                DriveId driveId = (DriveId) data.getParcelableExtra(
//                        OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
//
//                DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, DriveId.decodeFromString(driveId.toString()));
//                file.getMetadata(mGoogleApiClient).setResultCallback(mMetaResult);
//            }
//        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");

        EXISTING_FOLDER_ID = Drive.DriveApi.getRootFolder(mGoogleApiClient).getDriveId().getResourceId();
        Log.i(TAG, "EXISTING_FOLDER_ID:::" + EXISTING_FOLDER_ID);

        if(nCurrentRequest == REQUEST_CODE_SELECT_FOLDER){
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[] { DriveFolder.MIME_TYPE })
                    .build(mGoogleApiClient);
            try {
                startIntentSenderForResult(
                        intentSender, REQUEST_CODE_SELECT_FOLDER, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.w(TAG, "Unable to send intent", e);
            }
        }

        if(nCurrentRequest == REQUEST_CODE_GET_FOLDER_NAME){

//            DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, DriveId.decodeFromString(SharedPrefUtil.getDriveFolderInfo(mCtx)));
//            file.getMetadata(mGoogleApiClient).setResultCallback(mMetaResult);
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
