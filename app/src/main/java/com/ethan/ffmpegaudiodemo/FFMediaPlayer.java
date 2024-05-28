package com.ethan.ffmpegaudiodemo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.Surface;

public class FFMediaPlayer {

    static {
        System.loadLibrary("ffmpegaudio");
    }

    private long mNativePlayerHandle = 0;


    public static final int MSG_DECODER_INIT_ERROR      = 0;
    public static final int MSG_DECODER_READY           = 1;
    public static final int MSG_DECODER_DONE            = 2;
    public static final int MSG_DECODING_TIME           = 3;

    private EventCallback mEventCallback = null;

    private AudioTrack mAudioTrack;

    public void createTrack(int sampleRateInHz, int nb_channels) {
        int channelConfig;
        if(nb_channels == 1) {
            channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        } else if(nb_channels == 2) {
            channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
        } else {
            channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        }
        int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
        mAudioTrack.play();
    }

    public void playTrack(byte[] buffer, int lenght) {
        if(mAudioTrack != null) {
            mAudioTrack.write(buffer, 0, lenght);
        }
    }

    public static String GetFFmpegVersion() {
        return native_GetFFmpegVersion();
    }

    public void init(String url) {
        mNativePlayerHandle = native_Init(url);
    }

    public void play() {
        native_Play(mNativePlayerHandle);
    }

    public void pause() {
        native_Pause(mNativePlayerHandle);
    }

    public void seekToPosition(float position) {
        native_SeekToPosition(mNativePlayerHandle, position);
    }

    public float getDuration() {
        return native_getDuration(mNativePlayerHandle);
    }

    public void stop() {
        native_Stop(mNativePlayerHandle);
    }

    public void unInit() {
        native_UnInit(mNativePlayerHandle);
        if(mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
        }
    }

    public void addEventCallback(EventCallback callback) {
        mEventCallback = callback;
    }

    private void playerEventCallback(int msgType, float msgValue) {
        if(mEventCallback != null)
            mEventCallback.onPlayerEvent(msgType, msgValue);
    }

    private static native String native_GetFFmpegVersion();

    private native long native_Init(String url);

    private native void native_Play(long playerHandle);

    private native void native_SeekToPosition(long playerHandle, float position);

    private native void native_Pause(long playerHandle);

    private native void native_Stop(long playerHandle);

    private native void native_UnInit(long playerHandle);

    private native float native_getDuration(long playerHandle);


    public interface EventCallback {
        void onPlayerEvent(int msgType, float msgValue);
    }

}
