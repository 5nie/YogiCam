package com.example.dkdk6.yogicamera.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dkdk6.yogicamera.R;

public class loadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(loadingActivity.this, CameraActivity.class);
                startActivity(intent);
                finish();
            }
        }, (3 * 1000));
    }
}
