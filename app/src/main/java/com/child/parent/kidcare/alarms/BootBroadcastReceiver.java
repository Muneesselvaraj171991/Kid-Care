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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;

import androidx.core.os.BuildCompat;

import com.child.parent.kidcare.MyApplication;
import com.child.parent.kidcare.db.PackageDAO;
import com.child.parent.kidcare.db.Packages;
import com.child.parent.kidcare.services.AppMonitorService;
import com.child.parent.kidcare.utils.TimePreference;

import java.util.List;


public class BootBroadcastReceiver extends BroadcastReceiver {

    public static final String ALARM_SERVICE_TRIGGER = "manage_service";

    private static final String TAG = "BootBroadcastReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();


        switch (action) {
            case Intent.ACTION_LOCKED_BOOT_COMPLETED:
            case Intent.ACTION_BOOT_COMPLETED:
                Log.d(TAG,"ACTION_BOOT_COMPLETED--------------------->");
                restoreAlarms(action, context);
                break;

            case ALARM_SERVICE_TRIGGER:
                Log.d(TAG,"ALARM_SERVICE_TRIGGER--------------------->");

                manageService(context,intent.getExtras().getBoolean("isStartService",false));
                break;


        }

    }

    private void manageService(Context context, boolean isStartService) {

        TimePreference.init(context);

        Intent intent = new Intent(context, AppMonitorService.class);
        intent.putExtra("isStartService", isStartService);
        if (isStartService) {
            updateDB(true);

            Log.d(TAG,"Service started--------------------->");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }

            TimePreference.setAlarmStatus(true);


        } else {
            Log.d(TAG,"Service stopped--------------------->");
            updateDB(false);

            TimePreference.setAlarmStatus(false);
            context.stopService(intent);
        }
    }


    private void restoreAlarms(String action, Context context) {
        boolean bootCompleted;
        if (BuildCompat.isAtLeastN()) {
            bootCompleted = Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action);
        } else {
            bootCompleted = Intent.ACTION_BOOT_COMPLETED.equals(action);
        }
        if (!bootCompleted) {
            return;
        }
        AlarmUtil util = new AlarmUtil(context);
        AlarmStorage alarmStorage = new AlarmStorage(context);
        Log.d(TAG, alarmStorage.getAlarms().toString());
        for (Alarm alarm : alarmStorage.getAlarms()) {
            util.scheduleAlarm(alarm, alarm.isStartSchedule);
        }
    }

    private void updateDB(boolean isStart) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                PackageDAO dao = MyApplication.getPkgDAO();
                List<Packages> dbPackageList = dao.getPackageList();



                    for (Packages pkg : dbPackageList) {
                        if (pkg.isLocked()) {

                            dao.updateDailyLockStatus(pkg.getPkgName(), isStart);

                        }
                    }
                }

        }).start();

    }

}
