package com.example.schoolview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.schoolview.DataBaseHelp.personalinfo_db_query;
import com.example.schoolview.ImageUtil.BitmapRectTORound;
import com.example.schoolview.ImageUtil.ChangeAvatarSize;
import com.example.schoolview.ImageUtil.Util;

import com.example.schoolview.InternetUtil.NetUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by 子寒 on 2015/10/16.
 */
public class QQ_login {

    private ImageView userlogo;
    private Button login_bt;
    private BitmapRectTORound bitmapRectTORound;
    private String openId;


    private static Tencent mTencent;
    private Context mcontext;
    private personalinfo_db_query db_query=new personalinfo_db_query();






    private UserInfo mInfo;

    public static String mAppid;

    public QQ_login(Context context,Activity activity ,Button login_bt,ImageView userlogo){
        this.mcontext=context;
        this.login_bt=login_bt;
        this.userlogo=userlogo;

        mAppid ="222222";
        //第一个参数就是上面所说的申请的APPID，第二个是全局的Context上下文，这句话实现了调用QQ登录
        mTencent = Tencent.createInstance(mAppid, context);
        mTencent.login(activity,"all",loginListener);


    }

    public boolean is_user_exist(String openId){
        NetUtil netUtil=new NetUtil(mcontext);
        JSONArray jsonArray=netUtil.getJsonArray("http://121.40.224.83:8080/JnPlant/api/user");

        try {
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has("openId"))
                    if (jsonObject .getString("openId").equals(openId)) {
                        return true;
                    }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void postUser_info(String openId,String name,String imgUrl) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://121.40.224.83:8080/JnPlant/api/user");
        httpPost.addHeader("Content-Type", "application/json");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imgUrl", imgUrl);
            jsonObject.put("name", name);
            jsonObject.put("openId", openId);
            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");

            entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(entity);






        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            HttpResponse responString = httpClient.execute(httpPost);
            if(responString.getStatusLine().getStatusCode()!=200){
                Toast.makeText(mcontext,"网络连接超时",Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateUserInfo() {
        if (mTencent != null && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {

                }

                @Override
                public void onComplete(final Object response) {
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                    new Thread() {

                        @Override
                        public void run() {
                            JSONObject json = (JSONObject) response;
                            if (json.has("figureurl")) {
                                Bitmap bitmap = null;
                                try {

                                    bitmap = Util.getbitmap(json.getString("figureurl_qq_2"), "QQ_avatar");
                                } catch (JSONException e) {

                                }

                                try {
                                    if(!is_user_exist(openId)) {
                                        postUser_info(openId, json.getString("nickname"), json.getString("figureurl_qq_2"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Message msg = new Message();
                                msg.obj = bitmap;
                                msg.what = 1;
                                mHandler.sendMessage(msg);
                            }
                        }

                    }.start();
                }

                @Override
                public void onCancel() {

                }
            };
            mInfo = new UserInfo(mcontext, mTencent.getQQToken());
            mInfo.getUserInfo(listener);


        }
    }





    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                JSONObject response = (JSONObject) msg.obj;

                if (response.has("nickname")) {

                   try {
                       db_query.writeNameToDb(mcontext, response.getString("nickname"));
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                }
            }else if(msg.what == 1){
                bitmapRectTORound=new BitmapRectTORound();
                ChangeAvatarSize change=new ChangeAvatarSize();

                Bitmap bitmap = (Bitmap)msg.obj;
                Bitmap newbmp=change.changeAvatarsize(bitmap,1);
                Bitmap temp=bitmapRectTORound.getRoundedCornerBitmap(newbmp);
                db_query.writeAvatarToDb(mcontext,temp,2);

                Bitmap newbitmap=change.changeAvatarsize(bitmap,0);

                Bitmap roundbitmap=bitmapRectTORound.getRoundedCornerBitmap(newbitmap);
                userlogo.setImageBitmap(roundbitmap);
                db_query.writeAvatarToDb(mcontext,roundbitmap,1);

                userlogo.setVisibility(View.VISIBLE);
            }
        }

    };


    public Tencent getmTencent(){
        return mTencent;
    }

    public IUiListener getLoginListener(){
        return loginListener;
    }



    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);

            }
        } catch(Exception e) {
        }
    }

    public IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {

            try {
                openId=values.getString("openid");
                db_query.writeOpenIdToDb(mcontext,openId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            initOpenidAndToken(values);
            updateUserInfo();
        }
    };

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {

                Toast.makeText(mcontext, "登录失败", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Toast.makeText(mcontext,"登录失败",Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(mcontext,"登录成功",Toast.LENGTH_SHORT).show();
            login_bt.setVisibility(View.GONE);
            doComplete((JSONObject)response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {

        }

        @Override
        public void onCancel() {

        }
    }

}
