package com.example.houzhe.weatheronclass;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by houzhe on 16/9/21.
 */

public class MainActivity extends Activity {

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);//加载布局
    }
}
