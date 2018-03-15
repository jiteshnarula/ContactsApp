package com.example.jiteshnarula.contactsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by JiteshNarula on 12-03-2018.
 */


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHelper";

    public static final String DATABASE_NAME = "phonebook";
    public static final String TABLE_NAME = "contact";
    public static final String COL1 = "ID";
    public static final String COL2 = "Name";
    public static final String COL3 = "PhoneNumber";
    public static final String COL4 = "email";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //  db.execSQL("CREATE TABLE "+ TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, newimage blob)");
        db.execSQL("create table " + TABLE_NAME + "(" + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + COL2 + " TEXT," + COL3 + " Varchar(100)," + COL4 + " varchar(100),newImage blob)");


//        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, + COL2 )";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop if table exists" + DATABASE_NAME);
        onCreate(db);

    }


}
