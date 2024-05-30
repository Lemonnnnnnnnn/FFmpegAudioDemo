package com.ethan.mediaplaydemo;

import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODER_DONE;
import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODER_INIT_ERROR;
import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODER_READY;
import static com.ethan.libffmpeg.FFMediaPlayer.MSG_DECODING_TIME;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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


import com.ethan.mediaplaydemo.R;
import com.ethan.libffmpeg.FFMediaPlayer;
import com.ethan.player.activity.AudioPlayerActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.MediaPlayer;

import java.text.SimpleDateFormat;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] REQUEST_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final int REQUEST_FILE_CODE = 0x11;
    private LibVLC mLibVLC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLibVLC = new LibVLC(this,null);

        if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    public void choose(View view) {
        // 指定类型
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
            String mVideoPath = PickUtils.getPath(MainActivity.this, uri);
            Log.d(TAG, "onActivityResult: path is " + mVideoPath);
            AudioPlayerActivity.gotoAudioPlayerActivity(MainActivity.this, mVideoPath);

        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}