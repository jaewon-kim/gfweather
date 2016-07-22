package com.iok.gfweather.util;

import android.content.ContentValues;
import android.util.Log;


import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by crismas on 2016. 7. 22..
 */
public class WeatherApiHelper {

//    HttpClient client = new DefaultHttpClient();
//

    public static String getWeather(String strYear, String strMonth, String strLocation) throws Exception{
        HttpClient httpclient = new DefaultHttpClient();
        String url = "http://www.kweather.co.kr/kma/kma_past.html";
        HttpPost post = new HttpPost(url);

        String httpres = "";

        List params = new ArrayList();
        params.add(new BasicNameValuePair("team_nm","서울경기"));
        params.add(new BasicNameValuePair("member_nm","108"));
        params.add(new BasicNameValuePair("sYear", strYear));
        params.add(new BasicNameValuePair("sMonth",strMonth));

        UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        post.setEntity(ent);

        org.apache.http.HttpResponse resPost = httpclient.execute(post);
        HttpEntity resEntity = resPost.getEntity();
        if(resEntity != null){
            httpres  = EntityUtils.toString(resEntity);
        }
        return httpres;
    }

    public static String getWeatherByDate(String strRes, String date){
//        StringTokenizer st = new StringTokenizer(strRes, "kma_past_cal");

        String rtnValue = "";

        String[] listParsed = strRes.split("kma_past_cal");
        for(String parsedItem : listParsed){

            if(parsedItem.indexOf("kma_past_weather_cont") == -1){
                continue;
            }

            String parsedDate = parsedItem.substring(parsedItem.indexOf("kma_past_weather_wtext")+53, parsedItem.length());
            parsedDate = parsedDate.substring(0, parsedDate.indexOf("</span>"));


            String mainWeather = parsedItem.substring(parsedItem.indexOf("</span>")+34 , parsedItem.length());
            mainWeather = mainWeather.substring(0, mainWeather.indexOf("</span>"));
//
//            Log.i("res", ":::" + date);
//            Log.i("res", ":::" + mainWeather);

            if(parsedDate.equalsIgnoreCase(date) == true){
                rtnValue = mainWeather;
                break;
            }

        }
        return rtnValue;
    }
}
