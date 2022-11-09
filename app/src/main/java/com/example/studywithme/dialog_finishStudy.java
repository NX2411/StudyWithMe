package com.example.studywithme;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class dialog_finishStudy extends DialogFragment {

    MyDBHelper myHelper;    //Helper 클래스 상속 받아 만든 클래스
    SQLiteDatabase sqlDB;   //Helper를 통해 생성된 DB를 받을 객체 - DB 객체

    Button cancelBtn, saveBtn;
    RatingBar achievementRb;
    EditText reviewEt, stdTimeEt;
    String mYear, mMonth, mDate;
    int dateCode;

    String achievemnt, stdTime,stdReview;

    boolean DateCodeExit;
    boolean isTimeExit = false;
    boolean isReviewExit = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_finish_study, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //데이터베이스 가져오기
        myHelper = new MyDBHelper(getContext());

        achievementRb = view.findViewById(R.id.rb_review);
        stdTimeEt = view.findViewById(R.id.eT_stdTime);
        reviewEt = view.findViewById(R.id.eT_review);

        cancelBtn = view.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //오늘 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        mYear = Integer.toString(calendar.get(Calendar.YEAR));
        mMonth = Integer.toString(calendar.get(Calendar.MONTH)+1);
        mDate = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));


        //데이트 코드가 있는지 검사하여 없으면 데이트 생성 후 데이트 코드 받아오기
        if(!getDateCode()){
            insertDate();
            getDateCode();
        }

        //기존 학습시간과 기존 학습 후기가 있는지 검사
        getTime();
        getReview();

        //데이터베이스에 값 저장해주기
        saveBtn = view.findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stdTime = stdTimeEt.getText().toString();
                achievemnt = Float.toString(achievementRb.getRating());
                stdReview = reviewEt.getText().toString();

                //예외처리 - 작성하지 않은 부분이 있으면 저장 불가
                if(!stdTime.equals("") && !stdReview.equals("")){

                    //학습시간이 존재하면 업데이트
                    if(isTimeExit){
                        updateTime();
                    }
                    //아니면 새로 입력
                    else{
                        insertTime();
                    }

                    //리뷰가 존재하면 업데이트
                    if(isReviewExit){
                        updateReview();
                    }
                    //아니면 새로 입력
                    else{
                        insertReview();
                    }
                    Toast.makeText(getContext(), "학습 후기가 저장되었습니다!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                Toast.makeText(getContext(), "작성하지 않은 부분이 있는지 확인해주세요!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    //날짜 데이터 생성
    public void insertDate(){

        sqlDB = myHelper.getWritableDatabase();

        sqlDB.execSQL("INSERT INTO studyDate (year, month, date) VALUES(?, ?, ?)",
                new String[]{mYear, mMonth, mDate});

        sqlDB.close();
        myHelper.close();
    }

    //데이트 코드 받아오기
    public boolean getDateCode(){
        boolean codeExit = false;
        sqlDB = myHelper.getWritableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT dateCode FROM studyDate WHERE year = '" + mYear+ "' AND month = '" +
                mMonth + "' AND date = '" + mDate + "';", null); //날짜 조회

        if (cursor.moveToNext()) { //조회한 행이 끝날 때까지 실행하는 역할 - 데이트 코드가 존재할 시
            dateCode = cursor.getInt(0);
            codeExit = true;
        }


        cursor.close();
        sqlDB.close();
        myHelper.close();
        return codeExit;
    }

    //학습 시간 받아오기
    public void getTime(){
        String stdTimeText;
        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT stdTime FROM studyTime WHERE dateCode = '"+dateCode+"';", null); //날짜 조회

        if (cursor.moveToNext()) { //조회한 행이 끝날 때까지 실행하는 역할 - 학습 시간 존재할 시
            isTimeExit = true;
            stdTimeText = cursor.getString(0);
        }
        else{
            stdTimeText = "0시간 0분";
        }

        //학습 시간 존재 여부에 따라 setText
        stdTimeEt.setText(stdTimeText);

        cursor.close();
        sqlDB.close();
        myHelper.close();

    }

    //학습 후기 받아오기
    public void getReview(){
        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT studyReview, stdRating FROM studyRating WHERE dateCode = '"+dateCode+"';", null); //날짜 조회

        if (cursor.moveToNext()) { //조회한 행이 끝날 때까지 실행하는 역할 - 리뷰가 존재하면 값 띄워주기
            isReviewExit = true;
            reviewEt.setText(cursor.getString(0));
            achievementRb.setRating(cursor.getFloat(1));
        }

        cursor.close();
        sqlDB.close();
        myHelper.close();
    }

    //학습 후기 입력
    public void insertReview(){
        sqlDB = myHelper.getWritableDatabase();

        sqlDB.execSQL("INSERT INTO studyRating (studyReview, stdRating, dateCode) VALUES(?, ?, ?)",
                new String[]{stdReview, achievemnt, Integer.toString(dateCode)});


        sqlDB.close();
        myHelper.close();
    }

    //학습 후기 업데이트
    public void updateReview(){
        sqlDB = myHelper.getWritableDatabase();

        sqlDB.execSQL("UPDATE studyRating SET studyReview = '"+stdReview+"', stdRating = '"+achievemnt+"' WHERE dateCode = '"+dateCode+"';");

        sqlDB.close();
        myHelper.close();
    }

    //학습 시간 입력
    public void insertTime(){
        sqlDB = myHelper.getWritableDatabase();

        sqlDB.execSQL("INSERT INTO studyTime (stdTime, goalTime, dateCode) VALUES(?, ?, ?)",
                new String[]{stdTime, "0시간 0분", Integer.toString(dateCode)});

        sqlDB.close();
        myHelper.close();

    }

    //학습 시간 업데이트
    public void updateTime(){
        sqlDB = myHelper.getWritableDatabase();

        sqlDB.execSQL("UPDATE studyTime SET stdTime = '"+stdTime+"' WHERE dateCode = '"+dateCode+"';");

        sqlDB.close();
        myHelper.close();

    }

}