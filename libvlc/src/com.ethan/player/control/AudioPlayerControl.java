package com.ethan.player.control;

import static org.videolan.libvlc.util.VLCUtil.TAG;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ethan.player.listener.VlcListener;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class AudioPlayerControl implements IMediaControl, MediaPlayer.EventListener {


    private final int MSG_PREPARED = 0;
    private final int MSG_COMPLETION = 1;
    private final int MSG_ERROR = 2;
    private final int MSG_BUFFERING_UPDATE = 3;
    private final int MSG_CURRENT_TIME_UPDATE = 4;

    //播放路径
    private String mPath;

    //vlc播放核心控制类
    private LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer;
    private VlcListener.OnChangeListener mChangeListener;

    private boolean mPausable;
    private int mBufferPercentage;
    private Context mContext;


    public AudioPlayerControl(Context context,String path) {
        this.mPath = path;
        mContext = context;
        init(mContext);
    }

    public void setOnChangeListener(VlcListener.OnChangeListener onChangeListener) {
        mChangeListener = onChangeListener;
    }

    private void init(Context context) {
        ArrayList<String> options = new ArrayList<>();
        options.add("--aout=opensles");//音频输出模块opensles模式
        options.add("--audio-time-stretch");
        options.add("-vvv");
        options.add("--no-skip-frames");//关闭 跳过帧
        options.add("--deinterlace=1");
        options.add("--deinterlace-mode=blend");//解除交错模式
        mLibVLC = new LibVLC(context,options);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        Media media;
        if(isLocalPath(mPath)) {
            media = new Media(mLibVLC, mPath);
        } else {
            media = new Media(mLibVLC, Uri.parse(mPath));
        }
        mMediaPlayer.setMedia(media);
        mMediaPlayer.setEventListener(this);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isSeekAble() {
        return mMediaPlayer.isSeekable();
    }

    @Override
    public void play() {
        mMediaPlayer.play();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public int getPlayerState() {
        return mMediaPlayer.getPlayerState();
    }

    @Override
    public int getVolume() {
        return mMediaPlayer.getVolume();
    }

    @Override
    public int setVolume(int volume) {
        return mMediaPlayer.setVolume(volume);
    }

    @Override
    public long getLength() {
        return mMediaPlayer.getLength();
    }

    @Override
    public long getCurrentPosition() {
        long currentPosition;

        if(!mMediaPlayer.isReleased()) {
            currentPosition =  mMediaPlayer.getTime();
        } else {
            currentPosition = 0;
        }

        return currentPosition;
    }

    @Override
    public void start() {
        mMediaPlayer.play();
    }

    @Override
    public void release() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override
    public void seekTo(int milliSeconds) {
        if(mMediaPlayer.isSeekable()) {
            if(milliSeconds > getLength()) {
                milliSeconds = (int)getLength();
            }

            mMediaPlayer.setTime(milliSeconds);
        }
    }

    /**
     * 判断是否为本地路径
     * @return
     */
    private boolean isLocalPath(String path) {
        boolean value = false;
        if(path != null && path.startsWith("/")) {
            value = true;
        }
        return value;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage (Message msg) {
            switch(msg.what) {
                case MSG_PREPARED:
                    if(mChangeListener != null) {
                        mChangeListener.onPrepared();
                    }
                    break;
                case MSG_COMPLETION:
                    if(mChangeListener != null) {
                        mChangeListener.onEnd();
                    }
                    break;
                case MSG_ERROR:
                    if(mChangeListener != null) {
                        mChangeListener.onError();
                    }
                    break;
                case MSG_BUFFERING_UPDATE:
                    if(mChangeListener != null) {
                        mChangeListener.onBufferChanged(mBufferPercentage);
                    }
                    break;
                case MSG_CURRENT_TIME_UPDATE:
                    if(mChangeListener != null) {
                        mChangeListener.onCurrentTimeUpdate((int)getCurrentPosition());
                    }

                    break;
            }
        }
    };


    @Override
    public void onEvent(MediaPlayer.Event event) {
        try {
            mPausable = event.getPausable();
            mBufferPercentage = (int) event.getBuffering();

            mHandler.obtainMessage(MSG_BUFFERING_UPDATE).sendToTarget();
            mHandler.obtainMessage(MSG_CURRENT_TIME_UPDATE).sendToTarget();

            int playerState = mMediaPlayer.getPlayerState();

            switch (playerState) {
                case Media.State.Ended:
                    mHandler.obtainMessage(MSG_COMPLETION).sendToTarget();

                    break;
                case Media.State.Error:
                    mHandler.obtainMessage(MSG_ERROR).sendToTarget();

                    break;
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }
}
