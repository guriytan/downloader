package org.guriytan.downloader.entity;

public class MessageEvent {

    private int msgType;
    private int msg;
    private DownloadTask task;

    public MessageEvent(int msgType, int msg) {
        this.msgType = msgType;
        this.msg = msg;
    }

    public MessageEvent(int msgType, int msg, DownloadTask task) {
        this.msgType = msgType;
        this.msg = msg;
        this.task = task;
    }

    public int getMsgType() {
        return msgType;
    }

    public int getMsg() {
        return msg;
    }

    public DownloadTask getTask() {
        return task;
    }
}
