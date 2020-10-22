package com.android.adclikmi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.adclikmi.Model.user;

/**
 * Marcel 2019 *
 **/

public class SplashActivity extends AppCompatActivity {
    private Intent intent;
    private int SPLASH_TIME_OUT=650;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final SharedPreferences sp=this.getSharedPreferences("user", MODE_PRIVATE);

        new Handler().postDelayed (new Runnable() {
            @Override
            public void run() {
                if(!sp.contains("token")){
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }else{
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }
                startActivity(intent);
            }
        }, SPLASH_TIME_OUT);
    }
}
