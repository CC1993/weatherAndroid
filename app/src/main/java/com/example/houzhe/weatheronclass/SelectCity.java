package com.example.houzhe.weatheronclass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.houzhe.weatheronclass.app.MyApplication;
import com.example.houzhe.weatheronclass.bean.City;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Created by houzhe on 2016/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener{

    private ImageView mBackBtn;

    private ListView listView;

    private List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        setContentView(R.layout.select_city);

        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        addCityNames();
        listView = (ListView)findViewById(R.id.lv);
//        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cityNames));

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItem,
                R.layout.items, new String[]{"name", "desc"},
                new int[]{R.id.name, R.id.desc});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapeterView, View view, int i, long l){
                Toast.makeText(SelectCity.this, "changing to："+listItem.get(i).get("name"), Toast.LENGTH_SHORT).show();
                Intent intent= new Intent();
                intent.putExtra("cityCode", (String) listItem.get(i).get("code"));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void addCityNames(){
        MyApplication myApplication = (MyApplication) this.getApplication();
        List<City> list = myApplication.getCityList();
//        for (City city : list){
//            cityNames.add(city.getCity());
//        }
        for (int i = 0;i < list.size();i++){
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", list.get(i).getCity());
            item.put("desc", list.get(i).getProvince());
            item.put("code", list.get(i).getNumber());
            listItem.add(item);
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back:
                Intent i = new Intent();
//                i.putExtra("cityCode", "101160101");
//                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                break;
        }
    }
}
