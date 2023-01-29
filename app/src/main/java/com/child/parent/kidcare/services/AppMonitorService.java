package com.child.parent.kidcare.services;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.child.parent.kidcare.MyApplication;
import com.child.parent.kidcare.R;
import com.child.parent.kidcare.alarms.BootBroadcastReceiver;
import com.child.parent.kidcare.db.Packages;
import com.child.parent.kidcare.utils.TimePreference;
import com.child.parent.kidcare.views.otplockscreen.OtpRequestActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.child.parent.kidcare.alarms.BootBroadcastReceiver.ALARM_SERVICE_TRIGGER;

public class AppMonitorService extends Service {
    public static final String TAG = AppMonitorService.class.getName();

    private String CURRENT_APP = "com.child.parent.kidcare";
    private int mInterval = 2;
    private Handler mHandler = new Handler();
    String app;
    private String mLauncherName;
    ArrayList<String> application = new ArrayList<>();
    boolean isScreenAwake, isStarted = false,
            isUnlocked = false, temp = false, isUpdated = false;
    String currentForegroundApp = "";

    private boolean mServiceStarted;

    private ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(1);


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        mLauncherName = getLauncherName();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            if (!isStarted)
                startRepeatingTask();
            isStarted = true;

        return START_STICKY;
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {

        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.logo_color)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
//        String NOTIFICATION_CHANNEL_ID = "example.permanence";
//        String channelName = "Background Service";
//        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
//        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
//        notificationManager.createNotificationChannel(notificationChannel);
//        Notification notification = new Notification.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.logo_color)
//                .setContentTitle(getString(R.string.app_name))
//                .setSubText("This is your care time!. Yor are blocked from the activities!")
//                .setChannelId(NOTIFICATION_CHANNEL_ID)
//                .build();
//        this.startForeground(2, notification);


    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                PowerManager powerManager = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
                isScreenAwake = (Build.VERSION.SDK_INT < 20 ? powerManager.isScreenOn() : powerManager.isInteractive());
                if (isScreenAwake) {
                    app = getForegroundApp();
                    if (!app.equals(CURRENT_APP) && !app.equals(mLauncherName)) {
                        List<Packages> packageItem = MyApplication.getPkgDAO().getPackage(app);
                        boolean isLocked = packageItem != null && packageItem.size() != 0 && packageItem.get(0).isLockedNow();
                        if (isLocked) {
                            startActivityViaIntent(app);
                        }
                    }
                    currentForegroundApp = app;

                }
                if (!temp || !isScreenAwake) {
                    isUnlocked = false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    };

    private void startActivityViaIntent(String pkg) {

        mHandler.post(() -> {
            Intent intent = new Intent(getApplicationContext(), OtpRequestActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("pkg", pkg);
            startActivity(intent);
        });

    }


    public String getForegroundApp() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            @SuppressLint("WrongConstant") UsageStatsManager mUsageStatsManager =
                    (UsageStatsManager) getBaseContext().getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {

                    Log.i(TAG,"current App ---->"+mySortedMap.get(mySortedMap.lastKey()).getPackageName());
                    return mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo ar = RunningTask.get(0);

            return ar.topActivity.getClassName();
        }
        return currentForegroundApp;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG,"onTaskRemoved");
        if(TimePreference.getAlarmStatus()) {
            Intent intent = new Intent(getApplicationContext(), BootBroadcastReceiver.class);
            intent.setAction(ALARM_SERVICE_TRIGGER);
            intent.putExtra("isStartService", true);
            sendBroadcast(intent);
        }
        super.onTaskRemoved(rootIntent);

    }

    void startRepeatingTask() {
        // mStatusChecker.run();

        scheduleTaskExecutor.scheduleAtFixedRate(mStatusChecker, 0, mInterval, TimeUnit.SECONDS);

    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
        isStarted = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");

        //shutdown the scheduler.
        scheduleTaskExecutor.shutdown();

       // if(!isStoptedBYUser) {
          //  startStopedForcely();
          //  stopRepeatingTask();
      //  } else {
           // scheduleTaskExecutor.
       // }


    }

    private String getLauncherName() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    void startStopedForcely() {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());


        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        //maybe 200 if error change to 400
    }


}

