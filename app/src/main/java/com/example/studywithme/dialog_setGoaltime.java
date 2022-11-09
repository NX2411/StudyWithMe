package com.example.studywithme;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;


public class dialog_setGoaltime extends DialogFragment {

    NumberPicker hour, min;
    Button cancelBtn, saveBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_set_goaltime, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        cancelBtn = view.findViewById(R.id.btn_cancel);

        //넘버피커 최대 최소 숫자 정해주기
        hour = view.findViewById(R.id.nP_hour);
        hour.setMinValue(0);
        hour.setMaxValue(23);

        min = view.findViewById(R.id.nP_min);
        min.setMinValue(0);
        min.setMaxValue(59);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //저장 버튼 클릭 시 값 보내주기
        saveBtn = view.findViewById(R.id.btn_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(1);
                dismiss();
            }
        });

        return view;
    }

    //값 전달 메소드
    private void sendResult(int REQUEST_CODE) {
        Intent intent = new Intent();
        intent.putExtra("hour", hour.getValue());
        intent.putExtra("min", min.getValue());
        getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
    }


}