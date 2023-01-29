 package com.child.parent.kidcare.views.otplockscreen;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.child.parent.kidcare.MyApplication;
import com.child.parent.kidcare.R;
import com.child.parent.kidcare.utils.Util;
import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;


 public class OtpRequestActivity extends AppCompatActivity {

    EditText edPin;

    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_request);
        edPin = findViewById(R.id.et_otp);

        packageName = getIntent().getExtras().getString("pkg");

        Button verifyButton = findViewById(R.id.btn_verify_otp);

        verifyButton.setOnClickListener(


                v ->  {
                    String pin = edPin.getText().toString();

                    if(pin == null || pin.length() !=4) {
                        showSnackBar("Please enter the valid PIN!");
                    } else  if(Util.verifyPassCode(pin)) {
                        //Reset db
                        new Thread(() -> {
                            MyApplication.getPkgDAO().updateDailyLockStatus( packageName, false);

                        }).start();
                        finishAffinity();
                    } else {
                        showSnackBar("Invalid PIN!");
                    }
                    });
    }
    @Override
    public void onBackPressed() {
        return;
    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),msg, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snackbar.show();
    }
}
