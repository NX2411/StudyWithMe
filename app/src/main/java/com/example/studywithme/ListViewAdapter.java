package com.example.studywithme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    ArrayList<ListViewAdapterData> list = new ArrayList<ListViewAdapterData>();

    Context context;
    String check;   //어느 화면에서 리스트를 생성하는지 확인 하기 위함

    public ListViewAdapter(Context _context, String _check){
        this.context = _context;
        check = _check;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = View.inflate(context, R.layout.custom_listviewitem, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(900, 200);
        convertView.setLayoutParams(params);

        TextView tvSubName = convertView.findViewById(R.id.tV_subName);
        TextView tvSubPlan = convertView.findViewById(R.id.tV_subPlan);
        final CheckBox chkSubCheck = convertView.findViewById(R.id.checkbox_finish);

        ListViewAdapterData listData = list.get(position);

        tvSubName.setText(listData.getSubName());
        tvSubPlan.setText(listData.getSubPlan());

        //리스트를 생성하는 화면에 따라서 check박스 클릭 가능 불가능 나눠주기
        if(check.equals("지난 일정")){
            chkSubCheck.setClickable(false);
            if(listData.getSubCheck() == 0){
                chkSubCheck.setChecked(false);
            }
            else{
                chkSubCheck.setChecked(true);
            }
        }
        else if(check.equals("홈")){
            if(listData.getSubCheck() == 0){
                chkSubCheck.setChecked(false);
            }
            else{
                chkSubCheck.setChecked(true);
            }
        }

        return convertView;
    }

    //ArrayList로 선언된 list 변수에 목록을 채워주기 위함
    public void addItemToList(String subName, String subPlan, int subCheck){
        ListViewAdapterData listdata = new ListViewAdapterData();

        listdata.setSubName(subName);
        listdata.setSubPlan(subPlan);
        listdata.setSubCheck(subCheck);

        list.add(listdata);
    }
}
