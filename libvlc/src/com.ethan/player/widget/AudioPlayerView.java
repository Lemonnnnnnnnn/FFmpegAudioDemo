package com.ethan.player.widget;


import static com.ethan.player.util.VlcUtils.millisToString;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ethan.player.control.AudioPlayerControl;
import com.ethan.player.listener.VlcListener;

import org.videolan.R;

public class AudioPlayerView extends LinearLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, VlcListener.OnChangeListener, Handler.Callback {

    private static final int SHOW_PROGRESS = 0;
    private static final int SHOW_LENGTH = 1;
    private static final int PLAYCONTROL_ERROR = 3;

    private Context mContext;
    private ImageView mIsPlay;
    private TextView mStartTime;
    private CustomSeekBar mSeekBar;
    private TextView mEndTime;
    private boolean mPlayState = false;
    private AudioPlayerControl mAudioPlayerControl;
    private Handler mHandler;

    public AudioPlayerView(Context context) {
        super(context);
        init(context);
    }

    public AudioPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AudioPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        initView();
        regEvent();
        setPlayOrPause(false);
        setSeekAble(true);
    }

    public void setPath(String path) {
        mAudioPlayerControl = new AudioPlayerControl(mContext,path);
        mAudioPlayerControl.setOnChangeListener(this);
        mHandler = new Handler(this);
        mHandler.sendEmptyMessage(SHOW_LENGTH);
        mAudioPlayerControl.start();
    }

    private void setSeekAble(boolean isSeekAble) {
        if(mSeekBar != null) {
            mSeekBar.setSeekAble(isSeekAble);
        }
    }

    private void regEvent() {
        mIsPlay.setOnClickListener(this);
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

    public boolean getPlayState() {
        return mPlayState;
    }

    public void stop() {
       onEnd();
    }

    public void start() {
        if(mAudioPlayerControl != null) {
            mAudioPlayerControl.play();
            setPlayOrPause(true);
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    }

    public void destroy() {
        if(mAudioPlayerControl != null) {
            mAudioPlayerControl.release();
        }
    }

    public void pause() {
        if(mAudioPlayerControl != null) {
            mAudioPlayerControl.pause();
            setPlayOrPause(false);
            mHandler.removeMessages(SHOW_PROGRESS);
        }
    }

    private void setAudioCurrentTime(String time) {
        mStartTime.setText(time);
    }

    private void setAudioDuration(String time) {
        mEndTime.setText(time);
    }

    private void setMax(int max) {
        mSeekBar.setMax(max);
    }

    private void setProcess(int process) {
        mSeekBar.setProgress(process);
    }


    private void setPlayOrPause(boolean isPlay) {
        if(mIsPlay != null) {
            mIsPlay.setImageResource(isPlay ? R.drawable.audio_pause : R.drawable.audio_play);
            this.mPlayState = isPlay;
        }
    }

    @Override
    public void onClick(View v) {
        setPlayOrPause(!mPlayState);
        if(mPlayState) {
            mAudioPlayerControl.play();
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        } else {
            mAudioPlayerControl.pause();
            mHandler.removeMessages(SHOW_PROGRESS);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && mAudioPlayerControl.isSeekAble()) {
            mAudioPlayerControl.seekTo(progress);
            setOverlayProgress();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPrepared() {

    }

    @Override
    public void onBufferChanged(int buffer) {

    }

    @Override
    public void onLoadComplete() {

    }

    @Override
    public void onError() {
        Toast.makeText(mContext, "音频播放出错", Toast.LENGTH_LONG);
    }

    @Override
    public void onEnd() {
        mAudioPlayerControl.pause();
        mAudioPlayerControl.stop();
        setPlayOrPause(false);
        setProcess(0);
        setAudioCurrentTime("00:00");
        mHandler.removeMessages(SHOW_PROGRESS);
    }

    @Override
    public void onCurrentTimeUpdate(int time) {

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS:
                setOverlayProgress();
                mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 500);
                break;
            case SHOW_LENGTH:
                getAudioLength();
                break;
            case PLAYCONTROL_ERROR:
                String tip = (String) msg.obj;
                Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return false;
    }

    private void setOverlayProgress() {
        if (mAudioPlayerControl == null) {
            return ;
        }
        long time = mAudioPlayerControl.getCurrentPosition();
        long length =  mAudioPlayerControl.getLength();

        setMax((int)length);
        setProcess((int) time);
        if (time >= 0) {
           setAudioCurrentTime(millisToString(time, false));
        }
        if (length >= 0) {
            setAudioDuration(millisToString(length, false));
        }
    }

    private void getAudioLength() {
        long length =  mAudioPlayerControl.getLength();
        long time = mAudioPlayerControl.getCurrentPosition();

        if (time >= 0) {
            setAudioCurrentTime(millisToString(time, false));
        }
        if(length >= 0) {
            setAudioDuration(millisToString(length, false));
            mAudioPlayerControl.stop();
        }

    }
}
