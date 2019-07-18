package org.guriytan.downloader.callback;

import org.guriytan.downloader.entity.Result;

/**
 * 消息回调
 */
public interface ResultCallback {
    /**
     * 成功信息
     *
     * @param result type为1，表示操作成功
     */
    void onSuccess(Result result);

    /**
     * 错误信息
     *
     * @param result type为2，表示操作失败
     */
    void onError(Result result);
}
