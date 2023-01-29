package com.child.parent.kidcare.customview.timepicker;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.child.parent.kidcare.R;
import com.child.parent.kidcare.utils.TimePreference;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class SleepTimePicker extends ViewGroup {
    private Paint progressPaint;
    private Paint progressBackgroundPaint;
    private Paint progressTopBlurPaint;
    private Paint progressBottomBlurPaint;
    private Paint divisionPaint;
    private Paint textPaint;
    private int divisionOffset;
    private int labelOffset;
    private int divisionLength;
    private int divisionWidth;
    private final List<Integer> hourLabels = Collections.unmodifiableList(Arrays.asList(12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
    private RectF circleBounds;
    private float radius;
    private Point center;
    private int progressBottomShadowSize;
    private int progressTopShadowSize;
    private int strokeBottomShadowColor;
    private int strokeTopShadowColor;
    private int labelColor;
    private View sleepLayout;
    private View wakeLayout;
    private double sleepAngle;
    private double wakeAngle;
    private boolean draggingSleep;
    private boolean draggingWake;
    private final int stepMinutes = 1;
    private final Rect textRect = new Rect();

    private static final int ANGLE_START_PROGRESS_BACKGROUND = 0;
    private static final int ANGLE_END_PROGRESS_BACKGROUND = 360;
    private static final float DEFAULT_STROKE_WIDTH_DP = 8.0F;
    private static final float DEFAULT_DIVISION_LENGTH_DP = 8.0F;
    private static final float DEFAULT_DIVISION_OFFSET_DP = 12.0F;
    private static final float DEFAULT_LABEL_OFFSET_DP = 36.0F;
    private static final float DEFAULT_DIVISION_WIDTH_DP = 2.0F;
    private static final float SCALE_LABEL_TEXT_SIZE = 13.0F;
    private static final String DEFAULT_PROGRESS_BACKGROUND_COLOR = "#e0e0e0";
    private static final float BLUR_STROKE_RATIO = 0.375F;
    private static final float BLUR_RADIUS_RATIO = 0.25F;
    private Assist mAssist;
    private Listener mListener;
    private boolean isEditable = true;


    @ColorInt
    public final int getProgressColor() {
        return progressPaint.getColor();
    }

    public final void setProgressColor(@ColorInt int color) {
        progressPaint.setColor(color);
        invalidate();
    }

    @ColorInt
    public final int getProgressBackgroundColor() {
        return progressBackgroundPaint.getColor();
    }

    public final void setProgressBackgroundColor(@ColorInt int color) {
        progressBackgroundPaint.setColor(color);
        invalidate();
    }

    public void setLisener(Listener lisener) {
        mListener = lisener;

    }

    @NonNull
    public final LocalTime getStartTime() {
        return this.computeBedTime();
    }

    @NonNull
    public final LocalTime getEndTime() {
        return this.computeWakeTime();
    }

    public final void setTime() {
        int startTime = (int) TimePreference.getStartTime();
        int stopTime = (int) TimePreference.getStopTime();
        if (startTime == 0 && stopTime == 0) {

            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            sleepAngle = mAssist.minutesToAngle(((hour * 60) + min));
            wakeAngle = mAssist.minutesToAngle((((hour + 1 )* 60 )+ min));
        } else {
            sleepAngle = mAssist.minutesToAngle(((startTime / 60 % 24) * 60 + startTime % 60));
            wakeAngle = mAssist.minutesToAngle(((stopTime / 60 % 24) * 60 + stopTime % 60));
        }
        invalidate();
        notifyChanges();
    }

    public float getProgressStrokeWidth() {
        return progressPaint.getStrokeWidth();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init(@NonNull Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        mAssist = new Assist();
        divisionOffset = dp2px(DEFAULT_DIVISION_OFFSET_DP);
        divisionLength = dp2px(DEFAULT_DIVISION_LENGTH_DP);
        divisionWidth = dp2px(DEFAULT_DIVISION_WIDTH_DP);
        labelOffset = dp2px(DEFAULT_LABEL_OFFSET_DP);
        int progressColor = Color.WHITE;
        int progressBackgroundColor = Color.parseColor(DEFAULT_PROGRESS_BACKGROUND_COLOR);
        int divisionColor = Color.parseColor(DEFAULT_PROGRESS_BACKGROUND_COLOR);
        int progressStrokeWidth = this.dp2px(DEFAULT_STROKE_WIDTH_DP);
        int progressBgStrokeWidth = this.dp2px(DEFAULT_STROKE_WIDTH_DP);
        Cap progressStrokeCap = Cap.ROUND;
        int sleepLayoutId = 0;
        int wakeLayoutId = 0;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SleepTimePicker);
            sleepLayoutId = a.getResourceId(R.styleable.SleepTimePicker_sleepLayoutId, 100);
            wakeLayoutId = a.getResourceId(R.styleable.SleepTimePicker_wakeLayoutId, 101);
            progressColor = a.getColor(R.styleable.SleepTimePicker_progressColor, progressColor);
            progressBackgroundColor = a.getColor(R.styleable.SleepTimePicker_progressBackgroundColor, progressBackgroundColor);
            divisionColor = a.getColor(R.styleable.SleepTimePicker_divisionColor, divisionColor);
            progressStrokeWidth = a.getDimensionPixelSize(R.styleable.SleepTimePicker_progressStrokeWidth, progressStrokeWidth);
            progressBottomShadowSize = a.getDimensionPixelSize(R.styleable.SleepTimePicker_strokeBottomShadowRadius, 0);
            progressTopShadowSize = a.getDimensionPixelSize(R.styleable.SleepTimePicker_strokeTopShadowRadius, 0);
            progressBgStrokeWidth = a.getDimensionPixelSize(R.styleable.SleepTimePicker_progressBgStrokeWidth, progressStrokeWidth);
            strokeBottomShadowColor = a.getColor(R.styleable.SleepTimePicker_strokeBottomShadowColor, progressColor);
            strokeTopShadowColor = a.getColor(R.styleable.SleepTimePicker_strokeTopShadowColor, progressColor);
            labelColor = a.getColor(R.styleable.SleepTimePicker_labelColor, progressColor);
            labelColor = a.getColor(R.styleable.SleepTimePicker_labelColor, progressColor);
            progressStrokeCap = Cap.ROUND;
            a.recycle();
        }

        progressPaint = new Paint();
        progressPaint.setStrokeCap(progressStrokeCap);
        progressPaint.setStrokeWidth((float) progressStrokeWidth);

        progressPaint.setStyle(Style.STROKE);
        progressPaint.setColor(progressColor);
        progressPaint.setAntiAlias(true);
        progressBackgroundPaint = new Paint();
        progressBackgroundPaint.setStyle(Style.STROKE);
        progressBackgroundPaint.setStrokeWidth((float) progressBgStrokeWidth);
        progressBackgroundPaint.setColor(progressBackgroundColor);
        progressBackgroundPaint.setAntiAlias(true);
        float bottomBlurRadius;
        if (progressTopShadowSize > 0) {
            progressTopBlurPaint = new Paint();
            progressTopBlurPaint.setStrokeCap(Cap.ROUND);

            progressTopBlurPaint.setStrokeWidth(BLUR_STROKE_RATIO * (float) (this.progressTopShadowSize + progressStrokeWidth));
            progressTopBlurPaint.setStyle(Style.STROKE);
            progressTopBlurPaint.setAntiAlias(true);
            bottomBlurRadius = BLUR_RADIUS_RATIO * (float) (this.progressTopShadowSize + progressBgStrokeWidth);

            progressTopBlurPaint.setMaskFilter((MaskFilter) (new BlurMaskFilter(bottomBlurRadius, Blur.NORMAL)));

            progressTopBlurPaint.setColor(this.strokeTopShadowColor);
        }

        if (progressBottomShadowSize > 0) {
            progressBottomBlurPaint = new Paint(0);
            progressBottomBlurPaint.setStrokeCap(Cap.ROUND);
            progressBottomBlurPaint.setStrokeWidth(BLUR_STROKE_RATIO * (float) (this.progressBottomShadowSize + progressStrokeWidth));
            progressBottomBlurPaint.setStyle(Style.STROKE);
            progressBottomBlurPaint.setAntiAlias(true);
            bottomBlurRadius = BLUR_RADIUS_RATIO * (float) (this.progressBottomShadowSize + progressBgStrokeWidth);
            progressBottomBlurPaint.setMaskFilter((MaskFilter) (new BlurMaskFilter(bottomBlurRadius, Blur.NORMAL)));
            progressBottomBlurPaint.setColor(this.strokeBottomShadowColor);
        }

        divisionPaint = new Paint(0);
        divisionPaint.setStrokeCap(Cap.BUTT);
        divisionPaint.setStrokeWidth((float) this.divisionWidth);
        divisionPaint.setColor(divisionColor);
        divisionPaint.setStyle(Style.STROKE);
        divisionPaint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, SCALE_LABEL_TEXT_SIZE, getResources().getDisplayMetrics()));
        textPaint.setColor(this.labelColor);
        LayoutInflater inflater = LayoutInflater.from(context);
        sleepLayout = inflater.inflate(sleepLayoutId, (ViewGroup) this, false);
        wakeLayout = inflater.inflate(wakeLayoutId, (ViewGroup) this, false);
        addView(sleepLayout);
        addView(wakeLayout);
        circleBounds = new RectF();
        setLayerType(View.LAYER_TYPE_SOFTWARE, (Paint) null);
        setWillNotDraw(false);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int smallestSide = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(smallestSide, smallestSide);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        calculateBounds(w, h);
        requestLayout();
    }

    public void setEditable(boolean value) {
        isEditable = value;
    }


    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutView(sleepLayout, sleepAngle);
        layoutView(wakeLayout, wakeAngle);
    }

    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        //Intrinsics.checkParameterIsNotNull(ev, "ev");
        if (ev.getAction() == 0) {
            if (this.isTouchOnView(sleepLayout, ev)) {
                this.draggingSleep = true;
                return true;
            }
            if (this.isTouchOnView(wakeLayout, ev)) {
                this.draggingWake = true;
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        // Intrinsics.checkParameterIsNotNull(ev, "ev");
        if(isEditable) {
            float x = ev.getX();
            float y = ev.getY();
            switch (ev.getAction()) {
                case 0:
                    return true;
                case 1:
                case 3:
                    draggingSleep = false;
                    draggingWake = false;
                    mListener.onViewChange();

                    break;
                case 2:
                    float var6 = (float) this.center.y - y;
                    float var7 = x - (float) this.center.x;
                    double touchAngleRad = (double) ((float) Math.atan2((double) var6, (double) var7));
                    double diff;
                    double wakeAngleRad;
                    if (this.draggingSleep) {
                        wakeAngleRad = Math.toRadians(this.sleepAngle);
                        diff = Math.toDegrees(mAssist.angleBetweenVectors(wakeAngleRad, touchAngleRad));
                        sleepAngle = mAssist.to_0_720(this.sleepAngle + diff);
                        requestLayout();
                        notifyChanges();
                        return true;
                    }

                    if (this.draggingWake) {
                        wakeAngleRad = Math.toRadians(this.wakeAngle);
                        diff = Math.toDegrees(mAssist.angleBetweenVectors(wakeAngleRad, touchAngleRad));
                        wakeAngle = mAssist.to_0_720(this.wakeAngle + diff);
                        requestLayout();
                        notifyChanges();
                        return true;
                    }
            }
        }

        return false;
    }

    protected void onDraw(@NonNull Canvas canvas) {
        this.drawProgressBackground(canvas);
        this.drawProgress(canvas);
        this.drawDivisions(canvas);
    }


    private void notifyChanges() {
        LocalTime startTime = computeBedTime();
        LocalTime endTime = computeWakeTime();
        if (mListener != null) {
            mListener.handleUpdates(startTime, endTime);
        }
    }

    private LocalTime computeBedTime() {
        int bedMins = mAssist.snapMinutes(mAssist.angleToMins(this.sleepAngle), this.stepMinutes);
        return LocalTime.of(bedMins / 60 % 24, bedMins % 60);
    }

    private LocalTime computeWakeTime() {
        int wakeMins = mAssist.snapMinutes(mAssist.angleToMins(this.wakeAngle), this.stepMinutes);
        return LocalTime.of(wakeMins / 60 % 24, wakeMins % 60);

    }

    public void storeStartTimePreference() {
        int starttimeMin = mAssist.snapMinutes(mAssist.angleToMins(this.sleepAngle), this.stepMinutes);

        TimePreference.setStartTime(starttimeMin);

    }

    public void storeEndTimePreference() {
        int stopMins = mAssist.snapMinutes(mAssist.angleToMins(this.wakeAngle), this.stepMinutes);
        TimePreference.setStopTime(stopMins);

    }

    private void layoutView(View view, double angle) {
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int halfWidth = measuredWidth / 2;
        int halfHeight = measuredHeight / 2;
        int parentCenterX = this.getWidth() / 2;
        int parentCenterY = this.getHeight() / 2;
        int centerX = (int) (parentCenterX + radius * cos(Math.toRadians(angle)));
        int centerY = (int) (parentCenterY - radius * sin(Math.toRadians(angle)));
        view.layout(centerX - halfWidth, centerY - halfHeight, centerX + halfWidth, centerY + halfHeight);
    }

    private final void calculateBounds(int w, int h) {
        int maxChildHeight = Math.max(sleepLayout.getMeasuredHeight(), wakeLayout.getMeasuredHeight());
        int maxChildWidth = Math.max(sleepLayout.getMeasuredWidth(), wakeLayout.getMeasuredWidth());
        int maxChildSize = Math.max(maxChildWidth, maxChildHeight);
        float width = progressBackgroundPaint.getStrokeWidth() / (float) 2 - (float) (maxChildSize / 2);
        float offset = Math.abs(width);
        width = (float) (w - this.getPaddingStart() - this.getPaddingEnd() - maxChildSize) - offset;
        float height = (float) (h - this.getPaddingTop() - this.getPaddingBottom() - maxChildSize) - offset;
        radius = Math.min(width, height) / 2.0F;
        center = new Point(w / 2, h / 2);
        circleBounds.left = (float) this.center.x - this.radius;
        circleBounds.top = (float) this.center.y - this.radius;
        circleBounds.right = (float) this.center.x + this.radius;
        circleBounds.bottom = (float) this.center.y + this.radius;
    }




    private boolean isTouchOnView(View view, MotionEvent ev) {
        return ev.getX() > (float) view.getLeft() && ev.getX() < (float) view.getRight() && ev.getY() > (float) view.getTop() && ev.getY() < (float) view.getBottom();
    }

    private void drawProgressBackground(Canvas canvas) {

        canvas.drawArc(
                circleBounds, ANGLE_START_PROGRESS_BACKGROUND,
                ANGLE_END_PROGRESS_BACKGROUND,
                false, progressBackgroundPaint);
    }

    private void drawProgress(Canvas canvas) {
        float startAngle = -((float) sleepAngle);
        float sweep = (float) mAssist.to_0_360(sleepAngle - wakeAngle);
        if (progressBottomBlurPaint != null) {
            canvas.drawArc(circleBounds, startAngle, sweep, false, progressBottomBlurPaint);
        }

        if (progressTopBlurPaint != null) {
            canvas.drawArc(circleBounds, startAngle, sweep, false, progressTopBlurPaint);
        }
        canvas.drawArc(circleBounds, startAngle, sweep, false, progressPaint);

    }

    private void drawDivisions(Canvas canvas) {
        int divisionAngle = 360 / hourLabels.size();
        String tmp;
        for (int index = 0; index < hourLabels.size(); index++) {
            int angle = divisionAngle * index - 90;
            double radians = Math.toRadians((double) angle);
            float bgStrokeWidth = progressBackgroundPaint.getStrokeWidth();
            double startX = center.x + (radius - bgStrokeWidth / 2 - divisionOffset) * cos(radians);
            double endX = center.x + (radius - bgStrokeWidth / 2 - divisionOffset - divisionLength) * cos(radians);
            double startY = center.y + (radius - bgStrokeWidth / 2 - divisionOffset) * sin(radians);
            double endY = center.y + (radius - bgStrokeWidth / 2 - divisionOffset - divisionLength) * sin(radians);
            canvas.drawLine((float) startX, (float) startY, (float) endX, (float) endY, divisionPaint);
            tmp = String.valueOf(hourLabels.get(index));
            textPaint.getTextBounds(tmp, 0, tmp.length(), textRect);
            double x = center.x + (radius - bgStrokeWidth / 2 - labelOffset) * cos(radians) - textRect.width() / 2;
            double y = (center.y + (radius - bgStrokeWidth / 2 - labelOffset) * sin(radians) + textRect.height() / 2);
            canvas.drawText(tmp, (float) x, (float) y, textPaint);
        }

    }

    private int dp2px(float dp) {

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(1, dp, metrics);
    }

    public SleepTimePicker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        // Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        center = new Point(0, 0);
        labelColor = -1;
        sleepAngle = 30.0D;
        wakeAngle = 225.0D;

    }


    public SleepTimePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0, 4);
        init(context, attrs);
        center = new Point(0, 0);
        labelColor = -1;
        sleepAngle = 30.0D;
        wakeAngle = 225.0D;
    }

    public SleepTimePicker(@NonNull Context context) {
        super(context, (AttributeSet) null, 0, 6);
    }

    public interface Listener {
        public void handleUpdates(LocalTime startTime, LocalTime endTime);

        public void onViewChange();
    }

}

