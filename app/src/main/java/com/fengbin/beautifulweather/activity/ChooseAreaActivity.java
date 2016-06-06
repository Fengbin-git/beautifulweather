package com.fengbin.beautifulweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengbin.beautifulweather.R;
import com.fengbin.beautifulweather.db.WeatherDB;
import com.fengbin.beautifulweather.model.City;
import com.fengbin.beautifulweather.model.County;
import com.fengbin.beautifulweather.model.Province;
import com.fengbin.beautifulweather.util.HttpCallBackListener;
import com.fengbin.beautifulweather.util.HttpUtil;
import com.fengbin.beautifulweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/5.
 */
public class ChooseAreaActivity extends Activity {
    public static  final int LEVEL_PROVINCE = 0;
    public static  final int LEVEL_CITY = 1;
    public static  final int LEVEL_COUNTY = 2;
    private TextView title_text;
    private ListView list_view;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private WeatherDB weatherDB;
    private int currentLevel;
    private ProgressDialog progressDialog;
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        weatherDB = WeatherDB.getInstance(this);
        title_text = (TextView)findViewById(R.id.title_text);
        list_view = (ListView)findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCity();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        queryProvinces();//加载省级数据
    }

    private void queryCounties() {
        countyList = weatherDB.loadCounty(selectedCity.getId());
        if(countyList.size() > 0) {
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            title_text.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    private void queryCity() {
        cityList = weatherDB.loadCity(selectedProvince.getId());
        if(cityList.size() > 0) {
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            title_text.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    private void queryProvinces() {
        provinceList = weatherDB.loadProvince();
        if(provinceList.size() > 0) {
            dataList.clear();
            for (Province province  : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            title_text.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if(!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)) {
                    result = Utility.handleProvinceResponse(weatherDB, response);
                }else if("city".equals(type)) {
                    result = Utility.handleCityResponse(weatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)) {
                    result = Utility.handleCountyResponse(weatherDB,response,selectedCity.getId());
                }

                if(result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)) {
                                queryProvinces();
                            } else if("city".equals(type)) {
                                queryCity();
                            }else if("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void closeProgressDialog() {
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY) {
            queryCity();
        }else if(currentLevel == LEVEL_CITY) {
            queryProvinces();
        }else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
