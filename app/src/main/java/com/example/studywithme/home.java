package com.example.studywithme;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class home extends Fragment {

    dialog_plusList dlg_plusList = new dialog_plusList();;
    dialog_deleteList dlg_deleteList = new dialog_deleteList();

    MyDBHelper myHelper;
    SQLiteDatabase sqlDB;

    TextView todayTv;
    Button finishBtn;
    ImageButton addPlanBtn;
    ListView listView;

    String subName, subPlan;
    int subCheck;

    int mYear, mMonth, mDate;
    int dateCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        myHelper = new MyDBHelper(getContext());

        listView = view.findViewById(R.id.listView);
        todayTv = view.findViewById(R.id.tV_Today);

        //값 전달을 위해 타겟 프래그먼트와 리퀘스트 코드 정해주기
        this.setTargetFragment(dlg_deleteList, 1);

        //오늘 날짜 가져오기
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA);
        String today = sdf.format(date);

        //오늘 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH)+1;
        mDate = calendar.get(Calendar.DAY_OF_MONTH);

        todayTv.setText(today);

        //날짜 코드가 있으면 리스트 띄워주기
        if(getDateCode()){
            displayList();
        }
        else{   //데이트 코드가 없으면 오늘 날짜 데이트 db에 넣어주고 날짜 코드 받아와서 리스트 띄워주기
            insertDate();
            getDateCode();
            displayList();
        }

        //길게 클릭시 삭제 - 다이얼로그 띄워주기
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ListViewAdapterData item = (ListViewAdapterData)parent.getItemAtPosition(position);

                subName = item.getSubName();
                subPlan = item.getSubPlan();
                subCheck = item.getSubCheck();

                dlg_deleteList.show(getChildFragmentManager(), "deletePlan");

                sendResult(1);

                return false;
            }
        });


        //할 일 추가 - 다이얼로그 띄워주기
        addPlanBtn = view.findViewById(R.id.btn_addPlan);

        addPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dlg_plusList.show(getChildFragmentManager(), "addPlan");

            }
        });



        //학습 종료
        finishBtn = view.findViewById(R.id.finishBtn);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //리스트 아이템 갯수를 받아오기
                int itemsCount = listView.getChildCount();

                //아이템 갯수 - 즉 포지션에 따라서 아이템 가져오기
                for (int i = 0; i < itemsCount; i++) {
                    View view = listView.getChildAt(i);
                    String subName = ((TextView) view.findViewById(R.id.tV_subName)).getText().toString();
                    String subPlan = ((TextView) view.findViewById(R.id.tV_subPlan)).getText().toString();

                    //체크박스 값 변경시 업데이트를 해주기 위함
                    if(((CheckBox) view.findViewById(R.id.checkbox_finish)).isChecked()){
                        sqlDB = myHelper.getWritableDatabase();

                        sqlDB.execSQL("UPDATE studyPlan SET subCheck = 1 WHERE dateCode = '"+ dateCode+"' AND subName = '"+
                                subName+"' AND subPlan = '"+subPlan+"'; ");

                        sqlDB.close();
                        myHelper.close();

                    }
                    else{
                        sqlDB = myHelper.getWritableDatabase();

                        sqlDB.execSQL("UPDATE studyPlan SET subCheck = 0 WHERE dateCode = '"+ dateCode+"' AND subName = '"+
                                subName+"' AND subPlan = '"+subPlan+"'; ");

                        sqlDB.close();
                        myHelper.close();
                    }
                }

                dialog_finishStudy dlg = new dialog_finishStudy();
                dlg.show(getChildFragmentManager(), "finish");
            }
        });

        return view;
    }


    //데이트 코드 얻어오기
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
                new String[]{Integer.toString(mYear), Integer.toString(mMonth), Integer.toString(mDate)});

        sqlDB.close();
        myHelper.close();
    }


    //데이트 코드로 해당 날짜의 일정 얻어와서 리스트에 띄워주기
    public void displayList(){
        sqlDB = myHelper.getReadableDatabase();

        Cursor cursor = sqlDB.rawQuery("SELECT subName, subPlan, subCheck FROM studyPlan " +
                "WHERE dateCode = '"+dateCode+"';", null);

        ListViewAdapter adapter = new ListViewAdapter(getContext(), "홈");

        while (cursor.moveToNext()){
            adapter.addItemToList(cursor.getString(0), cursor.getString(1), cursor.getInt(2));
        }

        listView.setAdapter(adapter);

        cursor.close();
        sqlDB.close();
        myHelper.close();
    }


    //데이터 전달하기
    private void sendResult(int REQUEST_CODE) {
        if(REQUEST_CODE == 1){
            Intent intent = new Intent();
            intent.putExtra("subName", subName);
            intent.putExtra("subPlan", subPlan);
            intent.putExtra("dateCode", dateCode);

            getTargetFragment().onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
        }
    }


}