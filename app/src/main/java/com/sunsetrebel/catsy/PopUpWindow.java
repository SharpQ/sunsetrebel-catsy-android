package com.sunsetrebel.catsy;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import  com.sunsetrebel.MapsActivity;
import androidx.appcompat.app.AppCompatActivity;

public class PopUpWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_window);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width =dm.widthPixels;
        int hight=dm.heightPixels;
        //  getWindow().setLayout((int)(width*.7),(int)(hight*.5));
        getWindow().setLayout((int)(width*.8),(int)(hight*.4));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        // params.gravity = Gravity.BOTTOM;
        params.x=10;
        params.y=12;
        getWindow().setAttributes(params);

    }

}