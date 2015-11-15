package com.example.schoolview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;


import com.example.schoolview.DataBaseHelp.prettyview_db_query;
import com.example.schoolview.ImageUtil.Util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 子寒 on 2015/10/30.
 */
public class prettyview_list {

    private List<pretty_view> pviewlist=new ArrayList<pretty_view>();
    private view_adapter adapter;
    private JSONArray jsonArray;
    private String web_id[]=new String[5];
    private Context context;

    private prettyview_db_query dbQuery=new prettyview_db_query();

    public prettyview_list(Context context,List<pretty_view> pviewlist,view_adapter adapter,String web_id[]){
        this.pviewlist=pviewlist;
        this.adapter=adapter;
        this.web_id=web_id;
        this.context=context;
    }
    
    public void getPrettyview(){

        new Thread(){
            @Override
            public void run(){
                String json=null;

                try {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int len = 0;
                    URL myFileUrl = new URL("http://simplyy.space:8080/JnPlant/api/scene");
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl .openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    while ((len = is.read(data)) != -1) {
                        outStream.write(data, 0, len);
                    }
                    is.close();
                    json=new String(outStream.toByteArray());
                    jsonArray=new JSONArray(json);
                    for(int i=jsonArray.length()-1;i>=0;i--){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        if (jsonObject.has("_id")){
                            web_id[jsonArray.length()-i-1]=jsonObject.getString("_id");
                        }
                        if(jsonObject.has("imgUrl")&&jsonObject.has("title")){
                            Bitmap bitmap=null;
                            String viewname=null;
                            try {
                                bitmap = Util.getbitmap(jsonObject.getString("imgUrl"), "prettyview");
                                viewname=jsonObject.getString("title");
                                dbQuery.WriteViewToDb(context,jsonArray.length()-i,web_id[jsonArray.length()-i-1],viewname,bitmap);
                            } catch (JSONException e) {

                            }
                            pretty_view prettyView=new pretty_view(viewname,bitmap);
                            Message msg = new Message();
                            msg.obj = prettyView;
                            msg.what = i;
                            mHandler.sendMessage(msg);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.start();
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            for(int i=0;i<jsonArray.length();i++){
                if (msg.what == i) {
                    pretty_view prettyView=(pretty_view)msg.obj;
                    pviewlist.add(prettyView);
                     adapter.notifyDataSetChanged();
                }
            }
        }

    };
}
