package com.iok.gfweather.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jaewon on 16. 7. 6..
 */
public class SharedPrefUtil {
    private static final String PREF_CATEGORY = "DRIVE_INFO";
    private static final String PREF_NAME = "FOLDER_ID";

    static public void setDriveFolderInfo(String pStrDriveFolderId, Context mCtx){
        SharedPreferences pref = mCtx.getSharedPreferences(PREF_CATEGORY, mCtx.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_NAME, pStrDriveFolderId);
        editor.commit();
    }

    static public String getDriveFolderInfo(Context mCtx){
        String strRtnValue = "";
        SharedPreferences pref = mCtx.getSharedPreferences(PREF_CATEGORY, mCtx.MODE_PRIVATE);
        strRtnValue = pref.getString(PREF_NAME, "");

        return strRtnValue;
    }
}
