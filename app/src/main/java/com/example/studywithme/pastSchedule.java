package com.example.studywithme;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class pastSchedule extends Fragment {

    DatePickerDialog dialog;
    MyDBHelper myHelper;    //Helper 클래스 상속 받아 만든 클래스
    SQLiteDatabase sqlDB;   //Helper를 통해 생성된 DB를 받을 객체 - DB 객체

    Calendar calendar = Calendar.getInstance();
    RatingBar ratingBar;
    ListView listView;
    TextView yesterdayTv, reviewTv, timeTv, noneDataTv;
    ImageButton selectDateBtn;
    int mYear, mMonth, mDate;
    int selectYear, selectMonth, selectDate;
    int dateCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_schedule, container, false);

        //데이터베이스 가져오기
        myHelper = new MyDBHelper(getContext());

        yesterdayTv = view.findViewById(R.id.tV_yesterday);
        reviewTv = view.findViewById(R.id.tV_reviewLast);
        ratingBar = view.findViewById(R.id.rating_review);
        timeTv = view.findViewById(R.id.tV_Time);
        noneDataTv = view.findViewById(R.id.tV_noneData);

        //기본 어제 날짜로 설정해주기
        Date dDate = new Date();
        dDate = new Date(dDate.getTime()+(1000*60*60*24*-1));
        SimpleDateFormat dSdf = new SimpleDateFormat("yyyy년 M월 d일 ", Locale.KOREA);
        String yesterday = dSdf.format(dDate);

        //년도가져오기
        SimpleDateFormat yearf = new SimpleDateFormat("yyyy", Locale.KOREA);
        String yesterdayYear = yearf.format(dDate);

        //월가져오기
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.KOREA);
        String yesterdayMonth = dateFormat.format(dDate);

        //일가져오기
        SimpleDateFormat datef = new SimpleDateFormat("dd", Locale.KOREA);
        String yesterdayDate = datef.format(dDate);

        Log.d("어제 날짜", yesterdayDate);

        //어제날짜로 기본 설정되도록 변수 다르게 지정
        mYear = Integer.parseInt(yesterdayYear);
        mMonth = Integer.parseInt(yesterdayMonth);
        mDate = Integer.parseInt(yesterdayDate);

        //어제 날짜로 기본 설정되도록 화면에 설정하도록함
        selectYear = Integer.parseInt(yesterdayYear);
        selectMonth =  Integer.parseInt(yesterdayMonth);
        selectDate = Integer.parseInt(yesterdayDate);

        yesterdayTv.setText(yesterday);

        listView = view.findViewById(R.id.listView);

        //데이트 피커 열어주기
        selectDateBtn = view.findViewById(R.id.btn_cal);
        selectDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new DatePickerDialog(getContext(), listener, mYear, mMonth, mDate);
                dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());  //오늘 이후 날짜 선택 못하도록
                dialog.show();

            }
        });

        //데이트 코드 가져와서 작업하기
        getDateCode();

        return view;
    }

    //데이트 피커 선택 - 현재 선택한 날짜 가져오기
    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            selectYear = year;
            selectMonth = (monthOfYear+1);
            selectDate = dayOfMonth;

            yesterdayTv.setText(selectYear + "년 " + selectMonth + "월 " + selectDate+ "일");


            getDateCode();

        }
    };

    //데이트 코드 가져오기
    public void getDateCode(){
        sqlDB = myHelper.getWritableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT dateCode FROM studyDate WHERE year = '" + selectYear+ "' AND month = '" +
                                                selectMonth + "' AND date = '" + selectDate + "';", null); //날짜 조회

        if (cursor.moveToNext()) { //조회한 행이 끝날 때까지 실행하는 역할
            dateCode = cursor.getInt(0);
            noneDataTv.setVisibility(View.INVISIBLE);
            displayList();      //학습 일정 리스트 갱신
            displayReview();    //학습 후기 가져오기
            displayTime();      //학습 시간 가져오기
        }
        else{   //학습 일정이 없을때
            Toast.makeText(getContext(), "해당 날짜에 생성된 학습 일정이 없습니다!", Toast.LENGTH_SHORT).show();
            ListViewAdapter adapter = new ListViewAdapter(getContext(), "지난 일정");
            noneDataTv.setVisibility(View.VISIBLE);
            listView.setAdapter(adapter);
            timeTv.setText("0시간 0분");
            reviewTv.setText("리뷰가 존재하지 않습니다.");
            ratingBar.setRating(0);
        }

        cursor.close();
        sqlDB.close();
        myHelper.close();
    }

    //학습 일정 가져오기
    public void displayList(){
        boolean isPlanExit = false;     //기존 학습 일정이 있는지 없는지 검사해주는 변수
        sqlDB = myHelper.getReadableDatabase();

        Cursor cursor = sqlDB.rawQuery("SELECT subName, subPlan, subCheck FROM studyPlan " +
                "WHERE dateCode = '"+dateCode+"';", null);

        ListViewAdapter adapter = new ListViewAdapter(getContext(), "지난 일정");

        while (cursor.moveToNext()){
            adapter.addItemToList(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
            isPlanExit = true;
        }

        //학습 일정이 없으면 일정이 없다고 표시해주기
        if(!isPlanExit){
            noneDataTv.setVisibility(View.VISIBLE);
        }

        listView.setAdapter(adapter);

        cursor.close();
        sqlDB.close();
        myHelper.close();
    }

    //해당 날짜의 리뷰 가져오기
    public void displayReview(){
        sqlDB = myHelper.getReadableDatabase();

        Cursor cursor = sqlDB.rawQuery("SELECT studyReview, stdRating FROM studyRating " +
                "WHERE dateCode = '"+dateCode+"';", null);

        if(cursor.moveToNext()){
            reviewTv.setText(cursor.getString(0));
            ratingBar.setRating(cursor.getFloat(1));
        }
        else{
            reviewTv.setText("리뷰가 존재하지 않습니다.");
            ratingBar.setRating(0);
        }

        cursor.close();
        sqlDB.close();
        myHelper.close();
    }

    //해당 날짜의 학습 시간 가져오기
    public void displayTime(){
        sqlDB = myHelper.getReadableDatabase();

        Cursor cursor = sqlDB.rawQuery("SELECT stdTime FROM studyTime " +
                "WHERE dateCode = '"+dateCode+"';", null);

        if(cursor.moveToNext()){
            timeTv.setText(cursor.getString(0));
        }
        else{
            timeTv.setText("00:00");
        }

        cursor.close();
        sqlDB.close();
        myHelper.close();
    }

}