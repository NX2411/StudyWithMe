package com.example.studywithme;

import android.content.Intent;
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

public class dialog_deleteList extends DialogFragment {

    MyDBHelper myHelper;
    SQLiteDatabase sqlDB;

    Button cancelBtn, saveBtn;

    String subName, subPlan;
    int dateCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_delete_list, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myHelper = new MyDBHelper(getContext());

        cancelBtn = view.findViewById(R.id.btn_cancel);
        saveBtn = view.findViewById(R.id.btn_save);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //데이터 삭제
                deleteData();

                //데이터 리스트에 바로 갱신
                ((home)getParentFragment()).displayList();

                dismiss();
            }
        });

        return view;
    }

    //데이터 전달받기
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            subName = data.getStringExtra("subName");
            subPlan = data.getStringExtra("subPlan");
            dateCode = data.getIntExtra("dateCode", 0);

        }
    }

    //전달 받은 데이터 값으로 값 검색해서 삭제
    public void deleteData(){
        sqlDB = myHelper.getWritableDatabase();
        sqlDB.execSQL("DELETE FROM studyPlan WHERE dateCode = '"+dateCode+"' AND subName = '"+subName+"' AND subPlan = '"+subPlan+"';");

        sqlDB.close();
        myHelper.close();
    }
}