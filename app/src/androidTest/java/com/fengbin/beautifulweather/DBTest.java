package com.fengbin.beautifulweather;

import android.util.Log;

import com.fengbin.beautifulweather.db.WeatherDB;
import com.fengbin.beautifulweather.model.City;
import com.fengbin.beautifulweather.model.County;
import com.fengbin.beautifulweather.model.Province;

import java.util.List;

/**
 * Created by Administrator on 2016/6/5.
 */
public class DBTest extends ApplicationTest {

    public void testUtil(){
        WeatherDB weatherDB = WeatherDB.getInstance(getContext());
        weatherDB.saveProvince(new Province("天津", "03"));
        weatherDB.saveCity(new City("天津", "0301", 03));
        weatherDB.saveCounty(new County("东丽", "030105", 0301));
        List<County> counties = weatherDB.loadCounty(0301);
        Log.e("TAG","county is :" + counties.toString());
    }

}
