package com.ethan.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;


public class CustomSeekBar extends AppCompatSeekBar {

    private boolean mIsSeekAble;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSeekAble(boolean isSeekAble) {
        this.mIsSeekAble = isSeekAble;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mIsSeekAble ? super.onTouchEvent(event) : mIsSeekAble;
    }
}
