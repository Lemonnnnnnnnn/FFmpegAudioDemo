package com.ethan.player.activity;

import static android.text.TextUtils.isEmpty;

import static com.ethan.player.util.VlcUtils.millisToString;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ethan.player.control.AudioPlayerControl;
import com.ethan.player.listener.VlcListener;
import com.ethan.player.widget.AudioPlayer;

import org.videolan.R;

public class AudioPlayerActivity extends AppCompatActivity implements VlcListener.OnChangeListener, VlcListener.OnAudioChangeListener, Handler.Callback {

    private static final int SHOW_PROGRESS = 0;
    private static final int SHOW_LENGTH = 1;
    private static final int PLAYCONTROL_ERROR = 3;

    private String mCurrentUrl;

    private AudioPlayer mAudioView;

    private AudioPlayerControl mAudioPlayerControl;

    private Handler mHandler;

    private boolean mFirstCome = true;

    private boolean mIsGetTotalLength = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        mCurrentUrl = getIntent().getStringExtra("url");

        initView();
        regEvent(true);
        initData();
    }

    private void initView() {
        mAudioView = (AudioPlayer)findViewById(R.id.audio_player_view);
    }

    private void regEvent(boolean b) {
        if(mAudioView != null) {
            mAudioView.setOnChangeListener(b ? this : null);
        }
    }

    private void initData() {
        mAudioPlayerControl = new AudioPlayerControl(this,mCurrentUrl);
        mAudioPlayerControl.setOnChangeListener(this);
        mHandler = new Handler(this);
        mHandler.sendEmptyMessage(SHOW_LENGTH);
        mAudioPlayerControl.play();
    }

    public static void gotoAudioPlayerActivity(Context mContext, String videoUrl) {
        if(isEmpty(videoUrl)) {
            Toast.makeText(mContext, "音频播放地址有误", Toast.LENGTH_LONG);
            return ;
        }
        Intent intent = new Intent(mContext, AudioPlayerActivity.class);
        intent.putExtra("url", videoUrl);
        mContext.startActivity(intent);
    }

    @Override
    public void onPrepared() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mAudioPlayerControl.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAudioPlayerControl.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mFirstCome) {
            mFirstCome = false;
        } else {
            if(mAudioView.getPlayState()) {
                mAudioPlayerControl.start();
                mHandler.sendEmptyMessage(SHOW_PROGRESS);
            }
        }

    }

    @Override
    public void onBufferChanged(int buffer) {
    }

    @Override
    public void onLoadComplete() {
    }

    @Override
    public void onError() {
        Toast.makeText(this, "播放错误", Toast.LENGTH_LONG);
    }

    @Override
    public void onEnd() {
        mAudioPlayerControl.pause();
        mAudioPlayerControl.stop();
        mAudioView.setPlayOrPause(false);
        mAudioView.setProcess(0);
        mAudioView.setAudioCurrentTime("00:00");
        mHandler.removeMessages(SHOW_PROGRESS);
    }

    @Override
    public void onCurrentTimeUpdate(int time) {

    }

    private void setOverlayProgress() {
        if (mAudioPlayerControl == null) {
            return ;
        }
        long time = mAudioPlayerControl.getCurrentPosition();
        long length =  mAudioPlayerControl.getLength();

        mAudioView.setMax((int)length);
        mAudioView.setProcess((int) time);
        if (time >= 0) {
            mAudioView.setAudioCurrentTime(millisToString(time, false));
        }
        if (length >= 0) {
            mAudioView.setAudioDuration(millisToString(length, false));
        }
    }

    @Override
    public void onClickIsPlay(boolean isPlay) {
        if(isPlay) {
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
            Log.d("AudioPlayerActivity", "onProgressChanged: " + progress);
            mAudioPlayerControl.seekTo(progress);
            setOverlayProgress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        regEvent(false);
        mHandler.removeMessages(SHOW_PROGRESS);
        mAudioPlayerControl.release();
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
                Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                break;
        }

        return false;
    }

    private void getAudioLength() {
        if(mIsGetTotalLength) {
            mHandler.removeMessages(SHOW_LENGTH);
            return ;
        }

        long length =  mAudioPlayerControl.getLength();
        long time = mAudioPlayerControl.getCurrentPosition();

        if (time >= 0) {
            mAudioView.setAudioCurrentTime(millisToString(time, false));
        }
        if(length >= 0) {
            mIsGetTotalLength = true;
            mAudioView.setAudioDuration(millisToString(length, false));
            mAudioPlayerControl.stop();
        }

    }
}
