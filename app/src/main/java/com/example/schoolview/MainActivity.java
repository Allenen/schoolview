package com.example.schoolview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baoyz.widget.PullRefreshLayout;
import com.example.schoolview.DataBaseHelp.personalinfo_db_query;
import com.example.schoolview.DataBaseHelp.prettyview_db_query;
import com.example.schoolview.InternetUtil.NetUtil;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements View.OnClickListener,TextWatcher,PopupWindow.OnDismissListener{

    private Button bt_login;
    private String login_bt_flag;


    private TextView search_textview;


   //四个功能布局
    private View nearby_layout;
    private View camera_layout;
    private View plantcollect_layout;
    private View viewcollect_layout;

    private List<pretty_view> pviewlist=new ArrayList<pretty_view>();
    private  view_adapter adapter;

    //hide and show
    private RelativeLayout mainLayout;
    private RelativeLayout titlelayout;
    private int moveHeight;
    private int statusBarHeight;

    //search popupWindow
    private PopupWindow popupWindow;
    private View search_view;
    private EditText searchEditText;
    private TextView cancelTextView;
    private View alphaView;
    private ListView filterListView;

    //QQ头像
    private QQ_login qq_login;
    private Tencent mTencent;
    private IUiListener loginListener;

    private ImageView userlogo;
    private String userlog_flag;

    //单个美景页面网页Id
    private String web_id[]=new String[8];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userlog_flag="0";
        login_bt_flag="1";

        //初始化控件
        initwidget();
        initQQ(savedInstanceState);
    }



    public void initQQ(Bundle savedInstanceState){
        //用来显示头像的Imageview
        userlogo=(ImageView)findViewById(R.id.nick_icon);
        userlogo.setOnClickListener(this);



        if(personalinfo_db_query.IsAvatarEmpty(this)){
            bt_login.setVisibility(View.GONE);
            login_bt_flag="0";
            userlogo.setVisibility(View.VISIBLE);
            userlog_flag="1";
            Bitmap avatar=personalinfo_db_query.getAvatar(this,1);
            userlogo.setImageBitmap(avatar);
        }


    }

    public void initwidget(){
        bt_login=(Button)findViewById(R.id.login_bt);
        bt_login.setOnClickListener(this);

        //ListVIew的头
        final LinearLayout hearderViewLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.search_function_layout,null);
        search_textview=(TextView)hearderViewLayout.findViewById(R.id.search_textview);
        search_textview.setOnClickListener(this);



        //主布局和标题布局
        mainLayout=(RelativeLayout)findViewById(R.id.main);
        titlelayout=(RelativeLayout)findViewById(R.id.include_title);

        //弹出的搜索框
        LayoutInflater popupwindowInflater=LayoutInflater.from(this);
        search_view=popupwindowInflater.inflate(R.layout.popupwindow_layout,null);
        searchEditText=(EditText)search_view.findViewById(R.id.pop_search_edittext);
        searchEditText.setFocusable(true);
        searchEditText.addTextChangedListener(this);
        cancelTextView=(TextView)search_view.findViewById(R.id.popupwindow_cancel);
        cancelTextView.setOnClickListener(this);

        filterListView = (ListView) search_view.findViewById(R.id.popup_window_lv);
        filterListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14" }));
        //  filterListView.setOnItemClickListener(this);
        alphaView=search_view.findViewById(R.id.popup_window_v_alpha);
        alphaView.setOnClickListener(this);

        popupWindow=new PopupWindow(search_view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(this);


       //四个功能布局
        nearby_layout=hearderViewLayout.findViewById(R.id.nearby_layout);
        camera_layout=hearderViewLayout.findViewById(R.id.camera_layout);
        plantcollect_layout=hearderViewLayout.findViewById(R.id.plantcollect_layout);
        viewcollect_layout=hearderViewLayout.findViewById(R.id.viewcollect_layout);

        nearby_layout.setOnClickListener(this);
        camera_layout.setOnClickListener(this);
        plantcollect_layout.setOnClickListener(this);
        viewcollect_layout.setOnClickListener(this);

        adapter=new view_adapter(MainActivity.this,R.layout.view_item,pviewlist);
        init_prettyview();

        ListView listView=(ListView)findViewById(R.id.schoolview_listview);
        listView.addHeaderView(hearderViewLayout,null,false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pretty_view prettyView=pviewlist.get(position-1);
                Intent intent=new Intent(MainActivity.this,view_web_activity.class);
                intent.putExtra("website","http://121.40.224.83:8080/scene/?sceneId="+web_id[position-1]);

                startActivity(intent);
            }
        });

        final  PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        // listen refresh event
         layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                NetUtil netUtil=new NetUtil(MainActivity.this);
                if(netUtil.isConnectingToInternet()) {
                    prettyview_db_query dbQuery = new prettyview_db_query();
                    dbQuery.clear_table(MainActivity.this);
                    pviewlist.clear();
                    prettyview_list prettyviewList = new prettyview_list(MainActivity.this, pviewlist, adapter, web_id);
                    prettyviewList.getPrettyview();
                }
                else{
                    Toast.makeText(MainActivity.this,"请检查网络设置",Toast.LENGTH_SHORT).show();
                }
                layout.setRefreshing(false);

            }
        });

        layout.setRefreshing(false);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_bt:
                qq_login=new QQ_login(this,this,bt_login,userlogo);
                mTencent=qq_login.getmTencent();
                loginListener=qq_login.getLoginListener();
                break;
            case R.id.nick_icon:
                Intent intent=new Intent(MainActivity.this,person_info_activity.class);

                startActivityForResult(intent, 2);
                break;
            case R.id.search_textview:
                showSearchBar(v);
                break;
            case R.id.popupwindow_cancel:
                dismissPopupWindow();
                break;
            case R.id.popup_window_v_alpha:
                dismissPopupWindow();
                break;
            case R.id.nearby_layout:
                Toast.makeText(MainActivity.this,"即将进入附近植物",Toast.LENGTH_SHORT).show();
                break;
            case R.id.camera_layout:
                Toast.makeText(MainActivity.this,"即将进入拍照识别",Toast.LENGTH_SHORT).show();
                break;
            case R.id.plantcollect_layout:
                Toast.makeText(MainActivity.this,"即将进入植物收藏",Toast.LENGTH_SHORT).show();
                break;
            case R.id.viewcollect_layout:
                Intent intent1=new Intent(MainActivity.this,viewcollect_activity.class);
                startActivity(intent1);
                break;
        }
    }

    private void init_prettyview(){
      /* prettyview_list prettyviewList=new prettyview_list(this,pviewlist,adapter,web_id);
        prettyviewList.getPrettyview();*/


        prettyview_db_query dbQuery=new prettyview_db_query();
        int size=dbQuery.table_size(this);
        for(int i=0;i<size;i++){

            web_id[i]=dbQuery.get_ID(this,i+1);

            pretty_view prettyView=new pretty_view(dbQuery.getTitle(this,i+1),dbQuery.getPicture(this,i+1));
            pviewlist.add(prettyView);
        }

    }


    @Override
    public void onDismiss() {
        resetUI();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.toString().equals("")) {
            alphaView.setVisibility(View.VISIBLE);
            filterListView.setVisibility(View.GONE);
        } else {
            alphaView.setVisibility(View.GONE);
            filterListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void showSearchBar(final View v){
        getStatusBarHeight();
        moveHeight=titlelayout.getHeight();
        Animation translateAnimation=new TranslateAnimation(0,0,0,-moveHeight);
        translateAnimation.setDuration(300);
        translateAnimation.setFillAfter(true);
        mainLayout.startAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

        }

            @Override
            public void onAnimationEnd(Animation arg0) {

            //    titlelayout.setVisibility(View.GONE);
                searchEditText.setText("");
                popupWindow.showAtLocation(mainLayout, Gravity.CLIP_VERTICAL, 0, statusBarHeight);
                openKeyboard();
            }
        });

    }

    //获取状态栏的高度
    private void getStatusBarHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;
    }

    private void openKeyboard() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }


    private void dismissPopupWindow(){
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void resetUI(){
        titlelayout.setPadding(0, 0, 0, 0);
        Animation translateAnimation = new TranslateAnimation(0, 0, -moveHeight, 0);
        translateAnimation.setDuration(300);
        mainLayout.startAnimation(translateAnimation);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.RESULT_LOGIN) {
                mTencent.handleLoginData(data, loginListener);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode==2){
            if(resultCode==5){
                login_bt_flag=data.getStringExtra("login_flag");
                userlog_flag=data.getStringExtra("userlogo");
                if(Integer.parseInt(login_bt_flag)==1) {
                    bt_login.setVisibility(View.VISIBLE);
                    userlogo.setVisibility(View.GONE);
                //    mTencent.logout(this);
                }

                super.onActivityResult(requestCode, resultCode, data);
            }

        }
    }
}
