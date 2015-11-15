package com.example.schoolview.DataBaseHelp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;


/**
 * Created by 子寒 on 2015/10/16.
 */
public class personalinfo_db_query {
    private static int VERSION=1;
    private static String DATABASE_NAME = "personal_info.db";

    public static SQLiteDatabase getDatabase(Context context){
        SQLiteOpenHelper databaseHelper = new DbHelper(context, DATABASE_NAME, null, VERSION);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        return database;
    }

    public static int isDbNull(SQLiteDatabase database){
        Cursor cursor=database.rawQuery("select count(*) from personal_information",null);
        cursor.moveToNext();
        int count=cursor.getInt(0);
        return count;
    }

    public static String getvalue(Cursor cursor,String columnname){
        return cursor.getString(cursor.getColumnIndex(columnname));
    }

    public static void writeNameToDb(Context context,String name){
        SQLiteDatabase database = getDatabase(context);
        ContentValues values=new ContentValues();
        values.put("nickname",name);

        database.update("personal_information", values, "id=1", null);

        database.close();
    }

    public static void writeAvatarToDb(Context context,Bitmap bitmap,int type){
        SQLiteDatabase database = getDatabase(context);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        ContentValues values=new ContentValues();

        if(type==1) {
            values.put("avatar",os.toByteArray());
            database.update("personal_information", values, "id=1", null);//.insert("personal_information","avatar",values);
        }
        else {
            values.put("avatar_big",os.toByteArray());
            database.update("personal_information", values, "id=1", null);
        }
        database.close();

    }

    public static void writeOpenIdToDb(Context context,String openid){
        SQLiteDatabase database = getDatabase(context);
        ContentValues values=new ContentValues();
        values.put("openid",openid);
        int count=isDbNull(database);
        if(count==0) {
            database.insert("personal_information", null, values);
        }
        else{
            database.update("personal_information", values, "id=1", null);
        }
        database.close();
    }

    public static String getNickname(Context context){
        SQLiteDatabase database = getDatabase(context);
        String name="";
        Cursor cursor=database.rawQuery("select nickname from personal_information where id=1",null);//.query("personal_information",new String[]{"nickname"},null,null,null,null,null);
        if(cursor.moveToNext()){
             name=cursor.getString(cursor.getColumnIndex("nickname"));

        }
        cursor.close();
        database.close();
        return name;
    }

    public static String getOpenId(Context context){
        SQLiteDatabase database = getDatabase(context);
        String openid="";
        Cursor cursor=database.rawQuery("select openid from personal_information where id=1",null);
        if(cursor.moveToNext()){
            openid=cursor.getString(cursor.getColumnIndex("openid"));

        }
        cursor.close();
        database.close();
        return openid;
    }

    public static Bitmap getAvatar(Context context,int type){
        SQLiteDatabase database = getDatabase(context);
        Cursor cursor;
        byte[] imagequery = null;
        if(type==1) {
            cursor = database.rawQuery("select avatar from personal_information where id=1", null);//.query("personal_information", new String[]{"avatar"}, null, null, null, null, null);
            if (cursor.moveToNext()) {
                //将Blob数据转化为字节数组
                imagequery = cursor.getBlob(cursor.getColumnIndex("avatar"));
            }
            //将字节数组转化为位图
            Bitmap imagebitmap= BitmapFactory.decodeByteArray(imagequery, 0, imagequery.length);
            cursor.close();
            database.close();
            return imagebitmap;
        }
        else{
            cursor = database.rawQuery("select avatar_big from personal_information where id=1", null);
            if (cursor.moveToNext()) {
                //将Blob数据转化为字节数组
                imagequery = cursor.getBlob(cursor.getColumnIndex("avatar_big"));
            }
            //将字节数组转化为位图
            Bitmap imagebitmap= BitmapFactory.decodeByteArray(imagequery, 0, imagequery.length);
            cursor.close();
            database.close();
            return imagebitmap;
        }


    }

    public static Boolean IsAvatarEmpty(Context context){
        SQLiteDatabase database = getDatabase(context);
        Cursor cursor=database.rawQuery("select count(*) from personal_information",null);
        cursor.moveToNext();
        int count=cursor.getInt(0);
       if(count==0)
           return false;
        database.close();
       return true;

    }


}
