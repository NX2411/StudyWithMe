package com.example.studywithme;

public class ListViewAdapterData {

    private String subName;
    private String subPlan;
    private int subCheck;

    //getter setter 메소드를 이용해 값 주고 받기
    public void setSubName(String name){subName = name;}
    public void setSubPlan(String plan){subPlan = plan;}
    public void setSubCheck(int check){subCheck = check;}

    public String getSubName(){return subName;}
    public String getSubPlan(){return subPlan;}
    public int getSubCheck(){return subCheck;}

}
