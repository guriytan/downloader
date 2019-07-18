package org.guriytan.downloader.manager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.callback.DownloadCallback;
import org.guriytan.downloader.callback.ResultCallback;
import org.guriytan.downloader.entity.DownloadTask;
import org.guriytan.downloader.entity.Result;
import org.guriytan.downloader.entity.TaskInfo;
import org.guriytan.downloader.util.FileUtil;
import org.guriytan.downloader.util.HttpUtil;
import org.guriytan.downloader.util.IOUtil;
import org.guriytan.downloader.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.internal.EverythingIsNonNull;

/**
 * 下载管理器，多线程断点续传
 */
@SuppressWarnings("unused")
public class DownloadManager extends Handler {
    private static final String TAG = "DownloadManager";

    private static String DOWNLOAD_PATH = Constant.DOWNLOAD_PATH; // 默认下载目录
    private static int MAXIMUM_DOWNLOAD = Constant.MAXIMUM_DOWNLOAD; // 最大同时下载任务数
    private static int THREAD_NUMBER = Constant.THREAD_NUMBER; // 单个下载任务线程数

    private static DownloadManager downloadManager; // 单例模式

    private static DatabaseManager databaseManager; // 数据库操作类
    private static ResultCallback resultCallback; // 消息回调，默认为空，可通过构造器实现
    private static HttpUtil httpUtil; // okHttp工具类

    private static Map<String, DownloadTask> downloadCache; // 下载任务缓存池，String为taskId，用来操作下载任务
    private static Map<String, TaskInfo> downloadQueue; // 文件下载队列，String为taskId，用来更新数据并操纵下载缓存
    private static LinkedHashMap<TaskInfo, DownloadCallback> waitQueue; // 下载任务等待队列，用taskId作为key，监听器为value

    /**
     * 单例模式
     *
     * @return 下载管理器
     */
    public static DownloadManager getInstance() {
        if (downloadManager == null) {
            synchronized (DownloadManager.class) {
                if (downloadManager == null) {
                    downloadManager = new DownloadManager();
                }
            }
        }
        return downloadManager;
    }

    // 构造器模式，增加消息回调
    public static final class Builder {
        public Builder initialCallback(ResultCallback callback) {
            resultCallback = callback;
            return new Builder();
        }

        public DownloadManager build() {
            return getInstance();
        }
    }

    private DownloadManager() {
        databaseManager = DatabaseManager.getInstance();
        downloadCache = new HashMap<>();
        downloadQueue = new HashMap<>();
        waitQueue = new LinkedHashMap<>();
        httpUtil = HttpUtil.getInstance();
    }

    /**
     * 添加下载任务
     */
    public void add(String url) {
        add(url, null);
    }

    /**
     * 添加下载任务，可重命名文件
     */
    public void add(String url, String fileName) {
        if (StringUtil.isHttpUrl(url)) {
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            TaskInfo info = new TaskInfo();
            info.setUrl(url);

            // 重命名文件
            if (StringUtil.isValid(fileName)) {
                info.setFileName(fileName);
            }

            // 异步创建DownloadTask
            httpUtil.doAsync(request, new RequestCallback(info));
        } else sendMsg(Constant.MSG_ERROR, "添加任务失败");
    }

    /**
     * 下载
     *
     * @param info     任务信息
     * @param callback 下载回调
     */
    public void download(TaskInfo info, DownloadCallback callback) {
        // 若当前下载数大于最大同时下载数则添加至等待队列
        if (downloadQueue.size() >= MAXIMUM_DOWNLOAD) {
            waitQueue.put(info, callback);
            callback.onWait(); // 设置下载回调为等待状态
        } else {
            String key = info.getTaskId();
            // 添加至下载队列
            downloadQueue.put(key, info);
            // 若下载任务缓存池存在则直接开始下载，否则加入至下载任务缓存池
            if (downloadCache.containsKey(key)) {
                downloadCache.get(key).start();
            } else {
                DownloadTask task = new DownloadTask(info, callback, this);
                downloadCache.put(key, task);
                task.start();
            }
        }
    }

    /**
     * 暂停
     *
     * @param info 任务信息
     */
    public void pause(TaskInfo info) {
        String key = info.getTaskId();
        // 若任务信息在等待队列中未开始下载则直接移除，否则设置为暂停
        if (downloadQueue.containsKey(key)) {
            downloadCache.get(key).pause();
        } else if (waitQueue.containsKey(info)) {
            // 设置任务标志
            if (waitQueue.get(info) != null) {
                waitQueue.get(info).onPause(); // 设置下载回调为暂停状态
            }
            waitQueue.remove(info); // 从等待队列中移除
        }
    }

    /**
     * 多任务暂停下载
     */
    public void pause(List<TaskInfo> list) {
        for (TaskInfo info : list) {
            pause(info);
        }
    }

    /**
     * 重新下载，若已绑定监听器则通过监听器读取结果，若未绑定则可以通过返回boolean类型判断结果
     *
     * @param info 任务信息
     */
    public boolean reset(TaskInfo info) {
        String key = info.getTaskId();
        try {
            if (info.getTaskStatus() == Constant.MSG_FINISH) {
                // 若文件已下载完则删除下载好的文件
                File file = new File(info.getFilePath(), info.getFileName());
                FileUtil.cleanFile(file);
            } else if (downloadCache.containsKey(key)) {
                // 若任务在下载队列则设置为重置
                if (downloadQueue.containsKey(key)) {
                    downloadCache.get(key).reset();
                } else { // 否则直接删除
                    downloadCache.get(key).clearAll();
                }
                downloadCache.remove(key);
            } else {
                // 因未生成下载执行类，因此手动生成并删除缓存文件和临时文件
                DownloadTask task = new DownloadTask(info, null, this);
                task.clearAll();
                // 若在等待队列则移除并设置回调信息，否则返回boolean
                if (waitQueue.containsKey(info)) {
                    waitQueue.get(info).onReset();
                    waitQueue.remove(info);
                }
            }
            // 重新设置下载任务
            info.setDownloadSize(0);
            info.setTaskStatus(Constant.MSG_PAUSE);
            info.setThreadNumber(THREAD_NUMBER);
        } catch (Exception e) {
            Log.e(TAG, "重新下载任务失败：" + e.toString());
            sendMsg(Constant.MSG_ERROR, "重新下载失败");
            return false;
        }
        return true;
    }

    /**
     * 删除，若已绑定监听器则通过监听器读取结果，若未绑定则可以通过返回boolean类型判断结果
     */
    public boolean delete(TaskInfo info, boolean deleteFile) {
        String key = info.getTaskId();
        try {
            if (info.getTaskStatus() == Constant.MSG_FINISH && deleteFile) {
                // 若文件已下载完则删除下载好的文件
                File file = new File(info.getFilePath(), info.getFileName());
                FileUtil.cleanFile(file);
            } else if (downloadCache.containsKey(key)) {
                // 若任务在下载队列则设置为删除状态
                if (downloadQueue.containsKey(key)) {
                    downloadCache.get(key).delete();
                } else { // 否则直接删除
                    downloadCache.get(key).clearAll();
                }
                downloadCache.remove(key);
            } else {
                // 因未生成下载执行类，因此手动生成并删除缓存文件和临时文件
                DownloadTask task = new DownloadTask(info, null, this);
                task.clearAll();
                // 若在等待队列则移除并设置回调信息，否则返回boolean
                if (waitQueue.containsKey(info)) {
                    waitQueue.get(info).onDelete();
                    waitQueue.remove(info);
                }
            }
            // 从数据库删除任务信息
            databaseManager.deleteTask(info);
        } catch (Exception e) {
            Log.e(TAG, "删除下载任务失败：" + e.toString());
            sendMsg(Constant.MSG_ERROR, "删除失败");
            return false;
        }
        // 发送删除状态
        sendMsg(Constant.MSG_SUCCESS, "删除成功");
        return true;
    }

    /**
     * 多任务删除下载
     */
    public void delete(List<TaskInfo> list, boolean deleteFile) {
        for (TaskInfo info : list) {
            delete(info, deleteFile);
        }
    }

    /**
     * 获取全部任务信息
     *
     * @return 全部任务信息
     */
    public List<TaskInfo> getAllTasks() {
        return databaseManager.getAllTasks();
    }

    /**
     * 暂停全部任务
     */
    public void pauseAll() {
        for (Map.Entry<String, TaskInfo> task : downloadQueue.entrySet()) {
            downloadCache.get(task.getKey()).pause();
        }
    }

    /**
     * 发送消息
     *
     * @param type 消息类型，1为成功，2为失败，3为更新数据库，4为更新数据库并从等待队列选取第一个进行下载
     * @param msg  消息附件，1、2类型时为消息内容，3、4类型时为需要更新的任务信息识别码
     */
    private void sendMsg(int type, String msg) {
        Message message = new Message();
        message.what = type;
        message.obj = msg;
        sendMessage(message);
    }

    /**
     * 任务回调消息
     *
     * @param msg 消息
     */
    @Override
    public void handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case Constant.MSG_SUCCESS: // 创建/删除成功
                // 回调显示消息
                if (resultCallback != null) {
                    resultCallback.onSuccess(new Result(Constant.MSG_SUCCESS, msg.obj));
                }
                break;
            case Constant.MSG_ERROR: // 创建/删除失败
                // 回调显示消息
                if (resultCallback != null) {
                    resultCallback.onError(new Result(Constant.MSG_ERROR, msg.obj));
                }
                break;
            case Constant.MSG_UPDATE:
                // 更新数据库
                databaseManager.updateTask(downloadQueue.get((String) msg.obj));
                break;
            case Constant.MSG_UPDATE_AND_NEW:
                // 更新数据库
                databaseManager.updateTask(downloadQueue.get((String) msg.obj));
                downloadQueue.remove((String) msg.obj);
                // 从等待队列中取出第一个任务进行下载
                if (waitQueue.size() > 0) {
                    Map.Entry<TaskInfo, DownloadCallback> keyValue = waitQueue.entrySet().iterator().next();
                    download(keyValue.getKey(), keyValue.getValue());
                    waitQueue.remove(keyValue.getKey());
                }
                break;
        }
    }

    /**
     * 异步请求服务器获取资源信息并构建任务信息
     */
    @EverythingIsNonNull
    private class RequestCallback implements okhttp3.Callback {
        private TaskInfo info;

        RequestCallback(TaskInfo info) {
            this.info = info;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "添加任务失败：" + e.getMessage());
            sendMsg(Constant.MSG_ERROR, "添加任务失败");
        }

        @Override
        public void onResponse(Call call, Response response) {
            if (response.code() != 200) {
                IOUtil.close(response.body());
                Log.e(TAG, "添加任务请求长度失败：" + response.code());
                sendMsg(Constant.MSG_ERROR, "添加任务失败");
                return;
            }
            Headers headers = response.headers();
            for (int i = 0; i < headers.size(); i++) {
                Log.d(TAG, headers.name(i) + ":" + headers.value(i));
            }
            Log.d(TAG, "URL:" + response.request().url().toString());
            if (info.getFileName() == null) { // 若文件名未定义则根据响应url生成文件名
                String realName = response.header("Content-Disposition"); //attachment;filename=FileName.txt
                if (realName != null) {
                    info.setFileName(realName.substring(realName.lastIndexOf("filename=") + 9));
                } else {
                    String contentType = response.header("Content-Type"); // MIME-Type
                    if (contentType != null) {
                        String suffix = FileUtil.getSuffix(contentType);// 根据MIME-Type获得文件后缀名
                        if (suffix != null) {
                            String realUrl = response.request().url().toString();
                            String fileName = realUrl.substring(realUrl.lastIndexOf("/") + 1, realUrl.lastIndexOf(suffix));
                            info.setFileName(fileName + suffix);
                        }
                    } else {
                        sendMsg(Constant.MSG_ERROR, "下载文件不支持");
                        return;
                    }
                }
            }
            String contentLength = response.header("Content-Length");
            if (contentLength == null) {
                IOUtil.close(response.body());
                Log.e(TAG, "添加任务失败：contentLength == -1");
                sendMsg(Constant.MSG_ERROR, "添加任务失败");
                return;
            }
            info.setFileSize(Long.valueOf(contentLength)); // 设置资源大小
            info.setTaskStatus(Constant.MSG_PAUSE); // 设置任务状态为正常
            try {
                commit();
            } catch (IOException e) {
                Log.e(TAG, "添加任务失败：" + e.getMessage());
                sendMsg(Constant.MSG_ERROR, "添加任务失败");
            }
        }

        /**
         * 提交数据库事务
         *
         * @throws IOException 文件异常
         */
        private synchronized void commit() throws IOException {
            info.setDate(new Date()); // 设置日期
            info.setFilePath(DOWNLOAD_PATH); // 设置下载路径
            info.setTaskId(StringUtil.generateTaskId(info)); // 设置任务识别码
            info.setThreadNumber(THREAD_NUMBER);
            TaskInfo result = databaseManager.getTask(info.getTaskId());
            if (result == null) {
                // 若文件系统有相同文件则将文件名加一
                File file = new File(info.getFilePath(), info.getFileName());
                int dotIndex = info.getFileName().lastIndexOf(".");
                if (dotIndex != -1) {
                    String prefix = info.getFileName().substring(0, dotIndex), suffix = info.getFileName().substring(dotIndex);
                    for (int i = 1; file.exists() && i < Integer.MAX_VALUE; i++) {
                        info.setFileName(prefix + '(' + i + ')' + suffix);
                        info.setTaskId(StringUtil.generateTaskId(info));
                        file = new File(info.getFilePath(), info.getFileName());
                    }
                    // 在本地创建一个与资源同样大小的文件来占位
                    File mTmpFile = new File(info.getFilePath(), info.getFileName() + ".tmp");
                    FileUtil.createFile(mTmpFile);
                    RandomAccessFile tmpAccessFile = new RandomAccessFile(mTmpFile, "rw");
                    tmpAccessFile.setLength(info.getFileSize()); // 设置资源大小
                    IOUtil.close(tmpAccessFile);
                    // 提交数据库
                    databaseManager.addTask(info);
                    sendMsg(Constant.MSG_SUCCESS, "添加任务成功");
                } else {
                    sendMsg(Constant.MSG_ERROR, "添加任务失败");
                }
            } else {
                sendMsg(Constant.MSG_ERROR, "任务已存在");
            }
        }
    }

    /**
     * 设置默认下载目录
     *
     * @param downloadPath 默认下载目录
     */
    public static void setDownloadPath(String downloadPath) {
        DOWNLOAD_PATH = downloadPath;
    }

    /**
     * 设置最大同时下载任务数
     *
     * @param maximumDownload 最大同时下载任务数
     */
    public static void setMaximumDownload(int maximumDownload) {
        MAXIMUM_DOWNLOAD = maximumDownload;
    }

    /**
     * 设置单个下载任务线程数
     *
     * @param threadNumber 单个下载任务线程数
     */
    public static void setThreadNumber(int threadNumber) {
        THREAD_NUMBER = threadNumber;
    }

    /**
     * 获得单个下载任务线程数
     *
     * @return 单个下载任务线程数
     */
    public static int getThreadNumber() {
        return THREAD_NUMBER;
    }

    /**
     * 获得默认下载目录
     *
     * @return 默认下载目录
     */
    public static String getDownloadPath() {
        return DOWNLOAD_PATH;
    }

    /**
     * 获得最大同时下载任务数
     *
     * @return 最大同时下载任务数
     */
    public static int getMaximumDownload() {
        return MAXIMUM_DOWNLOAD;
    }
}
