package com.example.schoolview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.ImageView;
import android.widget.Toast;

import com.example.schoolview.DataBaseHelp.personalinfo_db_query;

/**
 * Created by 子寒 on 2015/10/30.
 */
public class view_web_activity extends Activity implements View.OnClickListener{
    private WebView webView;
    private ImageView back_image;
    private String openId="";

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_web_layout);
        webView=(WebView)findViewById(R.id.pretyview_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String url){
                view.loadUrl(url);
                return  true;
            }
        });
        Intent intent=getIntent();
        String wed_id=intent.getStringExtra("website");
        if(personalinfo_db_query.IsAvatarEmpty(this)){
           openId=personalinfo_db_query.getOpenId(this);
        }

        webView.loadUrl(wed_id+"&openId="+openId);
        webView.addJavascriptInterface(new JavascriptInterface(),"android");
        webView.setSaveEnabled(false);

        back_image=(ImageView)findViewById(R.id.back_icon);
        back_image.setOnClickListener(this);
    }

    final class JavascriptInterface{
        @android.webkit.JavascriptInterface
        public void back(){
            finish();
        }

        @android.webkit.JavascriptInterface
        public void webToast(final String tip){
            Toast.makeText(view_web_activity.this,tip,Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_icon:
                finish();
                break;
        }
    }
}
