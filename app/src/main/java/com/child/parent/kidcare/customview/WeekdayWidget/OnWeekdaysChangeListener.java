package com.child.parent.kidcare.customview.WeekdayWidget;

import android.view.View;

import java.util.List;


public interface OnWeekdaysChangeListener {
    /**
     * @param view             View of Weekdayspicker
     * @param clickedDayOfWeek Last clicked day
     * @param selectedDays     Integer-list of days from {@link java.util.Calendar}
     */
    void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays);
}
