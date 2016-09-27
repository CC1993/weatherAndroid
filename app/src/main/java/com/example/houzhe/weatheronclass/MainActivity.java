package com.example.houzhe.weatheronclass;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.houzhe.weatheronclass.util.NetUtil;

/**
 * Created by houzhe on 16/9/21.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);//加载布局

        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
            Log.d("myWeather", "网络ok");
            Toast.makeText(MainActivity.this, "网络ok!", Toast.LENGTH_LONG).show();
        }else{
            Log.d("myWeather", "网络不ok");
            Toast.makeText(MainActivity.this, "网络不ok!", Toast.LENGTH_LONG).show();
        }
    }
}
