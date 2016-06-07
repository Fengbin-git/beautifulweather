package com.fengbin.beautifulweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengbin.beautifulweather.R;
import com.fengbin.beautifulweather.util.HttpCallBackListener;
import com.fengbin.beautifulweather.util.HttpUtil;
import com.fengbin.beautifulweather.util.Utility;

/**
 * Created by Administrator on 2016/6/7.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private TextView city_name;
    private TextView publish_text;
    private LinearLayout weather_info_layout;
    private TextView current_date;
    private TextView weather_desp;
    private TextView temp1;
    private TextView temp2;
    private Button select_city;
    private Button refresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        city_name = (TextView)findViewById(R.id.city_name);
        publish_text = (TextView)findViewById(R.id.publish_text);
        weather_info_layout = (LinearLayout)findViewById(R.id.weather_info_layout);
        current_date = (TextView)findViewById(R.id.current_date);
        weather_desp = (TextView)findViewById(R.id.weather_desp);
        temp1 = (TextView)findViewById(R.id.temp1);
        temp2 = (TextView)findViewById(R.id.temp2);
        select_city = (Button) findViewById(R.id.select_city);
        refresh = (Button)findViewById(R.id.refresh);
        refresh.setOnClickListener(this);
        select_city.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)) {
            publish_text.setText("同步中...");
            weather_info_layout.setVisibility(View.INVISIBLE);
            city_name.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        }else {
            showWeather();
        }
    }

    private void showWeather() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        city_name.setText(sp.getString("city_name",""));
        temp1.setText(sp.getString("temp1",""));
        temp2.setText(sp.getString("temp2",""));
        weather_desp.setText(sp.getString("weather_desp",""));
        publish_text.setText("今天" + sp.getString("publish_time","") + "发布");
        current_date.setText(sp.getString("current_date", ""));
        weather_info_layout.setVisibility(View.VISIBLE);
        city_name.setVisibility(View.VISIBLE);
    }

    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address,"countyCode");
    }

    private void queryFromServer(String address,final String code) {
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(code)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] split = response.split("\\|");
                        if (split != null && split.length == 2) {
                            String weatherCode = split[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(code)) {
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publish_text.setText("同步失败");
                    }
                });
            }
        });
    }

    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh:
                publish_text.setText("同步中...");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = sp.getString("weather_code", "");
                if(!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
