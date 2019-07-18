package org.guriytan.downloader.entity;

/**
 * 消息类
 */
@SuppressWarnings("unused")
public class Result {

    // 1为成功，2为失败
    private int type;

    // 消息
    private Object message;

    public Result(int type, Object message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
