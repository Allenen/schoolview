package com.example.schoolview.ImageUtil;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by 子寒 on 2015/10/12.
 */
public class ChangeAvatarSize {
    public Bitmap changeAvatarsize(Bitmap bitmap,int size){
        Bitmap temp=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(), bitmap.getHeight());
        Matrix matrix=new Matrix();
        float scaleRate;
        if(size==0) {
            scaleRate  = 0.68f;
        }
        else {
             scaleRate = 1.5f;
        }

        matrix.postScale(scaleRate,scaleRate);
        Bitmap newbitmap= Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
        return newbitmap;
    }
}
