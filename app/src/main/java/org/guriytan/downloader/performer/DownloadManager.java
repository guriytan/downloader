package org.guriytan.downloader.performer;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.adapter.DownloadAdapter;
import org.guriytan.downloader.entity.DownloadTask;
import org.guriytan.downloader.util.AppTools;
import org.guriytan.downloader.util.IOUtil;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadManager {
    private static DownloadManager INSTANCE = null;
    private Map<String, Call> downCalls; //
    private OkHttpClient mClient; // OKHttpClient;
    private DatabasePerformer databasePerformer;
    private Map<String, Task> waitTask;

    //获得一个单例类
    public static DownloadManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DownloadManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownloadManager();
                }
            }
        }
        return INSTANCE;
    }

    private DownloadManager() {
        downCalls = new HashMap<>();
        mClient = new OkHttpClient.Builder().build();
        databasePerformer = DatabasePerformer.getInstance();
        waitTask = new LinkedHashMap<>();
    }

    /**
     * 开始下载
     */
    public void download(DownloadTask downloadTask, int position, DownloadAdapter downloadAdapter) {
        if (downCalls.size() <= AppTools.getMaximumDownload()) {
            downloadTask.setTaskStatus(Constant.DOWNLOAD_ING);
            databasePerformer.updateTask(downloadTask);
            Observable.just(downloadTask)
                    .filter(s -> !downCalls.containsKey(downloadTask.getTaskId()))//call的map已经有了,就证明正在下载,则这次不下载
                    .flatMap(task -> Observable.create(new DownloadSubscribe(task, position)))//下载
                    .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                    .subscribeOn(Schedulers.io())//在子线程执行
                    .subscribe(downloadAdapter);//添加观察者
        } else {
            downloadTask.setTaskStatus(Constant.DOWNLOAD_WAIT);
            waitTask.put(downloadTask.getTaskId(), new Task(downloadTask, position, downloadAdapter));
            Observable.just(downloadTask)
                    .filter(s -> !waitTask.containsKey(downloadTask.getTaskId()))//call的map已经有了,就证明正在下载,则这次不下载
                    .flatMap(task -> Observable.create(new WaitSubscribe(task, position)))//下载
                    .observeOn(AndroidSchedulers.mainThread())//在主线程回调
                    .subscribeOn(Schedulers.io())//在子线程执行
                    .subscribe(downloadAdapter);//添加观察者
        }
    }

    public void cancel(DownloadTask task) {
        if (downCalls.containsKey(task.getTaskId())) {
            Call call = downCalls.get(task.getTaskId());
            if (call != null) {
                call.cancel();//取消
                task.setTaskStatus(Constant.DOWNLOAD_STOP);
                databasePerformer.updateTask(task);
            }
            downCalls.remove(task.getTaskId());
        } else {
            waitTask.remove(task.getTaskId());
        }
    }

    private void finishCheck() {
        if (waitTask.size() > 0) {
            Task task = waitTask.entrySet().iterator().next().getValue();
            waitTask.remove(task.downloadTask.getTaskId());
            download(task.downloadTask, task.position, task.downloadAdapter);
        }
    }

    // 被观察者
    private class DownloadSubscribe implements ObservableOnSubscribe<Integer> {
        private DownloadTask task;
        private int position;

        DownloadSubscribe(DownloadTask task, int position) {
            this.task = task;
            this.position = position;
        }

        @Override
        public void subscribe(ObservableEmitter<Integer> e) throws Exception {
            long downloadLength = task.getDownloadSize();//已经下载好的长度
            long preLength = downloadLength;
            long contentLength = task.getFileSize();//文件的总长度
            //初始进度信息
            e.onNext(position);

            Request request = new Request.Builder()
                    //确定下载的范围,添加此头,则服务器就可以跳过已经下载好的部分
                    .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                    .url(task.getUrl())
                    .build();
            Call call = mClient.newCall(request);
            downCalls.put(task.getTaskId(), call);//把这个添加到call里,方便取消
            Response response = call.execute();

            InputStream is = null;
            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(task.getFilePath(), task.getFileName()), "rwd");
            FileChannel channelOut = null;

            try {
                channelOut = randomAccessFile.getChannel();
                MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, downloadLength, contentLength);
                is = response.body().byteStream();
                byte[] buffer = new byte[2048];//缓冲数组2kB
                int len = 0;
                long start = System.currentTimeMillis();
                while (!call.isCanceled() && (len = is.read(buffer)) != -1) {
                    mappedBuffer.put(buffer, 0, len);
                    downloadLength += len;
                    task.setDownloadSize(downloadLength);
                    if (System.currentTimeMillis() - start > 1000) {
                        task.setSpeed(downloadLength - preLength);
                        preLength = downloadLength;
                        start = System.currentTimeMillis();
                        databasePerformer.updateTask(task);
                        e.onNext(position);
                    }
                }
                if (len == -1) {
                    task.setTaskStatus(Constant.DOWNLOAD_FAIL);
                }
                if (task.getDownloadSize() >= task.getFileSize()) {
                    task.setTaskStatus(Constant.DOWNLOAD_FINISH);
                }
                databasePerformer.updateTask(task);
                e.onNext(position);
                downCalls.remove(task.getTaskId());
            } finally {
                //关闭IO流
                IOUtil.closeAll(is, channelOut, randomAccessFile);
            }
            e.onComplete();//完成
            finishCheck();
        }
    }

    private class WaitSubscribe implements ObservableOnSubscribe<Integer> {
        private DownloadTask task;
        private int position;

        WaitSubscribe(DownloadTask task, int position) {
            this.task = task;
            this.position = position;
        }

        @Override
        public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
            emitter.onNext(position);
            emitter.onComplete();
        }
    }

    private class Task {
        DownloadTask downloadTask;
        int position;
        DownloadAdapter downloadAdapter;

        Task(DownloadTask downloadTask, int position, DownloadAdapter downloadAdapter) {
            this.downloadTask = downloadTask;
            this.position = position;
            this.downloadAdapter = downloadAdapter;
        }
    }
}
