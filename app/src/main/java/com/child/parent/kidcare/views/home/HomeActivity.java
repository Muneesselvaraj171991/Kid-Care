package com.child.parent.kidcare.views.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.child.parent.kidcare.R;
import com.child.parent.kidcare.settings.SettingsActivity;
import com.child.parent.kidcare.utils.Util;
import com.child.parent.kidcare.views.UserProfile.UserProfileActivity;
import com.child.parent.kidcare.views.applist.SelectAppActivity;
import com.child.parent.kidcare.views.otplockscreen.OtpRequestActivity;
import com.child.parent.kidcare.views.settime.SetTimerActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {


    TextView txtUserName,txtUserDetails;
    boolean reFetchProfileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        txtUserName = findViewById(R.id.homescreen_lblUserName);
        txtUserDetails = findViewById(R.id.homescreen_lblUserInfo);
        fetchProfiledata();
        boolean isParentMobile = Util.isParentMobile(getApplicationContext());
        View viewSelectApp = findViewById(R.id.homescreen_linearSelectApp);
        viewSelectApp.setOnClickListener(v -> {
            if(isParentMobile) {
                launchSelectActivityScreen();
            } else {
                Intent intent = new Intent(getApplicationContext(), OtpRequestActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("pkg", "com.child.parent.kidcare");
                startActivity(intent);
            }


        });

        View viewSetTime = findViewById(R.id.homescreen_linearSetTime);
        viewSetTime.setOnClickListener(v -> {



            startActivity(new Intent(this, SetTimerActivity.class));
            overridePendingTransition(R.animator.slide_up,  R.animator.slide_down);

        });
        View viewSettings = findViewById(R.id.homescreen_linearSettings);
        viewSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.animator.slide_up,  R.animator.slide_down);
        });
        View viewUserProfile = findViewById(R.id.homescreen_linearUserProfile);
        viewUserProfile.setOnClickListener(v -> {
            reFetchProfileData = true;
            startActivity(new Intent(this, UserProfileActivity.class));
            overridePendingTransition(R.animator.slide_up,  R.animator.slide_down);
        });



    }

    private void launchSelectActivityScreen() {
        startActivity(new Intent(this, SelectAppActivity.class));
        overridePendingTransition(R.animator.slide_up, R.animator.slide_down);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(reFetchProfileData) {
            fetchProfiledata();
            reFetchProfileData = false;
        }
    }



    private void fetchProfiledata() {
        JSONObject jsonUserProfile = Util.loadUserProfileMap(getApplicationContext());
        try {
            if(jsonUserProfile.length() > 0 ){
                txtUserName.setText(jsonUserProfile.getString("UserName"));
                txtUserDetails .setText(jsonUserProfile.getString("EmailAddress"));
            }
        } catch (JSONException e) {
            // Oops
        }
    }



}
