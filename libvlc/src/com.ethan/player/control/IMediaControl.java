package com.ethan.player.control;

public interface IMediaControl {

    /**
     * 是否正在播放
     * @return
     */
    boolean isPlaying();


    /**
     * 是否能快进
     * @return
     */
    boolean isSeekAble();

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();

    /**
     * 获取播放状态
     * @return
     */
    int getPlayerState();

    /**
     * 获取当前音量大小
     * @return
     */
    int getVolume();

    /**
     * 调节音量
     * @param volume
     * @return
     */
    int setVolume(int volume);

    /**
     * 获取资源总长度
     * @return
     */
    long getLength();

    long getCurrentPosition();

    void start();

    void release();

    void seekTo(int milliSeconds);
}
