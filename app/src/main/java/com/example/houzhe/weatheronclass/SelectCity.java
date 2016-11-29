package com.example.houzhe.weatheronclass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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

public class SelectCity extends Activity implements View.OnClickListener, SearchView.OnQueryTextListener {

    private ImageView mBackBtn;

    private TextView presentCity;

    private ListView listView;

    private SearchView searchView;

    private List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        setContentView(R.layout.select_city);

        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        //设置搜索栏的监听器
        searchView = (SearchView)findViewById(R.id.srv1);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("search city(chinese)");

        //设置actionbar上的当前地区
        presentCity = (TextView) findViewById(R.id.title_name);
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String cityName = sharedPreferences.getString("main_city_name", "北京");//默认是北京的编号
        Log.d("myWeather", cityName);
        presentCity.setText("当前地区：" + cityName);


        addCityNames();
        listView = (ListView)findViewById(R.id.lv);
        //允许列表被过滤
        listView.setTextFilterEnabled(true);
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
                intent.putExtra("cityName", (String) listItem.get(i).get("name"));
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

        Map<String, Object> item = new HashMap<String, Object>();
        item.put("name", "houzhe");
        item.put("desc", "houzhe");
        item.put("code", "101010100");
        listItem.add(item);
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

    // 用户输入字符时激发该方法
    @Override
    public boolean onQueryTextChange(String newText) {
        Toast.makeText(SelectCity.this, "Searching" + newText, Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(newText)) {
            // 清除ListView的过滤
            listView.clearTextFilter();
        } else {
            // 使用用户输入的内容对ListView的列表项进行过滤
            listView.setFilterText(newText);
        }
        return true;
    }

    // 单击搜索按钮时激发该方法
    @Override
    public boolean onQueryTextSubmit(String query) {
        // 实际应用中应该在该方法内执行实际查询
        // 此处仅使用Toast显示用户输入的查询内容
        Toast.makeText(this, "Choosed:" + query, Toast.LENGTH_SHORT).show();
        return false;
    }

}
