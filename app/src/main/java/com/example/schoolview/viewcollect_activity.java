package com.example.schoolview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.schoolview.DataBaseHelp.prettyview_db_query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 子寒 on 2015/11/12.
 */
public class viewcollect_activity extends Activity implements View.OnClickListener{

    private ImageView back_image;
    private List<pretty_view> pviewlist=new ArrayList<pretty_view>();
    private view_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewcollect_layout);
        back_image=(ImageView)findViewById(R.id.collect_back_icon);
        back_image.setOnClickListener(this);

        adapter=new view_adapter(viewcollect_activity.this,R.layout.view_item,pviewlist);
        init_prettyview();
        ListView listView=(ListView)findViewById(R.id.schoolview_listview);
        listView.setAdapter(adapter);
    }

    private void init_prettyview(){
      /* prettyview_list prettyviewList=new prettyview_list(this,pviewlist,adapter,web_id);
        prettyviewList.getPrettyview();*/

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.collect_back_icon :
                finish();
                break;
        }
    }
}
