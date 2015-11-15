package com.example.schoolview.DataBaseHelp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by 子寒 on 2015/10/16.
 */
public class prettyview_db_query {
    private static int VERSION=1;
    private static String DATABASE_NAME = "prettyview.db";
    private  static  final String CREATE_TABLE_VIEW="create table prettyview_info (" +
            "ID integer primary key autoincrement," +
            "_ID text," +
            "title text," +
            "PICTURE BLOB)";

    public static SQLiteDatabase getDatabase(Context context){
        /*SQLiteOpenHelper databaseHelper = new Db_Helper(context, DATABASE_NAME, null, VERSION);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        return database;*/
        //数据库存储路径
        String filePath="data/data/com.example.schoolview/prettyview.db";
        //数据库存放的文件
        String pathStr ="data/data/com.example.schoolview";

        File dbfile=new File(filePath);
        
        if(dbfile.exists()){
            return SQLiteDatabase.openOrCreateDatabase(dbfile, null);
        }
        else{
            File path=new File(pathStr);
            if (path.mkdir()){
                Log.i("test","创建成功。");
            }
            

            try {
                InputStream is=context.getClass().getClassLoader().getResourceAsStream("assets/prettyview.db");
                FileOutputStream fos=new FileOutputStream(dbfile);
                byte[] buffer=new byte[1024];
                int count = 0;
                while((count = is.read(buffer))>0){

                    fos.write(buffer,0,count);
                }
                fos.flush();
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
        return getDatabase(context);
    }

    public static int isDbNull(SQLiteDatabase database){
        Cursor cursor=database.rawQuery("select count(*) from prettyview_info",null);
        cursor.moveToNext();
        int count=cursor.getInt(0);
        cursor.close();
        return count;
    }

    public static int table_size(Context context){
        SQLiteDatabase database = getDatabase(context);
        Cursor cursor=database.rawQuery("select count(*) from prettyview_info",null);
        cursor.moveToNext();
        int count=cursor.getInt(0);
        cursor.close();
        return count;
    }



    public static void clear_table(Context context){
        SQLiteDatabase database = getDatabase(context);
        database.execSQL("delete from prettyview_info");
    }

    public static String getvalue(Cursor cursor,String columnname){
        return cursor.getString(cursor.getColumnIndex(columnname));
    }

    public static void WriteViewToDb(Context context,int ID,String _ID,String title,Bitmap bitmap){
        SQLiteDatabase database = getDatabase(context);
        ContentValues values=new ContentValues();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        values.put("ID",ID);
        values.put("_ID",_ID);
        values.put("title",title);
        values.put("PICTURE",os.toByteArray());
        database.insert("prettyview_info",null,values);
        database.close();
    }



    public static String get_ID(Context context,int i){
        SQLiteDatabase database = getDatabase(context);
        String _ID="";
        Cursor cursor=database.rawQuery("select _ID from prettyview_info where ID="+i,null);//.query("prettyview_info",new String[]{"nickname"},null,null,null,null,null);
        if(cursor.moveToNext()){
            _ID=cursor.getString(cursor.getColumnIndex("_ID"));
        }
        cursor.close();
        database.close();
        return _ID;
    }
    public static String getTitle(Context context,int i){
        SQLiteDatabase database = getDatabase(context);
        String title="";
        Cursor cursor=database.rawQuery("select title from prettyview_info where ID="+i,null);//.query("prettyview_info",new String[]{"nickname"},null,null,null,null,null);
        if(cursor.moveToNext()){
            title=cursor.getString(cursor.getColumnIndex("title"));
        }
        cursor.close();
        database.close();
        return title;
    }

    public static Bitmap getPicture(Context context,int i){
        SQLiteDatabase database = getDatabase(context);
        Cursor cursor;
        byte[] imagequery = null;

        cursor = database.rawQuery("select PICTURE from prettyview_info where ID="+i, null);
        if (cursor.moveToNext()) {
            //将Blob数据转化为字节数组
            imagequery = cursor.getBlob(cursor.getColumnIndex("PICTURE"));
        }
        //将字节数组转化为位图
        Bitmap imagebitmap= BitmapFactory.decodeByteArray(imagequery, 0, imagequery.length);
        cursor.close();
        database.close();
        return imagebitmap;
    }


}
