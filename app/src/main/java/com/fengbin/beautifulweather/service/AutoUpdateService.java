package com.fengbin.beautifulweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.fengbin.beautifulweather.receiver.AutoUpdateReceiver;
import com.fengbin.beautifulweather.util.HttpCallBackListener;
import com.fengbin.beautifulweather.util.HttpUtil;
import com.fengbin.beautifulweather.util.Utility;

/**
 * Created by Administrator on 2016/6/7.
 */
public class AutoUpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(){
            public void run(){
                updateWeather();
            }
        }.start();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long hour = 8 * 1000 * 60 * 60;
        long triggerAtMillis = SystemClock.elapsedRealtime() + hour;
        Intent i = new Intent(AutoUpdateService.this,AutoUpdateReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AutoUpdateService.this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtMillis,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = sp.getString("weather_code", "");
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
