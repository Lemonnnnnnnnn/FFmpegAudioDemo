package com.ethan.mediaplaydemo;

import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODER_DONE;
import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODER_INIT_ERROR;
import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODER_READY;
import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODING_TIME;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ethan.libffmpeg.FFMediaPlayer;
import com.ethan.mediaplaydemo.R;

import java.text.SimpleDateFormat;


public class FFmpegActivity extends AppCompatActivity implements FFMediaPlayer.EventCallback {

    private static final String TAG = FFmpegActivity.class.getSimpleName();
    private TextView tvDuration,tvProgress;
    private SeekBar seekBar;
    private FFMediaPlayer ffMediaPlayer;
    private static final String[] REQUEST_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean mIsTouch = false;
    private String mVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.wma";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg);

        seekBar = findViewById(R.id.seek_bar);
        tvDuration=findViewById(R.id.tv_duration);
        tvProgress = findViewById(R.id.tv_process);
        mVideoPath = getIntent().getStringExtra("path");
        startPlay();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch() called with: progress = [" + seekBar.getProgress() + "]");
                if(ffMediaPlayer != null) {
                    ffMediaPlayer.seekToPosition(seekBar.getProgress());
                    mIsTouch = false;
                }
            }
        });

    }

    private void startPlay(){
        ffMediaPlayer = new FFMediaPlayer();
        ffMediaPlayer.addEventCallback(this);
        ffMediaPlayer.init(mVideoPath);
        ffMediaPlayer.play();
    }

    public static void gotoAudioPlayerActivity(Context mContext, String videoUrl) {
        Intent intent = new Intent(mContext, FFmpegActivity.class);
        intent.putExtra("path", videoUrl);
        mContext.startActivity(intent);
    }

    private void onDecoderReady() {
        int duration = (int) ffMediaPlayer.getDuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(0);
        }
        tvDuration.setText(formatTime(duration));
        seekBar.setMax(duration);
    }

    @Override
    public void onPlayerEvent(int msgType, float msgValue) {
        Log.d(TAG, "onPlayerEvent() called with: msgType = [" + msgType + "], msgValue = [" + msgValue + "]");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (msgType) {
                    case MSG_DECODER_INIT_ERROR:
                        break;
                    case MSG_DECODER_READY:
                        onDecoderReady();
                        break;
                    case MSG_DECODER_DONE:
                        Log.d(TAG, "onPlayerEvent() called with: MSG_DECODER_DONE");
                        break;
                    case MSG_DECODING_TIME:
                        if(!mIsTouch)
                            seekBar.setProgress((int) msgValue);
                        tvProgress.setText(formatTime(msgValue));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private String formatTime(float time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(time);
    }

    protected boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}