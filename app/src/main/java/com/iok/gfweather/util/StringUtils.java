package com.iok.gfweather.util;

import android.content.ContentValues;
import android.util.Log;

/**
 * Created by crismas on 2016. 7. 22..
 */
public class StringUtils {
    public static final String KEY_CV_YEAR = "YEAR";
    public static final String KEY_CV_MONTH = "MONTH";
    public static final String KEY_CV_MONTH_DAY = "MONTH_DAY";

    public static ContentValues convExifDateToCV(String strExifDate){

        if(strExifDate == null)
            return null;

        ContentValues cvRtnValue = new ContentValues();

        String strYear = strExifDate.substring(0,4);
        String strMonth = strExifDate.substring(5,7);
        String strDay = strExifDate.substring(8,10);

        Log.i("StringUtils", "YY:::" + strYear);
        Log.i("StringUtils", "MM:::" + strMonth);
        Log.i("StringUtils", "DD:::" + strDay);

        cvRtnValue.put(KEY_CV_YEAR, strYear);
        cvRtnValue.put(KEY_CV_MONTH, strMonth);
        cvRtnValue.put(KEY_CV_MONTH_DAY, strMonth + "/" + strDay );

        return cvRtnValue;
    }
}
