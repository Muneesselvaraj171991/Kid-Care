package com.child.parent.kidcare.views.settime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;



import com.child.parent.kidcare.BaseActivity;
import com.child.parent.kidcare.R;
import com.child.parent.kidcare.alarms.Alarm;
import com.child.parent.kidcare.alarms.AlarmStorage;
import com.child.parent.kidcare.alarms.AlarmUtil;
import com.child.parent.kidcare.customview.WeekdayWidget.OnWeekdaysChangeListener;
import com.child.parent.kidcare.customview.WeekdayWidget.WeekdaysPicker;
import com.child.parent.kidcare.customview.timepicker.SleepTimePicker;
import com.child.parent.kidcare.helper.Validation;
import com.child.parent.kidcare.utils.TimePreference;
import com.child.parent.kidcare.utils.Util;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

public class SetTimerActivity extends BaseActivity {
    private List<Integer> selected_days;
    SleepTimePicker picker;
    private AlarmStorage mAlarmStorage;
    private AlarmUtil mAlarmUtil;
    private boolean mIsTimerChenged;
    Button btnSetTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settime);
        mAlarmStorage = new AlarmStorage(this);
        mAlarmUtil = new AlarmUtil(this);
        TextView startTimeText = findViewById(R.id.tvStartTime);
        TextView endTimeText = findViewById(R.id.tvEndTime);
        TextView tvHours = findViewById(R.id.tvHours);
        TextView tvMins = findViewById(R.id.tvMins);
        View minuteview = findViewById(R.id.llMins);
        MaterialCheckBox checkboxEveryDay = findViewById(R.id.checkboxEveryDay);
        WeekdaysPicker weekdaysPicker = findViewById(R.id.weekdaywidget);
        btnSetTimer = findViewById(R.id.btnsetTimer);

        weekdaysPicker.setOnWeekdaysChangeListener((view, clickedDayOfWeek, selectedDays) -> {
            enableTimerButton();
            mIsTimerChenged = true;
            selected_days = selectedDays;
                checkboxEveryDay.setChecked(selected_days.size() == 7);

        });

        checkboxEveryDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            enableTimerButton();
            mIsTimerChenged = true;
            if (isChecked) {
                selected_days = new ArrayList<>(Arrays.asList(SUNDAY, MONDAY,TUESDAY,WEDNESDAY, THURSDAY,FRIDAY,SATURDAY));

            } else {
                if(selected_days.size()==7) {
                    selected_days.clear();

                }
            }
            weekdaysPicker.setSelectedDays(selected_days);
        });
        DateTimeFormatter formatter = Validation.getTimeFormater();

        picker = findViewById(R.id.timePicker);
        picker.setLisener(new SleepTimePicker.Listener() {
            @Override
            public void handleUpdates(LocalTime startTime, LocalTime endTime) {
                startTimeText.setText(startTime.format(formatter));
                endTimeText.setText(endTime.format(formatter));
                LocalDateTime startDate = startTime.atDate(LocalDate.now());
                LocalDateTime endDate = endTime.atDate(LocalDate.now());
                if (startDate.compareTo(endDate) >= 0) {
                    endDate = endDate.plusDays(1L);
                }
                Duration duration = Duration.between(startDate, endDate);
                long hours = duration.toHours();
                long minutes = duration.toMinutes() % (long)60;
                tvHours.setText(String.valueOf(hours));
                tvMins.setText(String.valueOf(minutes));
                minuteview.setVisibility(minutes >0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onViewChange() {
                enableTimerButton();

            }

        });

        mToolbar.setNavigationOnClickListener(v -> {
                backClicked();

        });
        picker.setTime();

        selected_days = TimePreference.getSelectedDaysList();
        if(selected_days !=null && selected_days.size() != 0) {
            weekdaysPicker.setSelectedDays(selected_days);
            checkboxEveryDay.setChecked(selected_days.size() == 7);
        } else {
            selected_days = new ArrayList<>(Arrays.asList(SUNDAY, MONDAY,TUESDAY,WEDNESDAY, THURSDAY,FRIDAY,SATURDAY));
            weekdaysPicker.setSelectedDays(selected_days);
        }


        btnSetTimer.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                    SetTimerActivity.this);
            alertDialog2.setTitle("Save");
            alertDialog2.setMessage("Changes are made. Do you want to save?");
            alertDialog2.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(isChangesAreMade()) {
                                scheduleNotification();
                                finishActivity();
                            }
                        }
                    });

            alertDialog2.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finishActivity();
                        }
                    });alertDialog2.show();
        });

        if(TimePreference.getAlarmStatus()) {
            findViewById(R.id.txtwarning).setVisibility(View.VISIBLE);
            checkboxEveryDay.setEnabled(false);
            weekdaysPicker.setEditable(false);
            picker.setEditable(false);

        }
    }

    private void backClicked() {
        if(btnSetTimer.getVisibility() == View.VISIBLE){
            btnSetTimer.callOnClick();
        }else{
            finishActivity();
        }
    }

    private void finishActivity() {
        finish();
        overridePendingTransition(R.animator.slide_up,  R.animator.slide_down);
    }

    private void enableTimerButton(){
        if(btnSetTimer.getVisibility()!= View.VISIBLE){
            btnSetTimer.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        backClicked();
    }

    boolean isChangesAreMade() {
        LocalTime startTime = picker.getStartTime();
        LocalTime endTime = picker.getEndTime();
        return mIsTimerChenged || TimePreference.getStartTime() != (startTime.getHour() * 60 + startTime.getMinute()) || TimePreference.getStopTime() != (endTime.getHour() * 60 + endTime.getMinute());
    }

    private  void scheduleNotification() {
        //delete scheculed alarm
            mAlarmUtil.deleteScheduledAlarms();
            TimePreference.saveSelectedDays(selected_days);
            setAlarm(picker.getStartTime(), true);
            picker.storeStartTimePreference();
            setAlarm(picker.getEndTime(), false);
            picker.storeEndTimePreference();


    }

    private void setAlarm(LocalTime time, boolean isStartAlarm) {
                Calendar alarmTime = mAlarmUtil
                        .getNextAlarmTime(time.getHour(), time.getMinute());
                Alarm alarm = mAlarmStorage
                        .saveAlarm(alarmTime.get(Calendar.MONTH), alarmTime.get(Calendar.DATE),
                                alarmTime.get(Calendar.HOUR_OF_DAY), alarmTime.get(Calendar.MINUTE), isStartAlarm);
                mAlarmUtil.scheduleAlarm(alarm, isStartAlarm);

    }


}