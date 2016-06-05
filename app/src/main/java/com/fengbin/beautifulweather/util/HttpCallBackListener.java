package com.fengbin.beautifulweather.util;

/**
 * Created by Administrator on 2016/6/5.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
