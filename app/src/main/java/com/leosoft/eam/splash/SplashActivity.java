package com.leosoft.eam.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.leosoft.eam.login.LoginActivity;
import com.leosoft.eam.R;

public class SplashActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent loginIntent = new Intent(SplashActivity.this,LoginActivity.class);
                SplashActivity.this.startActivity(loginIntent);
                SplashActivity.this.finish();
            }
        },SPLASH_DISPLAY_LENGHT);
    }
}
