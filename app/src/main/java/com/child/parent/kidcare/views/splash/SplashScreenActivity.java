package com.child.parent.kidcare.views.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.child.parent.kidcare.R;
import com.child.parent.kidcare.utils.Util;
import com.child.parent.kidcare.views.UserProfile.UserProfileActivity;
import com.child.parent.kidcare.views.home.HomeActivity;
import com.child.parent.kidcare.views.onboarding.OnboardingActivity;

import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity {

    static int TIMEOUT_MILLIS = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i ;
                SharedPreferences preferences =
                        getSharedPreferences("ON_BOARDING_PREFERENCE", MODE_PRIVATE);


                if(preferences.getBoolean("is_onboarding_completed", false)){
                    i= new Intent(SplashScreenActivity.this,
                            HomeActivity.class);
                }else{
                    i= new Intent(SplashScreenActivity.this,
                            OnboardingActivity.class);
                }
                startActivity(i);
                finish();
                overridePendingTransition(R.animator.slide_up,  R.animator.slide_down);
            }
        }, TIMEOUT_MILLIS);
    }
}