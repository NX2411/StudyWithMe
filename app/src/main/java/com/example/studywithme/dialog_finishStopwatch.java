package com.example.studywithme;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class dialog_finishStopwatch extends DialogFragment {

    MyDBHelper myHelper;
    SQLiteDatabase sqlDB;

    Button cancelBtn, saveBtn;
    TextView goalCheckTv, goalTime, studyTime;
    String goalTimeSt, studyTimeSt;
    int goalHour, goalMin, studyHour, studyMin;

    String dateCode;

    boolean isTimeExit = false; //학습 시간 존재 여부

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_finish_stopwatch, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myHelper = new MyDBHelper(getContext());

        goalCheckTv = view.findViewById(R.id.tV_goalCheck);
        goalTime = view.findViewById(R.id.tV_goalTime);
        studyTime = view.findViewById(R.id.tV_studyTime);
        cancelBtn = view.findViewById(R.id.btn_cancel);
        saveBtn = view.findViewById(R.id.btn_save);

        //목표시간에 따라 달성했는지 실패했는지 구분해서 텍스트 지정
        if(goalHour > studyHour){
            goalCheckTv.setText("목표 시간 달성 실패");
        }
        else if(goalHour == studyHour){
            if(goalMin <= studyMin){
                goalCheckTv.setText("목표 시간 달성!");
            }
            else{
                goalCheckTv.setText("목표 시간 달성 실패");
            }
        }

        //값 전달받아서 목표시간과 총 학습 시간 띄워주기
        goalTime.setText("목표 시간 : " + goalHour + "시간 " + goalMin + "분 ");
        studyTime.setText("총 학습 시간 : " + studyHour + "시간 " + studyMin + "분");

        goalTimeSt = goalHour + "시간 " + goalMin + "분 ";
        studyTimeSt = studyHour + "시간 " + studyMin + "분";

        //오늘 날짜에 학습 시간 데이터베이스가 존재하는지 검사
        checkTime();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "학습 시간 저장을 취소했습니다.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //학습 시간이 존재하면 업데이트
                if(isTimeExit){
                    updateTime();
                    Toast.makeText(getContext(), "기존 존재하던 학습 시간을 변경했습니다.", Toast.LENGTH_SHORT).show();
                }
                //학습 시간이 존재하지 않으면 새로 저장
                else{
                    saveTime();
                    Toast.makeText(getContext(), "학습 시간을 저장했습니다.", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });

        return view;
    }

    //학습 시간 존재 여부 검사
    public void checkTime(){
        sqlDB = myHelper.getReadableDatabase();

        Cursor cursor = sqlDB.rawQuery("SELECT * FROM studyTime WHERE dateCode = '"+dateCode+"';", null); //날짜 조회
        if(cursor.moveToNext()){
            isTimeExit = true;
        }
    }

    //학습 시간 저장
    public void saveTime(){
        sqlDB = myHelper.getWritableDatabase();

        sqlDB.execSQL("INSERT INTO studyTime (stdTime, goalTime, dateCode) VALUES(?, ?, ?)",
                new String[]{studyTimeSt, goalTimeSt, dateCode});

        sqlDB.close();
        myHelper.close();
    }

    //학습 시간 수정
    public void updateTime(){
        sqlDB = myHelper.getWritableDatabase();

        sqlDB.execSQL("UPDATE studyTime SET stdTime = '"+studyTimeSt+"',  goalTime = '"+goalTimeSt+"' WHERE dateCode = '"+dateCode+"';");

        sqlDB.close();
        myHelper.close();
    }

    //결과값 전달 받기
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            goalHour = data.getIntExtra("goalHour", 0);
            goalMin = data.getIntExtra("goalMin", 0);

            studyHour = data.getIntExtra("hour", 0);
            studyMin = data.getIntExtra("min", 0);

            dateCode = data.getStringExtra("dateCode");
        }
    }
}