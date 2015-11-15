package com.example.schoolview;

import android.graphics.Bitmap;

/**
 * Created by 子寒 on 2015/9/27.
 */
public class pretty_view {
    private String name;
    private Bitmap image;
    public pretty_view(String name,Bitmap image){
        this.name=name;
        this.image=image;
    }
    public String getName(){
        return name;
    }
    public Bitmap getImage(){
        return image;
    }
}
