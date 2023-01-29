package com.child.parent.kidcare.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.child.parent.kidcare.R;



public class EasyFlipView extends FrameLayout {

    public EasyFlipView( Context context) {
        super(context);
        init(null);
    }

    public EasyFlipView( Context context,  AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EasyFlipView( Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private boolean isFlipped=false;
    private View frontView;
    private View backView;
    private Animator enterFlipAnim;
    private Animator exitFlipAnim;
    private void init(AttributeSet attrs) {

        loadAnimation();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(getChildCount()!=2)
            throw new RuntimeException("FlipView should contain two children.");

        backView = getChildAt(0);
        frontView = getChildAt(1);
        //setActiveView();
        changeCameraDistance();
    }


    private void changeCameraDistance() {
        int distance = 15000;
        float scale = getResources().getDisplayMetrics().density * distance;
        frontView.setCameraDistance(scale);
        backView.setCameraDistance(scale);
    }

    private void loadAnimation() {
        enterFlipAnim = AnimatorInflater.loadAnimator(getContext(),R.animator.flip_enter_animator);
        exitFlipAnim = AnimatorInflater.loadAnimator(getContext(),R.animator.flip_exit_animator);
    }

    public void flip()
    {

        if(!isFlipped)
        {
            isFlipped=true;
            enterFlipAnim.setTarget(frontView);
            exitFlipAnim.setTarget(backView);
            enterFlipAnim.start();
            exitFlipAnim.start();

        }
        else
        {
            isFlipped=false;
            enterFlipAnim.setTarget(backView);
            exitFlipAnim.setTarget(frontView);
            enterFlipAnim.start();
            exitFlipAnim.start();

        }

    }



}
