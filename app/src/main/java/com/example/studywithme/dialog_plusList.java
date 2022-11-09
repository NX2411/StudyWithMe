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
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class dialog_plusList extends DialogFragment {

    MyDBHelper myHelper;
    SQLiteDatabase sqlDB;

    Button cancelBtn, saveBtn;
    EditText subNameEt, subPlanEt;
    String mYear, mMonth, mDate;
    int dateCode;

    String subName, subPlan, subcheck;

    boolean DateCodeExit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_plus_list, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myHelper = new MyDBHelper(getContext());

        //오늘 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        mYear = Integer.toString(calendar.get(Calendar.YEAR));
        mMonth = Integer.toString(calendar.get(Calendar.MONTH)+1);
        mDate = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

        subNameEt = view.findViewById(R.id.et_subName);
        subPlanEt = view.findViewById(R.id.et_subPlan);

        cancelBtn = view.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //오늘 날짜의 데이트 코드가 있는지 검사
        if(getDateCode()){
            DateCodeExit = true;
        }
        else{
            //없으면 데이트 코드 데베에 넣어주기
            insertDate();
            DateCodeExit = false;
        }



        //데베 연동 - 일정 추가
        saveBtn = view.findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subName = subNameEt.getText().toString();
                subPlan = subPlanEt.getText().toString();
                subcheck = Integer.toString(0);

                //예외처리 - 입력 값이 존재해야만 데이터 베이스에 저장 가능
                if(!subName.isEmpty() && !subPlan.isEmpty()){
                    //데이터 코드가 있는지 검사 - 있으면 일정추가
                    if(DateCodeExit){
                        insertPlan();
                    }
                    //없으면 가져와서 일정 추가
                    else{
                        getDateCode();
                        insertPlan();
                    }
                    Toast.makeText(getContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else{
                    Toast.makeText(getContext(), "작성하지 않은 부분이 있는지 확인해주세요!", Toast.LENGTH_SHORT).show();
                }
                //데이터 리스트에 바로 갱신
                ((home)getParentFragment()).displayList();

            }
        });

        return view;
    }

    //데이트 코드 가져오기
    public boolean getDateCode(){
        boolean codeExit = false;
        sqlDB = myHelper.getWritableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT dateCode FROM studyDate WHERE year = '" + mYear+ "' AND month = '" +
                mMonth + "' AND date = '" + mDate + "';", null); //날짜 조회

        if (cursor.moveToNext()) { //조회한 행이 끝날 때까지 실행하는 역할
            dateCode = cursor.getInt(0);
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

    //일정 입력
    public void insertPlan(){

        sqlDB = myHelper.getWritableDatabase();

        sqlDB.execSQL("INSERT INTO studyPlan (subName, subPlan, subCheck, dateCode) VALUES(?, ?, ?, ?)",
                new String[]{subName, subPlan, subcheck, Integer.toString(dateCode)});

        sqlDB.close();
        myHelper.close();
    }


}