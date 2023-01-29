package com.child.parent.kidcare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.os.BuildCompat;

import com.child.parent.kidcare.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class TimePreference {

    private static final String TAG= TimePreference.class.getName();
    private static final String TIME_PREFERENCES_NAME = "time_preferences";
    private static final String START_TIME_KEY = "start_time";
    private static final String STOP_TIME_KEY = "stop_time";
    private static final String  SELECTED_DAYS = "selected_days";
    private static final String ALARM_STATUS = "alarm_status";
    private static TimePreference sTimePreference;
    private final SharedPreferences mSharedPreferences;




    private TimePreference(Context context) {
        Context storageContext;
        if (BuildCompat.isAtLeastN()) {
            // All N devices have split storage areas, but we may need to
            // move the existing preferences to the new device protected
            // storage area, which is where the data lives from now on.
            final Context deviceContext = context.createDeviceProtectedStorageContext();
            if (!deviceContext.moveSharedPreferencesFrom(context,
                    TIME_PREFERENCES_NAME)) {
                Log.w(TAG, "Failed to migrate shared preferences.");
            }
            storageContext = deviceContext;
        } else {
            storageContext = context;
        }
        mSharedPreferences = storageContext
                .getSharedPreferences(TIME_PREFERENCES_NAME, Context.MODE_PRIVATE);


    }




    public static void setStartTime(int milliSec) {
        sTimePreference.setPref(START_TIME_KEY, milliSec);


    }

    public static void setStopTime( int milliSec) {
        sTimePreference.setPref( STOP_TIME_KEY, milliSec);

    }

    public static int getStartTime() {

        return sTimePreference.getPref( START_TIME_KEY);
    }

    public static int getStopTime() {

        return sTimePreference.getPref( STOP_TIME_KEY);
    }

    private  void setPref(String key, int value) {
         SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    private  int getPref( String key) {
        try {
            return mSharedPreferences.getInt(key, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static void saveSelectedDays(List<Integer> list){
         sTimePreference.selectedDays(list);
    }

    private void selectedDays(List<Integer> list){

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(SELECTED_DAYS, json);
        editor.apply();

    }
    public static List<Integer> getSelectedDaysList() {
    return  sTimePreference.getDaysList();
    }

    private   List<Integer> getDaysList(){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString(SELECTED_DAYS, null);
        Type type = new TypeToken<List<Integer>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void setAlarmStatus(boolean value) {
        sTimePreference.alarmStatus(value);
    }

    public static boolean getAlarmStatus() {
        return sTimePreference.alarmStatusValue();
    }
    private void alarmStatus(boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ALARM_STATUS,value);
        editor.apply();
    }

    private  boolean alarmStatusValue() {
        try {
            return mSharedPreferences.getBoolean(ALARM_STATUS, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void init(Context context) {

        if(sTimePreference == null) {
            sTimePreference = new TimePreference(context);
        }
    }

}
