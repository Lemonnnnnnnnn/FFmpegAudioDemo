package com.ethan.player.model;

import java.io.Serializable;

public class PlayerPara implements Serializable {

    //播放地址 必传
    private String path;

    //标题 不传不进行显示
    private String title;

    //是否能拖动进度条 默认为true
    private boolean isDragSeekBar = true;

    //工具栏显示时 隐藏工具栏时间 单位毫秒 默认 8s
    private int hideOverLayLength = 8000;

    public PlayerPara(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDragSeekBar() {
        return isDragSeekBar;
    }

    public void setDragSeekBar(boolean dragSeekBar) {
        isDragSeekBar = dragSeekBar;
    }

    public int getHideOverLayLength() {
        return hideOverLayLength;
    }

    public void setHideOverLayLength(int hideOverLayLength) {
        this.hideOverLayLength = hideOverLayLength;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
