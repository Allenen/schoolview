package com.example.schoolview.ImageUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Util {
    public static String TAG="UTIL";
    public static Bitmap getbitmap(String imageUri,String type) {

        if(type=="prettyview") {
            imageUri = encodeUrl(imageUri);
        }
        Log.v(TAG, "getbitmap:" + imageUri);
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();

            bitmap = BitmapFactory.decodeStream(is);
            is.close();

            Log.v(TAG, "image download finished." + imageUri);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getbitmap bmp fail---");
            bitmap = null;
        }
        return bitmap;
    }

    public static String encodeUrl(String imageUri){
        try {
            int index1 = imageUri.indexOf('/');
            int index2 = imageUri.indexOf('/', index1 + 1);
            int index3 = imageUri.indexOf('/', index2 + 1);

            String temp2 = imageUri.split("/")[3];
            int endIndex = temp2.indexOf(".") + index3;

            String imageName = imageUri.substring(index3 + 1, endIndex + 1);
            imageName = URLEncoder.encode(imageName, "UTF-8");

            imageUri = imageUri.substring(0, index3 + 1) + imageName + imageUri.substring(endIndex + 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return imageUri;
    }
}