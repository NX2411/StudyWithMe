package com.example.studywithme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(@Nullable Context context) {
        super(context, "studyDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //테이블 생성
        db.execSQL("CREATE TABLE studyDate ( dateCode INTEGER NOT NULL, year INTEGER NOT NULL, month INTEGER NOT NULL, date	INTEGER NOT NULL, PRIMARY KEY(dateCode));");
        db.execSQL("CREATE TABLE studyPlan ( planCode INTEGER NOT NULL, subName TEXT NOT NULL, subPlan TEXT NOT NULL, subCheck INTEGER NOT NULL, " +
                "dateCode INTEGER NOT NULL, PRIMARY KEY(planCode), FOREIGN KEY(dateCode) REFERENCES studyDate(dateCode));");
        db.execSQL("CREATE TABLE studyRating (ratingCode INTEGER NOT NULL, studyReview TEXT NOT NULL, stdRating REAL NOT NULL, " +
                "dateCode INTEGER NOT NULL, PRIMARY KEY(ratingCode), FOREIGN KEY(dateCode) REFERENCES studyDate(dateCode));");
        db.execSQL("CREATE TABLE studyTime (timeCode INTEGER NOT NULL, stdTime TEXT NOT NULL, goalTime TEXT NOT NULL, " +
                "dateCode INTEGER NOT NULL, PRIMARY KEY(timeCode), FOREIGN KEY(dateCode) REFERENCES studyDate(dateCode));");

        //지난 일정 확인을 위한 값 생성
        db.execSQL("INSERT INTO studyDate VALUES(0, 2021, 6, 13)");
        db.execSQL("INSERT INTO studyPlan VALUES(0, '테스트1', '테스트1 내용입니다.', 0, 0)");
        db.execSQL("INSERT INTO studyPlan VALUES(1, '테스트2', '테스트2 내용입니다.', 1, 0)");
        db.execSQL("INSERT INTO studyRating VALUES(0, '리뷰 테스트 입니다.', 3.0, 0)");
        db.execSQL("INSERT INTO studyTime VALUES(0, '1시간 5분', '1시간 10분', 0)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS studyDate");
        db.execSQL("DROP TABLE IF EXISTS studyPlan");
        db.execSQL("DROP TABLE IF EXISTS studyRating");
        db.execSQL("DROP TABLE IF EXISTS studyTime");
        onCreate(db);
    }
}

