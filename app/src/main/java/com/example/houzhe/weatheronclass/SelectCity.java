package com.example.houzhe.weatheronclass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.houzhe.weatheronclass.app.MyApplication;
import com.example.houzhe.weatheronclass.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houzhe on 2016/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;

    private ListView listView;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        setContentView(R.layout.select_city);

        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        MyApplication myApplication = (MyApplication) this.getApplication();
        List<City> list = myApplication.getCityList();
        List<String> cityNames = new ArrayList();
//        for (City city : list){
//            cityNames.add(city.getCity());
//        }
        for (int i = 0;i < 50;i++){
            cityNames.add(list.get(i).getCity());
        }
        listView = (ListView)findViewById(R.id.lv);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cityNames));
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode", "101160101");
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}
