package com.example.feihe.healthcare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by feihe on 2015/12/7.
 */
public class MyDB {
    public static final String DB_NAME = "healthcare_db";
    public static final int VERSION = 1;
    private static MyDB myDB;
    private SQLiteDatabase db;

    private MyDB(Context context){
        MyDbHelper myDbHelper = new MyDbHelper(context, DB_NAME, null, VERSION);
        db = myDbHelper.getWritableDatabase();
    }

    public synchronized static MyDB getInstance(Context context){
        if(myDB == null)
            myDB = new MyDB(context);
        return myDB;
    }
    public void addName(String name){
        if(name!=null){
            ContentValues values = new ContentValues();
            values.put("name", name);
            db.insert("myName", null, values);
        }
    }
    public void deleteName(String name){
            db.delete("myName", "name=?", new String[]{name});
    }
    public ArrayList<String> queryName(){
        Cursor cursor = db.rawQuery("select * from myName", null);
        ArrayList<String> result=new ArrayList<>();
        while(cursor.moveToNext()){
            result.add(cursor.getString(1));
        }
        return result;
    }

    public void addItem(DataInfo dataInfo){
        if(dataInfo!=null){
            ContentValues values = new ContentValues();
            values.put("name", dataInfo.getName());
            values.put("data", dataInfo.getData());
            values.put("time", dataInfo.getTime());
            db.insert("myItem", null, values);
        }
    }
    public void deleteItemById(int id){
        db.delete("myItem", "_id=?", new String[]{""+id});
    }
    public void deleteItemByName(String key){
        db.delete("myItem", "name=?", new String[]{key});
    }
    public void updateItemById(int id, float data){
        ContentValues values = new ContentValues();
        values.put("data", data);
        db.update("myItem", values,"_id=?", new String[]{""+id});
    }
    public ArrayList<DataInfo> queryItem(String key){
        Cursor cursor = db.rawQuery("select * from myItem where name like ? ", new String[]{key});

        ArrayList<DataInfo> result=new ArrayList<>();
        while(cursor.moveToNext()){
            DataInfo dataInfoTemp = new DataInfo(cursor.getInt(0),cursor.getString(1),
                    cursor.getFloat(2), cursor.getString(3));
            result.add(dataInfoTemp);
        }
        return result;
    }
}
