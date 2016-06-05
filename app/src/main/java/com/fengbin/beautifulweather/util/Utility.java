package com.fengbin.beautifulweather.util;

import android.text.TextUtils;

import com.fengbin.beautifulweather.db.WeatherDB;
import com.fengbin.beautifulweather.model.City;
import com.fengbin.beautifulweather.model.County;
import com.fengbin.beautifulweather.model.Province;

/**
 * Created by Administrator on 2016/6/5.
 */
/*
    解析和处理服务器返回的数据
*/
public class Utility {

    //省级数据
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
    //市级数据
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
    //县级数据
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
}
