package com.child.parent.kidcare.views.sendfeedback;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.child.parent.kidcare.BaseActivity;
import com.child.parent.kidcare.R;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SendFeedbackActivity extends BaseActivity {
    private LinearLayout linearlayoutfeedback;
    private EditText txtTitle,txtMessage;
    private Button btnSendfeedback;
    private TextInputLayout textInputLayoutTitle,textInputLayoutMessage;
    private ImageView imglogoAbout;
    private TextView txtaboutcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendfeedback);
        linearlayoutfeedback = findViewById(R.id.linearlayout_feedback);
        textInputLayoutTitle = findViewById(R.id.textInputLayoutTitle);
        textInputLayoutMessage = findViewById(R.id.textInputLayoutMessage);
        txtTitle = findViewById(R.id.txtTitle);
        txtMessage = findViewById(R.id.txtMessage);
        btnSendfeedback = findViewById(R.id.btnSendfeedback);
        imglogoAbout = findViewById(R.id.imglogoabout);
        txtaboutcontent = findViewById(R.id.txtaboutontent);
        Intent mIntent = getIntent();
        if (Intent.ACTION_VIEW.equals(mIntent.getAction())) {
            if(mIntent.getData() != null){
                String data = mIntent.getDataString();
                getSupportActionBar().setTitle(data);
                linearlayoutfeedback.setVisibility(View.GONE);
                imglogoAbout.setVisibility(View.GONE);
                txtaboutcontent.setVisibility(View.GONE);
                if(data.equalsIgnoreCase(getString(R.string.SendFeedback))) {
                    linearlayoutfeedback.setVisibility(View.VISIBLE);
                }else{
                    imglogoAbout.setVisibility(View.VISIBLE);
                    txtaboutcontent.setVisibility(View.VISIBLE);
                }

            }

        }

        btnSendfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* String title = txtTitle.getText().toString();
                String message = txtMessage.getText().toString();
                if(title.length() > 0){
                    textInputLayoutTitle.setError(null);
                    if(message.length() > 0){
                        textInputLayoutMessage.setError(null);
                        Intent gmail = new Intent(Intent.ACTION_VIEW);
                        gmail.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
                        gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { "Muneesselvaraj7@gmail.com" });
                        gmail.setData(Uri.parse("Muneesselvaraj7@gmail.com"));
                        gmail.putExtra(Intent.EXTRA_SUBJECT, "enter something");
                        gmail.setType("plain/text");
                        gmail.putExtra(Intent.EXTRA_TEXT, "hi android jack!");
                        startActivity(gmail);
                        txtMessage.setText(null);
                        txtTitle.requestFocus();
                    }else {
                        textInputLayoutMessage.setError("Enter the Description");
                    }
                }else {
                    textInputLayoutTitle.setError("Enter the title");
                }*/
            }
        });
    }


}
