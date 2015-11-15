package com.example.schoolview.DataBaseHelp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by 子寒 on 2015/10/16.
 */
public class Db_Helper extends SQLiteOpenHelper {



    private  static  final String CREATE_TABLE_VIEW="create table prettyview_info (" +
            "ID integer primary key," +
            "_ID text," +
            "title text," +
            "PICTURE BLOB)";

    public Db_Helper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_VIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
