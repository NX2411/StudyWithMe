package com.example.studywithme;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.LocaleList;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Calendar;

public class stopwatch extends Fragment {

    MyDBHelper myHelper;
    SQLiteDatabase sqlDB;

    dialog_setGoaltime dlg;
    dialog_finishStopwatch dlg_finish;
    private Chronometer stopwatch;
    private Boolean isPlaying = false;
    private long pauseOffset;
    int goalHour, goalMin;
    int hour, min, sec;

    String mYear, mMonth, mDate;
    String dateCode;

    Button setGoalTimeBtn, stopwatchBtn, finishStopwatchBtn;
    TextView goalTimeTv;

    boolean DateCodeExit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        myHelper = new MyDBHelper(getContext());

        setGoalTimeBtn = view.findViewById(R.id.btn_set);
        stopwatch = view.findViewById(R.id.chronometer);
        stopwatchBtn = view.findViewById(R.id.btn_stopwatch);
        finishStopwatchBtn = view.findViewById(R.id.btn_finishStopwatch);
        goalTimeTv = view.findViewById(R.id.tV_goalTime);

        //오늘 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        mYear = Integer.toString(calendar.get(Calendar.YEAR));
        mMonth = Integer.toString(calendar.get(Calendar.MONTH)+1);
        mDate = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));



        //다이얼로그 프래그먼트에서 값을 받아오기 위한 작업
        dlg = new dialog_setGoaltime();
        dlg.setTargetFragment(this, 1);     //현재 프래그먼트를 타겟으로 설정하고 requestCode를 1로 설정

        //현재 프래그먼트의 타겟 프래그먼트 설정 - 값 전달
        dlg_finish = new dialog_finishStopwatch();
        this.setTargetFragment(dlg_finish, 2);

        setGoalTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.show(getActivity().getSupportFragmentManager(), "setGoaltime");
            }
        });

        //현재 스톱워치가 실행중인지 검사
        if(!isPlaying){
            stopwatchBtn.setText("시작");
            finishStopwatchBtn.setEnabled(false);
        }else{
            stopwatchBtn.setText("일시정지");
            finishStopwatchBtn.setEnabled(true);
        }

        //날짜코드가 있는지 없는지 받아오기
        if(getDateCode()){
            DateCodeExit = true;
        }
        else{   //없으면 날짜 데이터 생성해주기
            insertDate();
            DateCodeExit = false;
        }

        //스톱워치 시작 버튼
        stopwatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPlaying){
                    stopwatch.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    stopwatch.start();
                    stopwatchBtn.setText("일시정지");
                    finishStopwatchBtn.setEnabled(true);
                    isPlaying = true;
                }
                else {
                    stopwatch.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - stopwatch.getBase();
                    stopwatchBtn.setText("재시작");
                    isPlaying = false;
                }
            }
        });

        //학습 시간 저장
        finishStopwatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopwatch.stop();
                isPlaying = false;
                stopwatchBtn.setText("시작");
                finishStopwatchBtn.setEnabled(false);

                //스톱워치 시간 받아오기
                long current = SystemClock.elapsedRealtime() - stopwatch.getBase();
                int time = (int) current / 1000;
                hour = time / (60*60);
                min = time % (60*60) /60;
                sec = time % 60;

                //스톱워치 초기화
                stopwatch.setBase(SystemClock.elapsedRealtime());
                pauseOffset = 0;
                if(!DateCodeExit){
                    getDateCode();
                }
                sendResult(2);
                dlg_finish.show(getActivity().getSupportFragmentManager(), "finishStopwatch");
            }
        });

        return view;
    }

    //다이얼로그 프래그먼트에 값 전달해주기
    private void sendResult(int REQUEST_CODE) {
        Intent intent = new Intent();
        intent.putExtra("goalHour", goalHour);
        intent.putExtra("goalMin", goalMin);

        intent.putExtra("hour", hour);
        intent.putExtra("min", min);

        intent.putExtra("dateCode", dateCode);

        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
    }

    //다이얼로그 프래그먼트에서 값을 받아오기
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            goalHour = data.getIntExtra("hour", 0);
            goalMin = data.getIntExtra("min", 0);

            goalTimeTv.setText(goalHour + "시간 " + goalMin + "분 " );
        }
    }

    //데이트 코드 받아오기
    public boolean getDateCode(){
        boolean codeExit = false;
        sqlDB = myHelper.getWritableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT dateCode FROM studyDate WHERE year = '" + mYear+ "' AND month = '" +
                mMonth + "' AND date = '" + mDate + "';", null); //날짜 조회

        if (cursor.moveToNext()) { //조회한 행이 끝날 때까지 실행하는 역할
            dateCode = Integer.toString(cursor.getInt(0));
            codeExit = true;
        }


        cursor.close();
        sqlDB.close();
        myHelper.close();
        return codeExit;
    }

    //오늘 날짜의 데이터 생성
    public void insertDate(){

        sqlDB = myHelper.getWritableDatabase();

        sqlDB.execSQL("INSERT INTO studyDate (year, month, date) VALUES(?, ?, ?)",
                new String[]{mYear, mMonth, mDate});

        sqlDB.close();
        myHelper.close();
    }
}