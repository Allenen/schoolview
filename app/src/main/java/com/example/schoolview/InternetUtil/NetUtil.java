package com.example.schoolview.InternetUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 子寒 on 2015/11/12.
 */
public class NetUtil {

    private Context context;

    public NetUtil(Context context){
        this.context=context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null){
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
        }
        return false;
    }

    public JSONArray getJsonArray(String Url){
        String json=null;
        JSONArray jsonArray=null;
        try {
            URL myUrl=new URL(Url);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int len = 0;
            HttpURLConnection conn = (HttpURLConnection) myUrl .openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(2000);// 设置连接主机超时
            conn.setReadTimeout(2000);// 设置从主机读取数据超时
            conn.connect();
            InputStream is = conn.getInputStream();
            while ((len = is.read(data)) != -1) {
                outStream.write(data, 0, len);
            }
            is.close();
            json=new String(outStream.toByteArray());
            jsonArray=new JSONArray(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

   /* public int getResponse(){

        try {
            HttpClient httpClient=new DefaultHttpClient();
            HttpGet httpGet=new HttpGet("clients3.google.com/generate_204");
            HttpResponse httpResponse=httpClient.execute(httpGet);
            if (httpResponse)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
