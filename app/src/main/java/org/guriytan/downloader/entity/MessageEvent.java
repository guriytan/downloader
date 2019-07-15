package org.guriytan.downloader.entity;

public class MessageEvent {

    private int msgType;
    private String msg;
    private int addOrRemove;
    private DownloadTask task;
    private int position;

    public MessageEvent(int msgType, String msg) {
        this.msgType = msgType;
        this.msg = msg;
    }

    public MessageEvent(int msgType, String msg, int addOrRemove, DownloadTask task) {
        this.msgType = msgType;
        this.msg = msg;
        this.addOrRemove = addOrRemove;
        this.task = task;
    }

    public MessageEvent(int msgType, String msg, int addOrRemove, int position) {
        this.msgType = msgType;
        this.msg = msg;
        this.addOrRemove = addOrRemove;
        this.position = position;
    }

    public int getMsgType() {
        return msgType;
    }

    public String getMsg() {
        return msg;
    }

    public int getAddOrRemove() {
        return addOrRemove;
    }

    public int getPosition() {
        return position;
    }

    public DownloadTask getTask() {
        return task;
    }
}
