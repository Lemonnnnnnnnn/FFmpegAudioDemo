package com.ethan.mediaplaydemo;

import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODER_DONE;
import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODER_INIT_ERROR;
import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODER_READY;
import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODING_TIME;

import android.Manifest;
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
    private final int REQUEST_FILE_CODE = 0x11;
    private boolean mIsTouch = false;
    private String mVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.wma";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seek_bar);
        tvDuration=findViewById(R.id.tv_duration);
        tvProgress = findViewById(R.id.tv_process);

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

        if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    private void startPlay(){
        ffMediaPlayer = new FFMediaPlayer();
        ffMediaPlayer.addEventCallback(this);
        ffMediaPlayer.init(mVideoPath);
        ffMediaPlayer.play();
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

    public void choose(View view) {
        // 指定类型
        if (ffMediaPlayer != null){
            ffMediaPlayer.stop();
            ffMediaPlayer.unInit();
            ffMediaPlayer = null;
        }
        String[] mimeTypes = {"*/*"};
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        StringBuilder mimeTypesStr = new StringBuilder();
        for (String mimeType : mimeTypes) {
            mimeTypesStr.append(mimeType).append("|");
        }
        intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), REQUEST_FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_FILE_CODE && data != null) {
            Uri uri = data.getData();
            mVideoPath = PickUtils.getPath(FFmpegActivity.this, uri);
            Log.d(TAG, "onActivityResult: path is " + mVideoPath);
            startPlay();

        }
        super.onActivityResult(requestCode, resultCode, data);
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