package com.example.houzhe.weatheronclass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.houzhe.weatheronclass.bean.TodayWeather;
import com.example.houzhe.weatheronclass.util.NetUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by houzhe on 16/9/21.
 */
public class MainActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int UPDATE_SPIN_START = 2;
    private static final int UPDATE_SPIN_STOP = 3;
    private static final int INIT_NO_DATA = 4;

    private ImageView mUpdateBtn;

    private ImageView mCitySelect;
    private TextView cityTv, timeTv, weekTv, pmDataTv, pmQualityTv, temperatureTv,
            climateTv, windTv, city_name_Tv, humidityTv, wendu, wenduTittle, pm25, detailTv;
    private ImageView weatherImg, pmImg;

    private Animation animation;

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv1, R.id.iv2};

    private TextView weekToday1Guide1, weekToday2Guide1, weekToday3Guide1, weekToday1Guide2,
            weekToday2Guide2, weekToday3Guide2, temperature1Guide1,temperature2Guide1,
            temperature3Guide1, temperature1Guide2, temperature2Guide2, temperature3Guide2,
            climate1Guide1, climate2Guide1, climate3Guide1, climate1Guide2, climate2Guide2,
            climate3Guide2, wind1Guide1, wind2Guide1, wind3Guide1, wind1Guide2, wind2Guide2,
            wind3Guide2;
    private ImageView weather1ImgGuide1, weather2ImgGuide1, weather3ImgGuide1, weather1ImgGuide2,
            weather2ImgGuide2, weather3ImgGuide2;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((Map<String, TodayWeather>) msg.obj);
                    break;
                case UPDATE_SPIN_START:
                    mUpdateBtn.startAnimation(animation);
                    mUpdateBtn.setEnabled(false);
                    break;
                case UPDATE_SPIN_STOP:
                    mUpdateBtn.clearAnimation();
                    mUpdateBtn.setEnabled(true);
                    break;
                case INIT_NO_DATA:
                    cityTv.setText("N/A");
                    timeTv.setText("N/A");
                    humidityTv.setText("N/A");
                    pmDataTv.setText("N/A");
                    pmQualityTv.setText("N/A");
                    weekTv.setText("N/A");
                    temperatureTv.setText("N/A");
                    climateTv.setText("N/A");
                    windTv.setText("N/A");
                    wendu.setText("N/A");
                    wenduTittle.setText("当前温度");
                    pm25.setText("PM2.5");
                    detailTv.setText("N/A");
                    SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                    String cityName = sharedPreferences.getString("main_city_name", "N/A");
                    city_name_Tv.setText(cityName);
                    Toast.makeText(MainActivity.this, "this city has no data, choose another one pls~", Toast.LENGTH_LONG).show();
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
            Toast.makeText(MainActivity.this, "network good!", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络完蛋");
            Toast.makeText(MainActivity.this, "network good!", Toast.LENGTH_LONG).show();
        }
        mCitySelect = (ImageView)findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        initViewPagers();
        initDots();
        initView();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void startService(View view) {
        startService(new Intent(getBaseContext(), MyService.class));
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), MyService.class));
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
        wenduTittle = (TextView) findViewById(R.id.wendutittle);
        pm25 = (TextView) findViewById(R.id.pm2_5);
        detailTv = (TextView) findViewById(R.id.detail);

        weekToday1Guide1 = (TextView) views.get(0).findViewById(R.id.week_today1_guide1);
        weekToday2Guide1 = (TextView) views.get(0).findViewById(R.id.week_today2_guide1);
        weekToday3Guide1 = (TextView) views.get(0).findViewById(R.id.week_today3_guide1);
        weekToday1Guide2 = (TextView) views.get(1).findViewById(R.id.week_today1_guide2);
        weekToday2Guide2 = (TextView) views.get(1).findViewById(R.id.week_today2_guide2);
        weekToday3Guide2 = (TextView) views.get(1).findViewById(R.id.week_today3_guide2);
        temperature1Guide1 = (TextView) views.get(0).findViewById(R.id.temperature1_guide1);
        temperature2Guide1 = (TextView) views.get(0).findViewById(R.id.temperature2_guide1);
        temperature3Guide1 = (TextView) views.get(0).findViewById(R.id.temperature3_guide1);
        temperature1Guide2 = (TextView) views.get(1).findViewById(R.id.temperature1_guide2);
        temperature2Guide2 = (TextView) views.get(1).findViewById(R.id.temperature2_guide2);
        temperature3Guide2 = (TextView) views.get(1).findViewById(R.id.temperature3_guide2);
        climate1Guide1 = (TextView) views.get(0).findViewById(R.id.climate1_guide1);
        climate2Guide1 = (TextView) views.get(0).findViewById(R.id.climate2_guide1);
        climate3Guide1 = (TextView) views.get(0).findViewById(R.id.climate3_guide1);
        climate1Guide2 = (TextView) views.get(1).findViewById(R.id.climate1_guide2);
        climate2Guide2 = (TextView) views.get(1).findViewById(R.id.climate2_guide2);
        climate3Guide2 = (TextView) views.get(1).findViewById(R.id.climate3_guide2);
        wind1Guide1 = (TextView) views.get(0).findViewById(R.id.wind1_guide1);
        wind2Guide1 = (TextView) views.get(0).findViewById(R.id.wind2_guide1);
        wind3Guide1 = (TextView) views.get(0).findViewById(R.id.wind3_guide1);
        wind1Guide2 = (TextView) views.get(1).findViewById(R.id.wind1_guide2);
        wind2Guide2 = (TextView) views.get(1).findViewById(R.id.wind2_guide2);
        wind3Guide2 = (TextView) views.get(1).findViewById(R.id.wind3_guide2);
        weather1ImgGuide1 = (ImageView) views.get(0).findViewById(R.id.weather1_img_guide1);
        weather2ImgGuide1 = (ImageView) views.get(0).findViewById(R.id.weather2_img_guide1);
        weather3ImgGuide1 = (ImageView) views.get(0).findViewById(R.id.weather3_img_guide1);
        weather1ImgGuide2 = (ImageView) views.get(1).findViewById(R.id.weather1_img_guide2);
        weather2ImgGuide2 = (ImageView) views.get(1).findViewById(R.id.weather2_img_guide2);
        weather3ImgGuide2 = (ImageView) views.get(1).findViewById(R.id.weather3_img_guide2);

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String cityCode = sharedPreferences.getString("main_city_code", "101160101");//默认是北京的编号
        Log.d("myWeather", cityCode);

        //初始化为一个城市
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络可用");
            queryWeatherCode(cityCode);
        } else {
            Log.d("myWeather", "网络不可用");
            Toast.makeText(MainActivity.this, "network not available!", Toast.LENGTH_LONG).show();
        }

    }

    void initViewPagers() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.first_three_days, null));
        views.add(inflater.inflate(R.layout.second_three_days, null));
        viewPagerAdapter = new ViewPagerAdapter(views, this);
        viewPager = (ViewPager) findViewById(R.id.week_broadcast);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    void initDots(){
        dots = new ImageView[views.size()];
        for (int i = 0;i < views.size();i++){
            dots[i] = (ImageView)findViewById(ids[i]);
        }
    }

    void initWeatherImg(TodayWeather todayWeather, ImageView imageView){
        String[] types = todayWeather.getType().split("转");
        String type = types[0];
        if(null == type || type.isEmpty())
            imageView.setImageLevel(0);
        else if(type.equals("晴")){
            imageView.setImageLevel(0);
        }else if (type.equals("暴雪")){
            imageView.setImageLevel(8);
        }else if (type.equals("暴雨")){
            imageView.setImageLevel(1);
        }else if (type.equals("大暴雨")){
            imageView.setImageLevel(2);
        }else if (type.equals("大雪")){
            imageView.setImageLevel(3);
        }else if (type.equals("大雨")){
            imageView.setImageLevel(4);
        }else if (type.equals("多云")){
            imageView.setImageLevel(5);
        }else if (type.equals("雷阵雨")){
            imageView.setImageLevel(6);
        }else if (type.equals("雷阵雨冰雹")){
            imageView.setImageLevel(7);
        }else if (type.equals("沙尘暴")){
            imageView.setImageLevel(9);
        }else if (type.equals("特大暴雨")){
            imageView.setImageLevel(10);
        }else if (type.equals("雾")){
            imageView.setImageLevel(11);
        }else if (type.equals("小雪")){
            imageView.setImageLevel(12);
        }else if (type.equals("小雨")){
            imageView.setImageLevel(13);
        }else if (type.equals("阴")){
            imageView.setImageLevel(14);
        }else if (type.equals("雨夹雪")){
            imageView.setImageLevel(15);
        }else if (type.equals("阵雪")){
            imageView.setImageLevel(16);
        }else if (type.equals("阵雨")){
            imageView.setImageLevel(17);
        }else if (type.equals("中雪")){
            imageView.setImageLevel(18);
        }else if (type.equals("中雨")){
            imageView.setImageLevel(19);
        }else{
            imageView.setImageLevel(0);
        }
    }

    void initPMFace(TodayWeather todayWeather, ImageView pmImg){
        //有可能在比较详细的地址区域内的PM值缺失而报错，当有这种情况，可以选择提示
        //信息不足，此时需要加入新的突破，也可以选择区域所在省市的大情况或者就近选择。
        //因为临近的地区代码差不多的。
        if (todayWeather.getPm25().equals("~~")){
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

    void updateTodayWeather(Map<String, TodayWeather> weathers) {
        TodayWeather todayWeather = null;
        TodayWeather yesterdayWeather = null;
        TodayWeather firstWeather = null;
        TodayWeather secondWeather = null;
        TodayWeather thirdWeather = null;
        TodayWeather fourthWeather = null;
        TodayWeather fifthWeather = null;
        TodayWeather detail = null;
        todayWeather = weathers.get("todayWeather");
        yesterdayWeather = weathers.get("yesterdayWeather");
        firstWeather = weathers.get("firstWeather");
        secondWeather = weathers.get("secondWeather");
        thirdWeather = weathers.get("thirdWeather");
        fourthWeather = weathers.get("fourthWeather");
        fifthWeather = weathers.get("fifthWeather");
        detail = weathers.get("detail");

        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度:" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText("今日 " + todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() +  "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:" + todayWeather.getFengli());
        wendu.setText(todayWeather.getWendu() + "℃");
        detailTv.setText(detail.getCity());

        weekToday1Guide1.setText(yesterdayWeather.getDate());
        weekToday2Guide1.setText(firstWeather.getDate());
        weekToday3Guide1.setText(secondWeather.getDate());
        weekToday1Guide2.setText(thirdWeather.getDate());
        weekToday2Guide2.setText(fourthWeather.getDate());
        weekToday3Guide2.setText(fifthWeather.getDate());
        temperature1Guide1.setText(yesterdayWeather.getHigh() + "~" + yesterdayWeather.getLow());
        temperature2Guide1.setText(firstWeather.getHigh() + "~" + firstWeather.getLow());
        temperature3Guide1.setText(secondWeather.getHigh() + "~" + secondWeather.getLow());
        temperature1Guide2.setText(thirdWeather.getHigh() + "~" + thirdWeather.getLow());
        temperature2Guide2.setText(fourthWeather.getHigh() + "~" + fourthWeather.getLow());
        temperature3Guide2.setText(fifthWeather.getHigh() + "~" + fifthWeather.getLow());
        wind1Guide1.setText(yesterdayWeather.getFengli());
        wind2Guide1.setText(firstWeather.getFengli());
        wind3Guide1.setText(secondWeather.getFengli());
        wind1Guide2.setText(thirdWeather.getFengli());
        wind2Guide2.setText(fourthWeather.getFengli());
        wind3Guide2.setText(fifthWeather.getFengli());
        climate1Guide1.setText(yesterdayWeather.getType());
        climate2Guide1.setText(firstWeather.getType());
        climate3Guide1.setText(secondWeather.getType());
        climate1Guide2.setText(thirdWeather.getType());
        climate2Guide2.setText(fourthWeather.getType());
        climate3Guide2.setText(fifthWeather.getType());
        /**
         * 下行代码用于更新因天气变化而变化的图片，注意要在这里进行更换，因为更新ui界面只能在主界面完成
         */
        initWeatherImg(todayWeather, weatherImg);
        initWeatherImg(yesterdayWeather, weather1ImgGuide1);
        initWeatherImg(firstWeather, weather2ImgGuide1);
        initWeatherImg(secondWeather, weather3ImgGuide1);
        initWeatherImg(thirdWeather, weather1ImgGuide2);
        initWeatherImg(fourthWeather, weather2ImgGuide2);
        initWeatherImg(fifthWeather, weather3ImgGuide2);
        /**
         * 下行代码用于更新pm值不同人的体验的表情变化图，理由同上
         */
        initPMFace(todayWeather, pmImg);
        if(null == todayWeather.getCity() || todayWeather.getCity().isEmpty()){
            Message msg = new Message();
            msg.what = INIT_NO_DATA;
            mHandler.sendMessage(msg);
            Log.d("myWeather", "no data from API for that city");
        }
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
            //此处处理点击更新按钮后旋转按钮且不允许再次点击该按钮
            animation = AnimationUtils.loadAnimation(this, R.anim.update_spin);
            LinearInterpolator lin = new LinearInterpolator();
            animation.setInterpolator(lin);
            if(null != animation){
                Message msg = new Message();
                msg.what = UPDATE_SPIN_START;
                mHandler.sendMessage(msg);
                Log.d("myWeather", "clicked update, can`t click until finished");
            }

            //点击刷新后读取sharedpreferences获得之前保存的当前地址刷新天气信息，并且将默认地址设置为北京
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101011100");//默认是北京的编号
            Log.d("myWeather", cityCode);

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络贼好");
                queryWeatherCode(cityCode);
            } else {
                Log.d("myWeather", "网络完蛋了");
                Toast.makeText(MainActivity.this, "network not available!", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            String newCityName = data.getStringExtra("cityName");
            Log.d("myWeather", "汝选择的城市代码乃"+newCityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather", "网络贼好");
                queryWeatherCode(newCityCode);
                SharedPreferences settings
                        = (SharedPreferences)getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("main_city_code", newCityCode);
                editor.putString("main_city_name", newCityName);
                editor.commit();
                Log.d("myWeather", "更新了默认天气地址：" + newCityCode);
                Log.d("myWeather", "更新了默认天气地址：" + newCityName);
            }else{
                Log.d("myWeather", "网络完蛋");
                Toast.makeText(MainActivity.this, "network not available！", Toast.LENGTH_LONG).show();
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
                    /**
                     * 报文例子
                     *D/myWeather: <resp><city>北京</city><updatetime>20:05</updatetime><wendu>3</wendu><fengli>1级</fengli>
                     * <shidu>68%</shidu><fengxiang>东北风</fengxiang><sunrise_1>07:15</sunrise_1><sunset_1>16:50</sunset_1><sunrise_2></sunrise_2>
                     * <sunset_2></sunset_2><environment><aqi>147</aqi><pm25>112</pm25><suggest>儿童、老年人及心脏、呼吸系统疾病患者人群应减少长时间或高强度户外锻炼</suggest>
                     * <quality>轻度污染</quality><MajorPollutants>颗粒物(PM2.5)</MajorPollutants><o3>2</o3><co>3</co><pm10>170</pm10><so2>24</so2><no2>86</no2>
                     * <time>20:00:00</time></environment><alarm><cityKey>10101</cityKey><cityName><![CDATA[北京市]]></cityName>
                     * <alarmType><![CDATA[霾]]></alarmType><alarmDegree><![CDATA[黄色]]></alarmDegree>
                     * <alarmText><![CDATA[北京市气象台发布霾黄色预警]]></alarmText>
                     * <alarm_details><![CDATA[北京市气象台29日17时00分发布霾黄色预警,预计，29日夜间至30日白天，本市大部分地区将出现轻度-中度霾，能见度较低，请注意防范。]]></alarm_details>
                     * <standard><![CDATA[预计未来24小时内可能出现下列条件之一并将持续或实况已达到下列条件之一并可能持续：能见度小于3000米且相对湿度小于80%的霾；能见度小于3000米且相对湿度大于等于80%，PM2.5浓度大于115微克/立方米且小于等于150微克/立方米：能见度小于5000米，PM2.5浓度大于150微克/立方米且小于等于250微克/立方米。]]></standard>
                     * <suggest><![CDATA[1、空气质量明显降低，人员需适当防护；2、一般人群适量减少户外活动，儿童、老人及易感人群应减少外出。]]></suggest>
                     * <imgUrl><![CDATA[http://static.etouch.cn/apps/weather/alarm_icon-1/mai_yellow-1.png]]></imgUrl>
                     * <time>2016-11-29 17:00:00</time></alarm>
                     * <yesterday><date_1>28日星期一</date_1><high_1>高温 8℃</high_1><low_1>低温 -4℃</low_1><day_1><type_1>晴</type_1><fx_1>无持续风向</fx_1><fl_1>微风</fl_1></day_1><night_1><type_1>雾</type_1><fx_1>无持续风向</fx_1><fl_1>微风</fl_1></night_1></yesterday>
                     * <forecast><weather><date>29日星期二</date><high>高温 4℃</high><low>低温 -3℃</low><day><type>雨夹雪</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>雨夹雪</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather>
                     * <weather><date>30日星期三</date><high>高温 9℃</high><low>低温 -1℃</low><day><type>雾</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>晴</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather>
                     * <weather><date>1日星期四</date><high>高温 8℃</high><low>低温 -4℃</low><day><type>晴</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>晴</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather>
                     * <weather><date>2日星期五</date><high>高温 7℃</high><low>低温 -4℃</low><day><type>晴</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>晴</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather>
                     * <weather><date>3日星期六</date><high>高温 7℃</high><low>低温 -2℃</low><day><type>雾</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></day><night><type>雾</type><fengxiang>无持续风向</fengxiang><fengli>微风级</fengli></night></weather></forecast>
                     * <zhishu><zhishu><name>晨练指数</name><value>不宜</value><detail>有雾，空气质量差，且部分地面可能有积雪，路面湿滑，请避免户外晨练，建议在室内做锻炼。</detail></zhishu><zhishu><name>舒适度</name><value>较舒适</value><detail>白天天气阴沉，会感到有点儿凉，但大部分人完全可以接受。</detail></zhishu>
                     * <zhishu><name>穿衣指数</name><value>较冷</value><detail>建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。</detai
                     *
                     */


                    Map<String, TodayWeather> weathers = new HashMap<String, TodayWeather>();
                    weathers = parseXML(responseStr);
                    todayWeather = weathers.get("todayWeather");

                    if(null == todayWeather.getCity() || todayWeather.getCity().isEmpty()){
                        Message msg = new Message();
                        msg.what = INIT_NO_DATA;
                        mHandler.sendMessage(msg);
                        Log.d("myWeather", "no data from API for that city");
                    }
                    if (todayWeather != null && null != todayWeather.getCity() && !todayWeather.getCity().isEmpty()) {
                        Log.d("myWeather", todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = weathers;
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

    private Map<String, TodayWeather> parseXML(String xmldate) {
        TodayWeather todayWeather = null;
        TodayWeather yesterdayWeather = null;
        TodayWeather firstWeather = null;
        TodayWeather secondWeather = null;
        TodayWeather thirdWeather = null;
        TodayWeather fourthWeather = null;
        TodayWeather fifthWeather = null;
        Map<String, TodayWeather> weathers = new HashMap<String, TodayWeather>();
        TodayWeather detail = null;

        int detailCount = 0;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        int typeCountBro = 0;
        int fxCountBro = 0;
        int flCountBro = 0;

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
                        //主体的天气信息获取
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }else if (xmlPullParser.getName().equals("yesterday")) {
                            yesterdayWeather = new TodayWeather();
                        }else if (xmlPullParser.getName().equals("forecast")) {
                            firstWeather = new TodayWeather();
                            secondWeather = new TodayWeather();
                            thirdWeather = new TodayWeather();
                            fourthWeather = new TodayWeather();
                            fifthWeather = new TodayWeather();
                            detail = new TodayWeather();
                        }

                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather city:    " + xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                                break;
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather updatetime:    " + xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                                break;
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather shidu:    " + xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                                break;
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather wendu:    " + xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                                break;
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather pm25:    " + xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                                break;
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather quality:    " + xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                                break;
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather fengxiang:    " + xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather fengli:    " + xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather date:    " + xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather high:    " + xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather low:    " + xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "todayWeather type:    " + xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                                break;
                            }
                        }
                        //昨天的天气信息获取
                        if (yesterdayWeather != null){
                            if (xmlPullParser.getName().equals("date_1")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "yesterdayWeather date_1:    " + xmlPullParser.getText());
                                String date[] = xmlPullParser.getText().split("日");
                                yesterdayWeather.setDate(date[1]);
                                break;
                            } else if (xmlPullParser.getName().equals("fx_1") && fxCountBro == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "yesterdayWeather fengxiangDay:    " + xmlPullParser.getText());
                                yesterdayWeather.setFengxiang(xmlPullParser.getText());
                                fxCountBro++;
                                break;
                            } else if (xmlPullParser.getName().equals("fx_1") && fxCountBro == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "yesterdayWeather fengxiangNight:    " + xmlPullParser.getText());
                                if (!yesterdayWeather.getFengxiang().equals(xmlPullParser.getText())){
                                    yesterdayWeather.setFengxiang(yesterdayWeather.getFengxiang() + "转" + xmlPullParser.getText());
                                }
                                fxCountBro++;
                                break;
                            } else if (xmlPullParser.getName().equals("fl_1") && flCountBro == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "yesterdayWeather fengliDay:    " + xmlPullParser.getText());
                                yesterdayWeather.setFengli(xmlPullParser.getText());
                                flCountBro++;
                                break;
                            } else if (xmlPullParser.getName().equals("fl_1") && flCountBro == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "yesterdayWeather fengliNight:    " + xmlPullParser.getText());
                                if (!yesterdayWeather.getFengli().equals(xmlPullParser.getText())){
                                    yesterdayWeather.setFengli(yesterdayWeather.getFengli() + "~" + xmlPullParser.getText());
                                }
                                flCountBro++;
                                break;
                            } else if (xmlPullParser.getName().equals("high_1")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "yesterdayWeather high_1:    " + xmlPullParser.getText());
                                yesterdayWeather.setHigh(xmlPullParser.getText());
                                break;
                            } else if (xmlPullParser.getName().equals("low_1")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "yesterdayWeather low_1:    " + xmlPullParser.getText());
                                yesterdayWeather.setLow(xmlPullParser.getText());
                                break;
                            }  else if (xmlPullParser.getName().equals("type_1") && typeCountBro == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "yesterdayWeather type_1Day:    " + xmlPullParser.getText());
                                yesterdayWeather.setType(xmlPullParser.getText());
                                typeCountBro++;
                                break;
                            } else if (xmlPullParser.getName().equals("type_1") && typeCountBro == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "yesterdayWeather type_1Night:    " + xmlPullParser.getText());
                                if(!xmlPullParser.getText().equals(yesterdayWeather.getType())){
                                    yesterdayWeather.setType(yesterdayWeather.getType() + "转" + xmlPullParser.getText());
                                }
                                typeCountBro++;
                                break;
                            }
                        }

                        if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 1) {
                            fengxiangCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 2) {
                            fengxiangCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1) {
                            fengliCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2) {
                            fengliCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 1) {
                            typeCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("detail") && detailCount == 0) {
                            detailCount++;
                            break;
                        }
                        //获取未来4天的天气预报
                        if (secondWeather != null){
                            if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "secondWeather fengxiangday:    " + xmlPullParser.getText());
                                secondWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 4) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "secondWeather fengxiangnight:    " + xmlPullParser.getText());
                                if (!secondWeather.getFengxiang().equals(xmlPullParser.getText())){
                                    secondWeather.setFengxiang(secondWeather.getFengxiang() + "转" + xmlPullParser.getText());
                                }
                                fengxiangCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "secondWeather fengliday:    " + xmlPullParser.getText());
                                secondWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 4) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "secondWeather fenglinight:    " + xmlPullParser.getText());
                                if (!secondWeather.getFengli().equals(xmlPullParser.getText())){
                                    secondWeather.setFengli(secondWeather.getFengli() + "~" + xmlPullParser.getText());
                                }
                                fengliCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "secondWeather date:    " + xmlPullParser.getText());
                                String date[] = xmlPullParser.getText().split("日");
                                secondWeather.setDate(date[1]);
                                dateCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "secondWeather high:    " + xmlPullParser.getText());
                                secondWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "secondWeather low:    " + xmlPullParser.getText());
                                secondWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "secondWeather typeday:    " + xmlPullParser.getText());
                                secondWeather.setType(xmlPullParser.getText());
                                typeCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "secondWeather typenight:    " + xmlPullParser.getText());
                                if(!xmlPullParser.getText().equals(secondWeather.getType())){
                                    secondWeather.setType(secondWeather.getType() + "转" + xmlPullParser.getText());
                                }
                                typeCount++;
                                break;
                            }
                        }
                        if (thirdWeather != null){
                            if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 5) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "thirdWeather fengxiangday:    " + xmlPullParser.getText());
                                thirdWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 6) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "thirdWeather fengxiangnight:    " + xmlPullParser.getText());
                                if (!thirdWeather.getFengxiang().equals(xmlPullParser.getText())){
                                    thirdWeather.setFengxiang(thirdWeather.getFengxiang() + "转" + xmlPullParser.getText());
                                }
                                fengxiangCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 5) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "thirdWeather fengliday:    " + xmlPullParser.getText());
                                thirdWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 6) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "thirdWeather fenglinight:    " + xmlPullParser.getText());
                                if (!thirdWeather.getFengli().equals(xmlPullParser.getText())){
                                    thirdWeather.setFengli(thirdWeather.getFengli() + "~" + xmlPullParser.getText());
                                }
                                fengliCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "thirdWeather date:    " + xmlPullParser.getText());
                                String date[] = xmlPullParser.getText().split("日");
                                thirdWeather.setDate(date[1]);
                                dateCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "thirdWeather high:    " + xmlPullParser.getText());
                                thirdWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "thirdWeather low:    " + xmlPullParser.getText());
                                thirdWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 4) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "thirdWeather typeday:    " + xmlPullParser.getText());
                                thirdWeather.setType(xmlPullParser.getText());
                                typeCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 5) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "thirdWeather typenight:    " + xmlPullParser.getText());
                                if(!xmlPullParser.getText().equals(thirdWeather.getType())){
                                    thirdWeather.setType(thirdWeather.getType() + "转" + xmlPullParser.getText());
                                }
                                typeCount++;
                                break;
                            }
                        }
                        if (fourthWeather != null){
                            if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 7) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fourthWeather fengxiangday:    " + xmlPullParser.getText());
                                fourthWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 8) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fourthWeather fengxiangnight:    " + xmlPullParser.getText());
                                if (!fourthWeather.getFengxiang().equals(xmlPullParser.getText())){
                                    fourthWeather.setFengxiang(fourthWeather.getFengxiang() + "转" + xmlPullParser.getText());
                                }
                                fengxiangCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 7) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fourthWeather fengliday:    " + xmlPullParser.getText());
                                fourthWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 8) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fourthWeather fenglinight:    " + xmlPullParser.getText());
                                if (!fourthWeather.getFengli().equals(xmlPullParser.getText())){
                                    fourthWeather.setFengli(fourthWeather.getFengli() + "~" + xmlPullParser.getText());
                                }
                                fengliCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fourthWeather date:    " + xmlPullParser.getText());
                                String date[] = xmlPullParser.getText().split("日");
                                fourthWeather.setDate(date[1]);
                                dateCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fourthWeather high:    " + xmlPullParser.getText());
                                fourthWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fourthWeather low:    " + xmlPullParser.getText());
                                fourthWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 6) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fourthWeather typeday:    " + xmlPullParser.getText());
                                fourthWeather.setType(xmlPullParser.getText());
                                typeCount++;
                                break;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 7) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "fourthWeather typenight:    " + xmlPullParser.getText());
                                if(!xmlPullParser.getText().equals(fourthWeather.getType())){
                                    fourthWeather.setType(fourthWeather.getType() + "转" + xmlPullParser.getText());
                                }
                                typeCount++;
                                break;
                            }
                        }
                        if (fifthWeather != null){
                        if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 9) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fifthWeather fengxiangday:    " + xmlPullParser.getText());
                            fifthWeather.setFengxiang(xmlPullParser.getText());
                            fengxiangCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 10) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fifthWeather fengxiangnight:    " + xmlPullParser.getText());
                            if (!fifthWeather.getFengxiang().equals(xmlPullParser.getText())){
                                fifthWeather.setFengxiang(fifthWeather.getFengxiang() + "转" + xmlPullParser.getText());
                            }
                            fengxiangCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 9) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fifthWeather fengliday:    " + xmlPullParser.getText());
                            fifthWeather.setFengli(xmlPullParser.getText());
                            fengliCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 10) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fifthWeather fenglinight:    " + xmlPullParser.getText());
                            if (!fifthWeather.getFengli().equals(xmlPullParser.getText())){
                                fifthWeather.setFengli(fifthWeather.getFengli() + "~" + xmlPullParser.getText());
                            }
                            fengliCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("date") && dateCount == 4) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fifthWeather date:    " + xmlPullParser.getText());
                            String date[] = xmlPullParser.getText().split("日");
                            fifthWeather.setDate(date[1]);
                            dateCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("high") && highCount == 4) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fifthWeather high:    " + xmlPullParser.getText());
                            fifthWeather.setHigh(xmlPullParser.getText());
                            highCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("low") && lowCount == 4) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fifthWeather low:    " + xmlPullParser.getText());
                            fifthWeather.setLow(xmlPullParser.getText());
                            lowCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 8) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fifthWeather typeday:    " + xmlPullParser.getText());
                            fifthWeather.setType(xmlPullParser.getText());
                            typeCount++;
                            break;
                        } else if (xmlPullParser.getName().equals("type") && typeCount == 9) {
                            eventType = xmlPullParser.next();
                            Log.d("myWeather", "fifthWeather typenight:    " + xmlPullParser.getText());
                            if(!xmlPullParser.getText().equals(fifthWeather.getType())){
                                fifthWeather.setType(fifthWeather.getType() + "转" + xmlPullParser.getText());
                            }
                            typeCount++;
                            break;
                        }
                    }
                        if (detail != null){
                            if (xmlPullParser.getName().equals("detail") && detailCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "detail:    " + xmlPullParser.getText());
                                detail.setCity(xmlPullParser.getText());
                                detailCount++;
                                break;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
            firstWeather = todayWeather;
            if(firstWeather.getDate() != null && !firstWeather.getDate().isEmpty()){
                String[] date = firstWeather.getDate().split("日");
                firstWeather.setDate(date[1]);
            }
            //自此获得了最近6天的天气信息

            if (null != animation){
                Message msg = new Message();
                msg.what = UPDATE_SPIN_STOP;
                mHandler.sendMessage(msg);
                Log.d("myWeather", "updating finished, can click again now");
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        weathers.put("todayWeather", todayWeather);
        weathers.put("firstWeather", firstWeather);
        weathers.put("secondWeather", secondWeather);
        weathers.put("thirdWeather", thirdWeather);
        weathers.put("fourthWeather", fourthWeather);
        weathers.put("fifthWeather", fifthWeather);
        weathers.put("yesterdayWeather", yesterdayWeather);
        weathers.put("detail", detail);

        return weathers;
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int a = 0;a<ids.length;a++){
            if(a == position){
                dots[a].setImageResource(R.drawable.page_indicator_focused);
            }else{
                dots[a].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

