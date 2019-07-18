package org.guriytan.downloader.callback;

/**
 * 下载监听
 */
public interface DownloadCallback {

    /**
     * 完成
     */
    void onFinished();

    /**
     * 更新进度
     */
    void onProgress();

    /**
     * 暂停
     */
    void onPause();

    /**
     * 重新下载
     */
    void onReset();

    /**
     * 等待下载
     */
    void onWait();

    /**
     * 下载失败
     */
    void onFail();

    /**
     * 删除
     */
    void onDelete();
}
