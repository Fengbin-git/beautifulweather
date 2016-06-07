package com.fengbin.beautifulweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.fengbin.beautifulweather.db.WeatherDB;
import com.fengbin.beautifulweather.model.City;
import com.fengbin.beautifulweather.model.County;
import com.fengbin.beautifulweather.model.Province;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/6/5.
 */
/*
    解析和处理服务器返回的数据
*/
public class Utility {

    //处理省级数据
    public synchronized static boolean handleProvinceResponse(WeatherDB weatherDB,String response){
        if(!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    //处理市级数据
    public synchronized static boolean handleCityResponse(WeatherDB weatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if(allCities != null && allCities.length > 0) {
                for (String p : allCities) {
                    String[] array = p.split("\\|");
                    City city = new City();
                    city.setCityName(array[1]);
                    city.setCityCode(array[0]);
                    city.setProvinceId(provinceId);
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    //处理县级数据
    public synchronized static boolean handleCountyResponse(WeatherDB weatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length > 0) {
                for (String p : allCounties) {
                    String[] array = p.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
    //处理天气数据
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherinfo.getString("city");
            String weatherCode = weatherinfo.getString("cityid");
            String temp1 = weatherinfo.getString("temp1");
            String temp2 = weatherinfo.getString("temp2");
            String weatherDesp = weatherinfo.getString("weather");
            String publishTime = weatherinfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //保存天气数据到sp文件
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
