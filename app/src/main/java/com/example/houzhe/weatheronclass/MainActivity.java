package com.example.houzhe.weatheronclass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.houzhe.weatheronclass.bean.TodayWeather;
import com.example.houzhe.weatheronclass.util.NetUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by houzhe on 16/9/21.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn;

    private ImageView mCitySelect;
    private TextView cityTv, timeTv, weekTv, pmDataTv, pmQualityTv, temperatureTv,
            climateTv, windTv, city_name_Tv, humidityTv, wendu;
    private ImageView weatherImg, pmImg;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);//加载布局

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络贼好");
            Toast.makeText(MainActivity.this, "网络贼好!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络完蛋");
            Toast.makeText(MainActivity.this, "网络完蛋!", Toast.LENGTH_LONG).show();
        }
        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        initView();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //    初始化控件内容
    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        wendu = (TextView) findViewById(R.id.wendu);

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String cityCode = sharedPreferences.getString("main_city_code", "101160101");//默认是北京的编号
        Log.d("myWeather", cityCode);

        //初始化为一个城市
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络可用");
            queryWeatherCode(cityCode);
        } else {
            Log.d("myWeather", "网络不可用");
            Toast.makeText(MainActivity.this, "网络不可用!", Toast.LENGTH_LONG).show();
        }

//        city_name_Tv.setText("N/A");
//        cityTv.setText("N/A");
//        timeTv.setText("N/A");
//        humidityTv.setText("N/A");
//        pmDataTv.setText("N/A");
//        pmQualityTv.setText("N/A");
//        weekTv.setText("N/A");
//        temperatureTv.setText("N/A");
//        climateTv.setText("N/A");
//        windTv.setText("N/A");
    }

    void initWeatherImg(TodayWeather todayWeather){
        if(todayWeather.getType().equals("晴")){
            weatherImg.setImageLevel(0);
        }else if (todayWeather.getType().equals("暴雪")){
            weatherImg.setImageLevel(8);
        }else if (todayWeather.getType().equals("暴雨")){
            weatherImg.setImageLevel(1);
        }else if (todayWeather.getType().equals("大暴雨")){
            weatherImg.setImageLevel(2);
        }else if (todayWeather.getType().equals("大雪")){
            weatherImg.setImageLevel(3);
        }else if (todayWeather.getType().equals("大雨")){
            weatherImg.setImageLevel(4);
        }else if (todayWeather.getType().equals("多云")){
            weatherImg.setImageLevel(5);
        }else if (todayWeather.getType().equals("雷阵雨")){
            weatherImg.setImageLevel(6);
        }else if (todayWeather.getType().equals("雷阵雨冰雹")){
            weatherImg.setImageLevel(7);
        }else if (todayWeather.getType().equals("沙尘暴")){
            weatherImg.setImageLevel(9);
        }else if (todayWeather.getType().equals("特大暴雨")){
            weatherImg.setImageLevel(10);
        }else if (todayWeather.getType().equals("雾")){
            weatherImg.setImageLevel(11);
        }else if (todayWeather.getType().equals("小雪")){
            weatherImg.setImageLevel(12);
        }else if (todayWeather.getType().equals("小雨")){
            weatherImg.setImageLevel(13);
        }else if (todayWeather.getType().equals("阴")){
            weatherImg.setImageLevel(14);
        }else if (todayWeather.getType().equals("雨加雪")){
            weatherImg.setImageLevel(15);
        }else if (todayWeather.getType().equals("阵雪")){
            weatherImg.setImageLevel(16);
        }else if (todayWeather.getType().equals("阵雨")){
            weatherImg.setImageLevel(17);
        }else if (todayWeather.getType().equals("中雪")){
            weatherImg.setImageLevel(18);
        }else if (todayWeather.getType().equals("中雨")){
            weatherImg.setImageLevel(19);
        }else{
            weatherImg.setImageLevel(0);
        }
    }

    void initPMFace(TodayWeather todayWeather){
        //有可能在比较详细的地址区域内的PM值缺失而报错，当有这种情况，可以选择提示
        //信息不足，此时需要加入新的突破，也可以选择区域所在省市的大情况或者就近选择。
        //因为临近的地区代码差不多的。
        if (todayWeather.getPm25().equals("???")){
            pmImg.setImageLevel(0);
        }
        else if (todayWeather.getPm25() != null && !todayWeather.getPm25().isEmpty() ){
            if (Integer.parseInt(todayWeather.getPm25()) >= 0 && Integer.parseInt(todayWeather.getPm25()) <= 50){
                pmImg.setImageLevel(0);
            }else if (Integer.parseInt(todayWeather.getPm25()) > 50 && Integer.parseInt(todayWeather.getPm25()) <=100){
                pmImg.setImageLevel(1);
            }else if (Integer.parseInt(todayWeather.getPm25()) > 100 && Integer.parseInt(todayWeather.getPm25()) <=150){
                pmImg.setImageLevel(2);
            }else if (Integer.parseInt(todayWeather.getPm25()) > 150 && Integer.parseInt(todayWeather.getPm25()) <=200){
                pmImg.setImageLevel(3);
            }else if (Integer.parseInt(todayWeather.getPm25()) > 200 && Integer.parseInt(todayWeather.getPm25()) <=300){
                pmImg.setImageLevel(4);
            }else if (Integer.parseInt(todayWeather.getPm25()) > 300){
                pmImg.setImageLevel(5);
            }else{
                pmImg.setImageLevel(0);
            }
        }else{
            pmImg.setImageLevel(0);
        }

    }

    void updateTodayWeather(TodayWeather todayWeather) {
        city_name_Tv.setText(todayWeather.getCity());
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度:" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() +  "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:" + todayWeather.getFengli());
        wendu.setText(todayWeather.getWendu() + "℃");
        /**
         * 下行代码用于更新因天气变化而变化的图片，注意要在这里进行更换，因为更新ui界面只能在主界面完成
         */
        initWeatherImg(todayWeather);
        /**
         * 下行代码用于更新pm值不同人的体验的表情变化图，理由同上
         */
        initPMFace(todayWeather);
        Toast.makeText(MainActivity.this, "update success!", Toast.LENGTH_SHORT).show();
    }


    //通过SharedPreferences读取城市id，如果没有定义则缺省为101010100(北京城市 ID)。兰州：101160101
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this, SelectCity.class);
//            startActivity(i);
            startActivityForResult(i, 1);
        }
        if (view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101011100");//默认是北京的编号
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络贼好");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络完蛋了");
                Toast.makeText(MainActivity.this, "网络完蛋了!", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "汝选择的城市代码乃"+newCityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather", "网络贼好");
                queryWeatherCode(newCityCode);
                SharedPreferences settings
                        = (SharedPreferences)getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("main_city_code", newCityCode);
                editor.commit();
                Log.d("myWeather", "更新了默认天气地址：" + newCityCode);
            }else{
                Log.d("myWeather", "网络完蛋");
                Toast.makeText(MainActivity.this, "网络完蛋了！", Toast.LENGTH_LONG).show();
            }
        }
    }


    //使用**获取网络数据
    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //------------------------http请求数据--------------------------------
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    //------------------------http请求数据--------------------------------
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);

                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {
                        Log.d("myWeather", todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldate) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldate));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前时间是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "city:    " + xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "updatetime:    " + xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "shidu:    " + xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "wendu:    " + xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "pm25:    " + xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "quality:    " + xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fengxiang:    " + xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fengli:    " + xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "date:    " + xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "high:    " + xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "low:    " + xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "type:    " + xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

