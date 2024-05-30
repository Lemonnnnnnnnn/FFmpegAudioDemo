package com.ethan.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ethan.player.listener.VlcListener;

import org.videolan.R;

public class AudioPlayer extends LinearLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Context mContext;

    private ImageView mIsPlay;

    private TextView mStartTime;

    private CustomSeekBar mSeekBar;

    private TextView mEndTime;

    private VlcListener.OnAudioChangeListener mChangeListener;

    private boolean mPlayState = false;

    private boolean isTouch = false;

    public AudioPlayer(Context context) {
        super(context);

    }

    public AudioPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
        regEvent();
        setPlayOrPause(false);
    }

    public AudioPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void regEvent() {
        mIsPlay.setOnClickListener(this);
        mSeekBar.setSeekAble(true);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_audio_player, null);
        mIsPlay = (ImageView) view.findViewById(R.id.iv_playrecord);
        mStartTime = (TextView) view.findViewById(R.id.tv_starttime);
        mSeekBar = (CustomSeekBar) view.findViewById(R.id.sb_audio);
        mEndTime = (TextView) view.findViewById(R.id.tv_durtion);
        LayoutParams timeViewLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(view, timeViewLp);
    }

    public void setOnChangeListener(VlcListener.OnAudioChangeListener changeListener) {
        this.mChangeListener = changeListener;
    }

    public void setAudioCurrentTime(String time) {
        mStartTime.setText(time);
    }

    public void setAudioDuration(String time) {
        mEndTime.setText(time);
    }

    public void setMax(int max) {
        mSeekBar.setMax(max);
    }

    public void setProcess(int process) {
        mSeekBar.setProgress(process);
    }

    public boolean getPlayState() {
        return mPlayState;
    }

    public void setPlayOrPause(boolean isPlay) {
        if(mIsPlay != null) {
            mIsPlay.setImageResource(isPlay ? R.drawable.audio_pause : R.drawable.audio_play);
            this.mPlayState = isPlay;
        }
    }

    @Override
    public void onClick(View v) {

        if(mChangeListener != null) {
            setPlayOrPause(!mPlayState);
            mChangeListener.onClickIsPlay(mPlayState);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        Log.d("onProgressChanged", "progress = " + progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d("onStartTrackingTouch", "SeekBar");
        isTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d("onStopTrackingTouch", "SeekBar");
        if (!isTouch){
            if (mChangeListener != null){
                mChangeListener.onProgressChanged(seekBar,seekBar.getProgress(),true);
            }
            isTouch = false;
        }
    }
}
