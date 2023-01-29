package com.child.parent.kidcare.views.passcode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.child.parent.kidcare.BaseActivity;
import com.child.parent.kidcare.R;
import com.child.parent.kidcare.utils.Util;

import org.json.JSONException;

public class PassCodeActivity extends BaseActivity {
    private static final int CODE_AUTHENTICATION_VERIFICATION=241;
    private TextView tvPassCode;
    private TextView txtTimer;
    long MillisecondTime, TimeBuff, UpdateTime = 0L;
    int Seconds, Minutes, MilliSeconds;
    private ImageView copy;
    private ImageView mRefreshImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_code);
        tvPassCode = findViewById(R.id.tvPasscode);
        txtTimer = findViewById(R.id.txtTimer);
        copy = findViewById(R.id.imCpy);
        mRefreshImageView = findViewById(R.id.ivRefresh);
        mRefreshImageView.setEnabled(false);
        mRefreshImageView.setOnClickListener(v -> {
            getPasscode();

        });
        copy.setOnClickListener(v -> {
            if(tvPassCode.getText()!=null) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData
                        .newPlainText("Your OTP", tvPassCode.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(),
                        "Your OTP is copied", Toast.LENGTH_SHORT).show();
            }
        });

        KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        if(km.isKeyguardSecure()) {

            Intent i = km.createConfirmDeviceCredentialIntent("Kid-Care", "Authentication is required to generate passcode");
            startActivityForResult(i, CODE_AUTHENTICATION_VERIFICATION);
        } else {
            getPasscode();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==CODE_AUTHENTICATION_VERIFICATION)
        {
            getPasscode();
        }
        else
        {
            Toast.makeText(this, "Failure: Unable to verify user's identity", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getPasscode() {
        copy.setVisibility(View.VISIBLE);
        mRefreshImageView.setVisibility(View.VISIBLE);

            tvPassCode.setText(Util.getPasscode());
            startTimer();
            setLocked(mRefreshImageView);


    }

    public void startTimer() {
        new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {

                MillisecondTime = millisUntilFinished;

                UpdateTime = TimeBuff + MillisecondTime;

                Seconds = (int) (UpdateTime / 1000);

                Minutes = Seconds / 60;

                Seconds = Seconds % 60;

                MilliSeconds = (int) (UpdateTime % 1000);

                txtTimer.setText("OTP will expire in 0" + Minutes + ":"
                        + String.format("%02d", Seconds)
                        );
            }

            public void onFinish() {
                txtTimer.setText("");
                mRefreshImageView.setEnabled(true);
                setUnlocked(mRefreshImageView);

            }
        }.start();


    }

    public static void  setLocked(ImageView v)
    {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
        v.setImageAlpha(128);   // 128 = 0.5
    }

    public static void  setUnlocked(ImageView v)
    {
        v.setColorFilter(null);
        v.setImageAlpha(255);
    }
}