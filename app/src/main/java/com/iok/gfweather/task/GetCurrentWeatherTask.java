package com.iok.gfweather.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iok.gfweather.util.SharedPrefUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by crismas on 2016. 7. 28..
 */
public class GetCurrentWeatherTask extends AsyncTask {
    String strBody = "";
    public Context mCtx;

    public double mLatitude;
    public double mLongitude;

    private final String APP_KEY = "fe686896-0051-3145-9f42-e38e172957f1";
    @Override
    protected Object doInBackground(Object[] params) {
        //http://api.openweathermap.org/data/2.5/weather?q=Seoul,kr&appid=50a91aec689ab354ab5ef11a53f84d78
        String url = "http://apis.skplanetx.com/weather/current/minutely?lon="+mLongitude+"&lat="+mLatitude+"&version=1";
        HttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        httpget.setHeader("Accept","applicaiton/json");
        httpget.setHeader("AppKey",APP_KEY);
        httpget.setHeader("x-skpop-userId","crismas");

        try {
            HttpResponse response = client.execute(httpget);
            strBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.i("weather", "WEATHER:::" + strBody);

        String strMain = "";
        try {
            JSONObject obj = new JSONObject(strBody);
            JSONObject weather = obj.getJSONObject("weather");
            JSONArray minutely = weather.getJSONArray("minutely");
            JSONObject weatherItem = minutely.getJSONObject(0);
            JSONObject sky = weatherItem.getJSONObject("sky");
            strMain = sky.getString("name");

            Log.i("weather", "MAIN:::" + strMain);

            SharedPrefUtil.setWeatherInfo(strMain, mCtx);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
