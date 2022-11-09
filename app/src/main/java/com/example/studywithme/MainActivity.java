package com.example.studywithme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    VPAdapter adapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager vp = findViewById(R.id.viewpager);
        adapter = new VPAdapter(getSupportFragmentManager());
        vp.setAdapter(adapter);

        //연동
        tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(vp);
        setupTabIcons();



    }

    //탭 아이콘 설정
    private void setupTabIcons(){
        //홈
        View viewHome = getLayoutInflater().inflate(R.layout.custom_tab, null);
        ImageView imgHome = viewHome.findViewById(R.id.img_tab);
        TextView txtHome = viewHome.findViewById(R.id.txt_tab);
        imgHome.setImageResource(R.drawable.home);
        txtHome.setText("home");

        //지난일정
        View viewPastSche = getLayoutInflater().inflate(R.layout.custom_tab, null);
        ImageView imgPastSche = viewPastSche.findViewById(R.id.img_tab);
        TextView txPastSche = viewPastSche.findViewById(R.id.txt_tab);
        imgPastSche.setImageResource(R.drawable.past_schedule);
        txPastSche.setText("pastSchedule");

        //스톱워치
        View viewStopwatch = getLayoutInflater().inflate(R.layout.custom_tab, null);
        ImageView imgStopwatch = viewStopwatch.findViewById(R.id.img_tab);
        TextView txtStopwatch = viewStopwatch.findViewById(R.id.txt_tab);
        imgStopwatch.setImageResource(R.drawable.stopwatch);
        txtStopwatch.setText("stopwatch");

        //탭 레이아웃에 커스텀뷰 추가
        tabLayout.getTabAt(0).setCustomView(viewHome);
        tabLayout.getTabAt(1).setCustomView(viewPastSche);
        tabLayout.getTabAt(2).setCustomView(viewStopwatch);

    }

}