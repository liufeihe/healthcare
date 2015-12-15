package com.example.feihe.healthcare;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by feihe on 2015/11/5.
 */
public class MyDbHelper extends SQLiteOpenHelper {

    final String CREATE_ITEM_TABLE_SQL="create table myItem(_id integer primary key autoincrement, " +
            "name text, data float, time text)";
    final String CREATE_NAME_TABLE_SQL="create table myName(_id integer primary key autoincrement, " +
            "name text)";

    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_ITEM_TABLE_SQL);
        db.execSQL(CREATE_NAME_TABLE_SQL);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion){
        System.out.println("Updating..."+oldversion+"->"+newversion);
    }
}
