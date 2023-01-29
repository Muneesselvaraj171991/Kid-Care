/*
* Copyright 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.child.parent.kidcare.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.util.Log;

import com.child.parent.kidcare.utils.TimePreference;

import java.util.Calendar;
import java.util.List;

import static com.child.parent.kidcare.alarms.BootBroadcastReceiver.ALARM_SERVICE_TRIGGER;


public class AlarmUtil {

    private static final String TAG = "AlarmUtil";
    private final Context mContext;
    private final AlarmManager mAlarmManager;
    private final AlarmStorage mAlarmStorage;

    public AlarmUtil(Context context) {
        mContext = context;
       // mAlarmManager = mContext.getSystemService(AlarmManager.class);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mAlarmStorage = new AlarmStorage(context);
    }


    public void scheduleAlarm(Alarm alarm, boolean isStartService) {
        List<Integer> selectedDays = TimePreference.getSelectedDaysList();
        Log.d(TAG, selectedDays.toString());
        if(selectedDays.size()==7) {
            scheduleAlarm(alarm, AlarmManager.INTERVAL_DAY, -1, isStartService);
        } else {

            for(int day : selectedDays) {
                scheduleAlarm(alarm, AlarmManager.INTERVAL_DAY*7, day, isStartService);
            }

        }


    }

    private void scheduleAlarm(Alarm alarm, long intervel, int weekOfTheDay, boolean isStartService) {
        Intent intent = new Intent(mContext, BootBroadcastReceiver.class);
        intent.setAction(ALARM_SERVICE_TRIGGER);
        intent.putExtra("isStartService", isStartService);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(mContext, alarm.id, intent, 0);
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.MONTH, alarm.month);
        alarmTime.set(Calendar.DATE, alarm.date);
        alarmTime.set(Calendar.HOUR_OF_DAY, alarm.hour);
        alarmTime.set(Calendar.MINUTE, alarm.minute);
        alarmTime.set(Calendar.SECOND, 0);

        if(weekOfTheDay != -1) {
            alarmTime.set(Calendar.DAY_OF_WEEK, weekOfTheDay);
        }
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                alarmTime.getTimeInMillis(), intervel, pendingIntent);
        Log.i(TAG,
                String.format("Alarm scheduled at (%2d:%02d) Date: %d, Month: %d",
                        alarm.hour, alarm.minute,
                        alarm.month, alarm.date));
    }

    public void deleteScheduledAlarms() {
        Log.d("Alarms","Size"+mAlarmStorage.getAlarms().size());
        for (Alarm alarm : mAlarmStorage.getAlarms()) {
            Intent myIntent = new Intent(mContext, BootBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarm.id, myIntent, 0);

            if (pendingIntent != null && mAlarmManager != null) {

                mAlarmManager.cancel(pendingIntent);
                mAlarmStorage.deleteAlarm(alarm);            }

        }

    }
    public Calendar getNextAlarmTime(int hour, int minute) {
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        if (alarmTime.before(now)) {    //this condition is used for future reminder that means your reminder not fire for past time
            alarmTime.add(Calendar.DATE, 7);
        }
        return alarmTime;
    }

    }
