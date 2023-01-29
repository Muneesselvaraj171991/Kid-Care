package com.child.parent.kidcare.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.TimeUtils;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static final String EMAIL ="muneesselvaraj7@gmail.com"; //your-gmail-username
    public static final String PASSWORD ="Lovegiri@171991"; //your-gmail-password

    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);
            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {

        }
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(EMAIL_STRING, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
    public static void saveUserProfileMap(Context context,Map<String,String> inputMap){
        SharedPreferences pSharedPref = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("My_map").commit();
            editor.putString("My_map", jsonString);
            editor.commit();
        }
    }

    public static  JSONObject loadUserProfileMap(Context context){
        JSONObject jsonObject = new JSONObject();
        SharedPreferences pSharedPref = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                jsonObject = new JSONObject(jsonString);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }
    public static boolean isValidMobile(String phone) {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 13;
        }
        return false;
    }


    public static String getPasscode(){
        String passcode = null;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        passcode = PasscodeUtil.getEncode(calendar.get(Calendar.HOUR_OF_DAY))+ PasscodeUtil.getEncode(calendar.get(Calendar.MINUTE));
        return passcode;
    }

    public static boolean verifyPassCode(String pin) {
        int hour = PasscodeUtil.getKeysByValue(pin.substring(0,2));
        int min = PasscodeUtil.getKeysByValue(pin.substring(2,4));
        if(min ==59) {
            min = 0;
            hour++;
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
       String passcode = PasscodeUtil.getEncode(calendar.get(Calendar.HOUR_OF_DAY))+ PasscodeUtil.getEncode(calendar.get(Calendar.MINUTE));

        return pin.equalsIgnoreCase(passcode) || passcode.equalsIgnoreCase(PasscodeUtil.getEncode(hour)+PasscodeUtil.getEncode(min+1));
    }


    public static boolean isParentMobile(Context context) {
        JSONObject jsonUserProfile = loadUserProfileMap(context);
        try {
            if(jsonUserProfile.length() > 0 ){
                return jsonUserProfile.getString("IsParentMobile").equals("true");
            }
        } catch (JSONException e) {
            // Oops
            return false;
        }
        return false;
    }
}
