package com.example.houzhe.weatheronclass;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by houzhe on 2016/11/29.
 */

public class GuidePages extends Activity implements ViewPager.OnPageChangeListener{

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv1, R.id.iv2, R.id.iv3};

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String fistTime = sharedPreferences.getString("check_fist_using", "0");
        if (fistTime.equals("0")){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.guide_pages);
            initViews();
            initDots();
            button = (Button)views.get(2).findViewById(R.id.begin);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    SharedPreferences settings
                            = (SharedPreferences)getSharedPreferences("config", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("check_fist_using", "1");
                    editor.commit();
                    Intent intent = new Intent(GuidePages.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }else if(fistTime.equals("1")){
            super.onCreate(savedInstanceState);
            Intent intent = new Intent(GuidePages.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    void initDots(){
        dots = new ImageView[views.size()];
        for (int i = 0;i < views.size();i++){
            dots[i] = (ImageView)findViewById(ids[i]);
        }
    }

    private void initViews(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.guide1, null));
        views.add(inflater.inflate(R.layout.guide2, null));
        views.add(inflater.inflate(R.layout.guide3, null));
        viewPagerAdapter = new ViewPagerAdapter(views, this);
        viewPager = (ViewPager) findViewById(R.id.guidePages);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnPageChangeListener(this);
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
