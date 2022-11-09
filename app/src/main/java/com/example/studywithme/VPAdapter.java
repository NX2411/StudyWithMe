package com.example.studywithme;

import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class VPAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> pages;

    //뷰페이저 어뎁터. 프래그먼트로 이루어진 ArrayList에 프래그먼트 추가하여 뷰페이저 생성
    public VPAdapter(@NonNull FragmentManager fm) {
        super(fm);
        pages = new ArrayList<Fragment>();
        pages.add(new home());
        pages.add(new pastSchedule());
        pages.add(new stopwatch());

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

}
