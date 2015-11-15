package com.example.schoolview;

import android.content.Context;

import android.graphics.Bitmap;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.TextView;


import com.example.schoolview.ImageUtil.changeroundcorner;

import java.util.List;

/**
 * Created by 子寒 on 2015/9/27.
 */
public class view_adapter extends ArrayAdapter<pretty_view>{
    private int resouceId;
    private Context context;

    public view_adapter(Context context,int textViewResouceId,List<pretty_view> objects){
        super(context,textViewResouceId,objects);
        this.context=context;
        resouceId=textViewResouceId;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        pretty_view prettyView=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resouceId,null);
        TextView name_view=(TextView)view.findViewById(R.id.view_name);
        ImageView image_view=(ImageView)view.findViewById(R.id.prettyview_image);
        name_view.setText(prettyView.getName());
        Bitmap bitmap=prettyView.getImage();
        changeroundcorner change=new changeroundcorner();
        bitmap=change.createFramedPhoto(bitmap);
        image_view.setImageBitmap(bitmap);


        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = display.getWidth();
        int height=width*2/5;
        view.setMinimumHeight(height);


        return view;
    }
}
