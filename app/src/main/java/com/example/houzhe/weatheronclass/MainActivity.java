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
import java.util.List;

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
            climateTv, windTv, city_name_Tv, humidityTv, wendu, wenduTittle, pm25;
    private ImageView weatherImg, pmImg;

    private Animation animation;

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv1, R.id.iv2, R.id.iv3};
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
                    wenduTittle.setText("N/A");
                    pm25.setText("N/A");
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
        initView();

        initViewPagers();
        initDots();
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
        views.add(inflater.inflate(R.layout.guide1, null));
        views.add(inflater.inflate(R.layout.guide2, null));
        views.add(inflater.inflate(R.layout.guide3, null));
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

    void initWeatherImg(TodayWeather todayWeather){
        if(null == todayWeather.getType() || todayWeather.getType().isEmpty())
            weatherImg.setImageLevel(0);
        else if(todayWeather.getType().equals("晴")){
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
        }else if (todayWeather.getType().equals("雨夹雪")){
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


                    todayWeather = parseXML(responseStr);

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

