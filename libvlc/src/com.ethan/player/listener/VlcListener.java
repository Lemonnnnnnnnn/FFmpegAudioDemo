package com.ethan.player.listener;

import android.widget.SeekBar;

public class VlcListener {
    public interface OnChangeListener {

        void onPrepared();

        void onBufferChanged(int buffer);

        void onLoadComplete();

        void onError();

        void onEnd();

        void onCurrentTimeUpdate(int time);
    }

    public interface OnAudioChangeListener {

        void onClickIsPlay(boolean isPlay);

        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
    }
}
