package com.child.parent.kidcare.views.UserProfile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import com.child.parent.kidcare.BaseActivity;
import com.child.parent.kidcare.R;
import com.child.parent.kidcare.customview.PhoneEditText;
import com.child.parent.kidcare.utils.Util;
import com.child.parent.kidcare.views.home.HomeActivity;
import com.google.android.material.textfield.TextInputLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;


public class UserProfileActivity extends BaseActivity implements View.OnClickListener{
    private EditText edtUserName,edtDob,edtEmailAddress;
    private PhoneEditText edtParentMobileNumber,edtChildMobileNumber;
    private DatePickerDialog picker;
    private Button btnRegister;
    private TextInputLayout textInputLayoutEmail,textInputLayoutParentMobileNumber,textInputLayoutChildMobileNumber,textInputLayoutUserName,textInputLayoutDOB;
    private Toolbar mToolbar;
    private ImageView imgbackground,imglogo,imgbackground1,imgEditProfile;
    private TextView txtappname,txtUserProfile;
    private SwitchCompat switchCompatIsParent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.VISIBLE);
        edtUserName = findViewById(R.id.userprofile_edtUsername);
        edtDob = findViewById(R.id.userprofile_edtDob);
        edtEmailAddress = findViewById(R.id.userprofile_edtEmailAddress);
        edtParentMobileNumber = findViewById(R.id.userprofile_edtParentMobileNumber);
        edtChildMobileNumber = findViewById(R.id.userprofile_edtChildMobileNumber);
        textInputLayoutEmail =  findViewById(R.id.userprofile_textInputLayoutEmail);
        textInputLayoutParentMobileNumber =  findViewById(R.id.userprofile_textInputLayoutParentMobileNumber);
        textInputLayoutChildMobileNumber =  findViewById(R.id.userprofile_textInputLayoutChildMobileNumber);
        textInputLayoutUserName = findViewById(R.id.userprofile_textInputLayoutUserName);
        textInputLayoutDOB = findViewById(R.id.textInputDOB);
        imgEditProfile = findViewById(R.id.userprofileEdit);
        imgEditProfile.setVisibility(View.VISIBLE);
        btnRegister = findViewById(R.id.userprofile_btnRegister);
        switchCompatIsParent = findViewById(R.id.userprofile_switchParent);
        txtUserProfile = findViewById(R.id.userprofile_lblUserProfile);
        imgbackground = findViewById(R.id.img_background);
        imgbackground1 = findViewById(R.id.img_background1);
        imglogo = findViewById(R.id.img_logo);
        txtappname = findViewById(R.id.txtAppName);
        imgbackground.setVisibility(View.VISIBLE);
        imgbackground1.setVisibility(View.VISIBLE);
        imglogo.setVisibility(View.VISIBLE);
        txtappname.setVisibility(View.VISIBLE);
        edtDob.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        imgEditProfile.setOnClickListener(this);
        edtUserName.addTextChangedListener(textWatcher);
        edtDob.addTextChangedListener(textWatcher);
        edtEmailAddress.addTextChangedListener(textWatcher);
        edtParentMobileNumber.addTextChangedListener(textWatcher);
        edtChildMobileNumber.addTextChangedListener(textWatcher);
        loadUserProfile();
        switchCompatIsParent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 textInputLayoutChildMobileNumber.setVisibility(isChecked? View.GONE: View.VISIBLE);

            }
        });
    }

    private void loadUserProfile() {
        txtUserProfile.setVisibility(View.VISIBLE);
        JSONObject jsonUserProfile = Util.loadUserProfileMap(getApplicationContext());
        try {
            if(jsonUserProfile.length() > 0 ){
                getSupportActionBar().setTitle(getString(R.string.UserProfile));
                txtUserProfile.setVisibility(View.GONE);
                imgbackground.setVisibility(View.GONE);
                imgbackground1.setVisibility(View.GONE);
                imglogo.setVisibility(View.GONE);
                txtappname.setVisibility(View.GONE);
                edtUserName.setText(jsonUserProfile.getString("UserName"));
                edtDob .setText(jsonUserProfile.getString("Dob"));
                edtEmailAddress .setText(jsonUserProfile.getString("EmailAddress"));
                edtParentMobileNumber .setText(jsonUserProfile.getString("ParentMobileNumber"));
                if(!jsonUserProfile.getString("IsParentMobile").equals("true")) {
                    edtChildMobileNumber.setText(jsonUserProfile.getString("ChildMobileNumber"));
                } else {
                    textInputLayoutChildMobileNumber.setVisibility(View.GONE);

                }
                switchCompatIsParent.setChecked(jsonUserProfile.getString("IsParentMobile").equals("true"));
                switchCompatIsParent.setEnabled(false);
                enabled(false);
            }else{
                mToolbar.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            // Oops
        }


    }

    private void enabled(boolean enabled) {
        edtUserName.setEnabled(enabled);
        edtDob.setEnabled(enabled);
        edtEmailAddress.setEnabled(enabled);
        edtParentMobileNumber.setEnabled(false);
        edtChildMobileNumber.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
        btnRegister.setBackground(getDrawable(R.drawable.login_button_transparent));
        if(enabled){
            btnRegister.setBackground(getDrawable(R.drawable.login_button_bk));
        }
        btnRegister.setText("Update");
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (edtEmailAddress.getText().hashCode() == s.hashCode()) {
                Is_Valid_Email(edtEmailAddress);
            }
            else if (edtParentMobileNumber.getText().hashCode() == s.hashCode()) {
                Is_Valid_Phone(edtParentMobileNumber,textInputLayoutParentMobileNumber);
            }
            else if (edtChildMobileNumber.getText().hashCode() == s.hashCode()) {
                Is_Valid_Phone(edtChildMobileNumber,textInputLayoutChildMobileNumber);

            } else if(edtDob.getText().hashCode()==s.hashCode()) {
                if(textInputLayoutDOB.getError()!=null) {
                    textInputLayoutDOB.setError(null);
                }
            } else if(edtUserName.getText().hashCode() == s.hashCode()) {
                if(textInputLayoutUserName.getError()!=null) {
                    textInputLayoutUserName.setError(null);
                }
            }
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userprofile_edtDob:
                textInputLayoutUserName.setError(null);
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(UserProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edtDob.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, year, month, day);
                picker.show();
                edtEmailAddress.requestFocus();
                int pos = edtEmailAddress.getText().length();
                edtEmailAddress.setSelection(pos);
                break;
            case R.id.userprofile_btnRegister:
                if(isVarified()) {
                    startHomeScreen();
                }


                break;
            case R.id.userprofileEdit:
                enabled(true);
                int position = edtUserName.getText().length();
                edtUserName.setSelection(position);
                edtUserName.requestFocus();
                break;
            default:
                break;
        }
    }


    private boolean isVarified() {
        resetError();
        if(TextUtils.isEmpty(edtUserName.getText())){
            textInputLayoutUserName.setError("Enter User Name");
            return false;
        } else if(TextUtils.isEmpty(edtDob.getText().toString().trim())){
            textInputLayoutDOB.setError("Enter DOB");
            return false;
        } else if(TextUtils.isEmpty(edtEmailAddress.getText())) {
            textInputLayoutEmail.setError("Enter email");
            return false;
        } else if(!Util.isEmailValid(edtEmailAddress.getText().toString())) {
            textInputLayoutEmail.setError("Enter valid email");
            return false;
        } else if(TextUtils.isEmpty(edtParentMobileNumber.getText())) {
            textInputLayoutParentMobileNumber.setError("Enter parent mobile number");
            return false;
        } else if(!Util.isValidPhoneNumber(edtParentMobileNumber.getText().toString())) {
            textInputLayoutParentMobileNumber.setError("Enter valid mobile number");
            return false;
        } else if(!switchCompatIsParent.isChecked()) {
             if(TextUtils.isEmpty(edtChildMobileNumber.getText())) {
                textInputLayoutChildMobileNumber.setError("Enter child mobile number");
                 return false;
            } else if(!Util.isValidPhoneNumber(edtChildMobileNumber.getText().toString())) {
                 textInputLayoutChildMobileNumber.setError("Enter valid mobile number");
                 return false;
            } else  if(edtParentMobileNumber.getText().toString().equalsIgnoreCase(edtChildMobileNumber.getText().toString())) {
                 textInputLayoutChildMobileNumber.setError("Number should not same as parent");
                 return false;
             }
        }
        return true;
    }

    private void resetError() {
        textInputLayoutChildMobileNumber.setError(null);
        textInputLayoutParentMobileNumber.setError(null);
        textInputLayoutEmail.setError(null);
        textInputLayoutDOB.setError(null);
        textInputLayoutUserName.setError(null);
    }
    private void startHomeScreen() {
        HashMap<String, String> testHashMap = new HashMap<String, String>();
        testHashMap.put("UserName", edtUserName.getText().toString());
        testHashMap.put("Dob", edtDob.getText().toString());
        testHashMap.put("EmailAddress", edtEmailAddress.getText().toString());
        testHashMap.put("ParentMobileNumber", edtParentMobileNumber.getText().toString());
        if(!switchCompatIsParent.isChecked()) {
            testHashMap.put("ChildMobileNumber", edtChildMobileNumber.getText().toString());
        }
        testHashMap.put("IsParentMobile", switchCompatIsParent.isChecked()? "true" : "false");
        Util.saveUserProfileMap(getApplicationContext(),testHashMap);
        if(!btnRegister.getText().equals("Update")) {
            SharedPreferences preferences =
                    getSharedPreferences("ON_BOARDING_PREFERENCE", MODE_PRIVATE);

            preferences.edit()
                    .putBoolean("is_onboarding_completed",true).apply();
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.animator.slide_up, R.animator.slide_down);

        }
        finish();
    }



    public void Is_Valid_Email(EditText edt) {
        textInputLayoutEmail.setError(Util.isEmailValid(edt.getText().toString())? null : "Invalid Email Address");
    }
    public void Is_Valid_Phone(EditText edt,TextInputLayout textInputLayout) {
        textInputLayout.setError(Util.isValidPhoneNumber(edt.getText().toString().trim())? null :"Invalid Phone Number" );

    }
}
