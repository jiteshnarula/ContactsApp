package com.example.jiteshnarula.contactsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by JiteshNarula on 12-03-2018.
 */

public class MyDatabase {
    Context context;
    DatabaseHelper databaseHelper;

    public static final String COL1 = "ID";
    public static final String COL2 = "Name";
    public static final String COL3 = "PhoneNumber";
    public static final String COL4 = "email";

    public static final String TABLE_NAME = "contact";

    public MyDatabase(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(this.context);
    }

    public boolean addContact(String name, String mobile, String email, byte[] image) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL2, name);
        values.put(COL3, mobile);
        values.put(COL4, email);
        values.put("newImage", image);
        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public Cursor getData() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String query = "Select * from " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    //    public Cursor getItemId(String name){
//        SQLiteDatabase db = databaseHelper.getWritableDatabase();
//        String query ="select * from "+TABLE_NAME+" where  "+COL2+" = '"+name+"'   ;";
//        Cursor data = db.rawQuery(query,null);
//        return data;
//    }
    public Cursor getItemId(int id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String query = "select * from " + TABLE_NAME + " where  " + COL1 + " = " + id + " ORDER BY " + COL2 + " ;";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void updateData(int id, String newName, String newPhoneNumber, String newEmail, byte[] image) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        // String query = "update "+TABLE_NAME+" set "+COL2+" = '"+newName+"', "+COL3+" = '"+newPhoneNumber+"' , "+COL4+" = '"+newEmail+"', newImage ="+image+"  where "+COL1+" = "+id+"";


        ContentValues cv = new ContentValues();
        cv.put(COL2, newName);
        cv.put(COL3, newPhoneNumber);
        cv.put(COL4, newEmail);
        cv.put("newImage", image);
        db.update(TABLE_NAME, cv, COL1 + " = " + id, null);


        //  Log.e("update quey",query);
        Log.e("Setting name to ", newName);
        Log.e("setting pno", newPhoneNumber);
        Log.e("Setting email to ", newEmail);
        Log.e("Setting email to ", String.valueOf(image));


    }

    public void deleteName(int id, String name, String phone, String email) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String query = "delete from " + TABLE_NAME + " where " + COL1 + " = " + id + "";

        Log.e("Delete name : query", query);
        Log.e("Deleting ", name + "from the query");
        db.execSQL(query);
    }
}
