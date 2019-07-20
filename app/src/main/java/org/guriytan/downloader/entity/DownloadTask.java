package org.guriytan.downloader.entity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.callback.DownloadCallback;
import org.guriytan.downloader.util.AppTools;
import org.guriytan.downloader.util.FileUtil;
import org.guriytan.downloader.util.HttpUtil;
import org.guriytan.downloader.util.IOUtil;
import org.guriytan.downloader.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 具体任务下载执行类
 */
public class DownloadTask extends Handler {
    private static final String TAG = "DownloadTask";

    private int THREAD_COUNT;// 线程数

    private volatile boolean hasInitial = false; // 是否已经分配好线程

    private AtomicInteger cancelThread = new AtomicInteger(0); // 子线程取消数量
    private AtomicInteger pauseThread = new AtomicInteger(0); // 子线程暂停数量
    private AtomicInteger finishThread = new AtomicInteger(0); // 子线程完成数量
    private AtomicInteger errorThread = new AtomicInteger(0); // 子线程错误数量

    private HttpUtil httpUtil; // okHttp工具类
    private Handler waitQueueHandler; // 下载管理器监听

    private long fileLength; // 文件大小
    private long[] progresses; // 子线程进度
    private File[] cacheFiles; // 临时存储线程起点文件
    private File tmpFile; // 缓存文件

    private volatile boolean pause; // 是否暂停
    private volatile boolean reset; // 是否重置下载
    private volatile boolean delete; // 是否删除下载

    private TaskInfo info; // 任务信息
    private DownloadCallback callback; // 下载回调监听

    /**
     * 任务管理器初始化数据
     *
     * @param info             初始化任务信息
     * @param callback         任务回调
     * @param waitQueueHandler 等待队列回调
     */
    public DownloadTask(TaskInfo info, DownloadCallback callback, Handler waitQueueHandler) {
        this.info = info;
        this.callback = callback;
        this.waitQueueHandler = waitQueueHandler;
        this.THREAD_COUNT = AppTools.getThreadNumber();
        this.progresses = new long[THREAD_COUNT];
        this.cacheFiles = new File[THREAD_COUNT];
        this.httpUtil = HttpUtil.getInstance();
    }

    private long exitTime = 0;

    /**
     * 任务回调消息
     *
     * @param msg 消息
     */
    @Override
    public void handleMessage(@NonNull Message msg) {
        // 更新进度
        long progress = 0;
        for (long p : this.progresses) {
            progress += p;
        }
        if (msg.what == Constant.MSG_PROGRESS) {
            if (System.currentTimeMillis() - exitTime > 1000) {
                info.setSpeed(progress - info.getDownloadSize());
                info.setDownloadSize(progress);
                // 进度回调
                if (callback != null) callback.onProgress();
                exitTime = System.currentTimeMillis();
                // 通知下载管理器更新数据库
                sendMsg(Constant.MSG_UPDATE, info.getTaskId());
            }
        } else {
            info.setDownloadSize(progress);
            switch (msg.what) {
                case Constant.MSG_PAUSE: // 暂停
                    if (confirmStatus(pauseThread))
                        return;
                    // 设置下载任务暂停标志
                    info.setTaskStatus(Constant.MSG_PAUSE);
                    if (callback != null) callback.onPause();
                    pause = false;
                    break;
                case Constant.MSG_FINISH: // 完成
                    if (confirmStatus(finishThread))
                        return;
                    // 下载完毕后，重命名目标文件名
                    tmpFile.renameTo(new File(info.getFilePath(), info.getFileName()));
                    // 设置下载任务成功标志
                    info.setTaskStatus(Constant.MSG_FINISH);
                    // 删除临时文件
                    clearCache();
                    if (callback != null) callback.onFinished();
                    break;
                case Constant.MSG_RESET: // 重置
                    if (confirmStatus(cancelThread))
                        return;
                    // 设置进度重新开始
                    info.setDownloadSize(0);
                    info.setTaskStatus(Constant.MSG_PAUSE);
                    // 删除所有文件
                    clearAll();
                    if (callback != null) callback.onReset();
                    reset = false;
                    break;
                case Constant.MSG_DELETE: // 删除
                    if (confirmStatus(cancelThread))
                        return;
                    // 删除所有文件
                    clearAll();
                    if (callback != null) callback.onDelete();
                    delete = false;
                    break;
                case Constant.MSG_FAIL: // 失败
                    if (confirmStatus(errorThread))
                        return;
                    // 重置下载线程进度
                    progresses = new long[THREAD_COUNT];
                    // 设置下载任务错误标志
                    info.setTaskStatus(Constant.MSG_FAIL);
                    if (callback != null) callback.onFail();
                    break;
            }
            // 通知下载管理器更新数据库并启动等待队列新下载
            sendMsg(Constant.MSG_UPDATE_AND_NEW, info.getTaskId());
        }
    }

    /**
     * 初始化下载，检查连接是否能连通，并校验文件大小是否一致
     * 若已初始化则直接分配线程进行下载
     */
    public synchronized void start() {
        info.setTaskStatus(Constant.MSG_PROGRESS);
        if (!hasInitial) {
            hasInitial = true;
            httpUtil.getContentLength(info.getUrl(), new okhttp3.Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.code() != 200) {
                        Log.e(TAG, "初始化请求长度失败：response.code() != 200 URL：" + info.getUrl());
                        IOUtil.close(response.body());
                        errorThread.addAndGet(THREAD_COUNT - 1);
                        sendEmptyMessage(Constant.MSG_FAIL);
                        return;
                    }
                    // 获取资源大小
                    fileLength = response.body().contentLength();
                    IOUtil.close(response.body());

                    // 设置文件
                    tmpFile = new File(info.getFilePath(), info.getFileName() + ".tmp");

                    // 判断每次获取的资源大小是否相同，如果不相同则重新下载
                    if (fileLength != info.getFileSize()) {
                        clearAll();
                        info.setDownloadSize(0);
                        FileUtil.createFile(tmpFile);
                        RandomAccessFile tmpAccessFile = new RandomAccessFile(tmpFile, "rw");
                        tmpAccessFile.setLength(fileLength); // 设置资源大小
                        IOUtil.close(tmpAccessFile);
                        info.setFileSize(fileLength);
                    }
                    startThread();
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "初始化请求长度失败 " + e.getMessage() + " URL：" + info.getUrl());
                    errorThread.addAndGet(THREAD_COUNT - 1);
                    sendEmptyMessage(Constant.MSG_FAIL);
                }
            });
        } else {
            startThread();
        }
    }

    /**
     * 分配线程进行下载
     */
    private void startThread() {
        try {
            /* 将下载任务分配给每个线程 */
            long blockSize = fileLength / THREAD_COUNT;// 计算每个线程理论上下载的数量.
            /* 为每个线程配置并分配任务 */
            for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
                long startIndex = threadId * blockSize; // 线程开始下载的位置
                long endIndex = (threadId + 1) * blockSize - 1; // 线程结束下载的位置
                if (threadId == (THREAD_COUNT - 1)) { // 如果是最后一个线程,将剩下的文件全部交给这个线程完成
                    endIndex = fileLength - 1;
                }
                download(startIndex, endIndex, threadId);// 开启线程下载
            }
        } catch (IOException e) {
            Log.e(TAG, "下载任务分配线程失败：" + e.getMessage() + " URL：" + info.getUrl());
            errorThread.addAndGet(THREAD_COUNT - 1);
            sendEmptyMessage(Constant.MSG_FAIL); // 发送错误消息
        }
    }

    /**
     * 启动每个线程的下载任务
     *
     * @param startIndex 文件下载开始起点
     * @param endIndex   文件下载终点
     * @param threadId   线程ID
     * @throws IOException 错误信息
     */
    private void download(final long startIndex, final long endIndex, final int threadId) throws IOException {
        long newStartIndex = startIndex;
        // 加载下载位置缓存文件
        final File cacheFile = new File(info.getFilePath(),
                "thread" + threadId + "-" + info.getFileName() + ".cache");
        cacheFiles[threadId] = cacheFile;
        final RandomAccessFile cacheAccessFile = new RandomAccessFile(cacheFile, "rwd");
        if (cacheFile.exists()) { // 如果文件存在
            String startIndexStr = cacheAccessFile.readLine(); // 读取文件下载开始起点
            try {
                if (StringUtil.isValid(startIndexStr))
                    newStartIndex = Integer.parseInt(startIndexStr); // 重新设置起点
            } catch (NumberFormatException e) {
                Log.e(TAG, "读取起点文件失败：Thread:" + threadId + " URL：" + info.getUrl());
            }
        }
        final long finalStartIndex = newStartIndex;
        // 分段请求网络连接，将数据保存到缓存文件.
        httpUtil.downloadFileByRange(info.getUrl(), finalStartIndex, endIndex, new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() != 206) { // 206：请求部分资源成功码
                    Log.e(TAG, "请求部分资源失败：Thread:" + threadId + " URL：" + info.getUrl());
                    sendEmptyMessage(Constant.MSG_FAIL);
                    return;
                }
                InputStream is = response.body().byteStream();// 获取流
                RandomAccessFile tmpAccessFile = new RandomAccessFile(tmpFile, "rw"); // 获取缓存文件
                tmpAccessFile.seek(finalStartIndex); // 文件写入的开始位置.
                /* 将网络流中的文件写入本地 */
                byte[] buffer = new byte[1024 << 2];
                int length;
                long progress = finalStartIndex; // 记录本次下载数据的大小
                while ((length = is.read(buffer)) > 0) {
                    if (reset) { // 请求重置
                        IOUtil.close(cacheAccessFile, is, response.body());
                        sendEmptyMessage(Constant.MSG_RESET);
                        return;
                    }
                    if (pause) { // 请求暂停
                        IOUtil.close(cacheAccessFile, is, response.body());
                        sendEmptyMessage(Constant.MSG_PAUSE);
                        return;
                    }
                    if (delete) { // 请求删除
                        IOUtil.close(cacheAccessFile, is, response.body());
                        sendEmptyMessage(Constant.MSG_DELETE);
                        return;
                    }
                    // 写入缓存文件
                    tmpAccessFile.write(buffer, 0, length);
                    progress += length;
                    // 将当前现在到的位置保存到文件中
                    cacheAccessFile.seek(0);
                    cacheAccessFile.write((String.valueOf(progress)).getBytes(StandardCharsets.UTF_8));
                    // 发送进度消息
                    DownloadTask.this.progresses[threadId] = progress - startIndex;
                    sendEmptyMessage(Constant.MSG_PROGRESS);
                }
                // 关闭资源
                IOUtil.close(cacheAccessFile, is, response.body());
                // 发送完成消息
                sendEmptyMessage(Constant.MSG_FINISH);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "线程下载失败：Thread:" + threadId + " URL：" + info.getUrl());
                sendEmptyMessage(Constant.MSG_FAIL); // 发送错误消息
            }
        });
    }

    /**
     * 暂停
     */
    public void pause() {
        pause = true;
    }

    /**
     * 取消
     */
    public void reset() {
        reset = true;
    }

    /**
     * 删除
     */
    public void delete() {
        delete = true;
    }

    /**
     * 删除全部临时文件
     */
    public void clearAll() {
        allocateThread();
        FileUtil.cleanFile(tmpFile);
        clearCache();
    }

    /**
     * 删除存放线程起点文件
     */
    private void clearCache() {
        FileUtil.cleanFile(cacheFiles);
    }

    /**
     * 若线程起点文件未初始化则进行初始化操作
     */
    private void allocateThread() {
        tmpFile = new File(info.getFilePath(), info.getFileName() + ".tmp");
        for (int threadId = 0; threadId < THREAD_COUNT; threadId++) {
            if (cacheFiles[threadId] == null) {
                cacheFiles[threadId] = new File(info.getFilePath(),
                        "thread" + threadId + "-" + info.getFileName() + ".cache");
            }
        }
    }

    /**
     * 确认下载状态
     *
     * @param count 下载状态
     * @return 是否达到线程总数
     */
    private boolean confirmStatus(AtomicInteger count) {
        return count.incrementAndGet() % THREAD_COUNT != 0;
    }

    /**
     * 发送消息
     *
     * @param type 消息类型
     * @param msg  消息内容
     */
    private void sendMsg(int type, String msg) {
        Message message = new Message();
        message.what = type;
        message.obj = msg;
        waitQueueHandler.sendMessage(message);
    }
}
