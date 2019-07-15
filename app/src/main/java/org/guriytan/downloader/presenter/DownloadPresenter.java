package org.guriytan.downloader.presenter;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.guriytan.downloader.Constant;
import org.guriytan.downloader.R;
import org.guriytan.downloader.adapter.DownloadAdapter;
import org.guriytan.downloader.entity.DownloadTask;
import org.guriytan.downloader.entity.MessageEvent;
import org.guriytan.downloader.performer.DatabasePerformer;
import org.guriytan.downloader.performer.DownloadManager;
import org.guriytan.downloader.util.AppTools;
import org.guriytan.downloader.util.NetUtil;
import org.guriytan.downloader.util.StringUtil;

import java.io.File;
import java.util.List;

public class DownloadPresenter {
    private static DownloadPresenter downloadPresenter;
    private DatabasePerformer databasePerformer;
    private DownloadManager downloadManager;

    private DownloadPresenter() {
        databasePerformer = DatabasePerformer.getInstance();
        downloadManager = DownloadManager.getInstance();
    }

    public static DownloadPresenter getInstance() {
        if (downloadPresenter == null) {
            synchronized (DownloadPresenter.class) {
                if (downloadPresenter == null) {
                    downloadPresenter = new DownloadPresenter();
                }
            }
        }
        return downloadPresenter;
    }

    public List<DownloadTask> getAllTasks() {
        return databasePerformer.getAllTasks();
    }

    public void addTask(DownloadTask task) {
        DownloadTask result = databasePerformer.getTask(task.getTaskId());
        File file = new File(task.getFilePath(), task.getFileName());
        int dotIndex = task.getFileName().lastIndexOf(".");
        if (dotIndex != -1) {
            String prefix = task.getFileName().substring(0, dotIndex), suffix = task.getFileName().substring(dotIndex);
            for (int i = 1; (file.exists() || result != null) && i < Integer.MAX_VALUE; i++) {
                task.setFileName(prefix + '(' + i + ')' + suffix);
                task.setTaskId(StringUtil.generateTaskId(task));
                file = new File(task.getFilePath(), task.getFileName());
                result = databasePerformer.getTask(task.getTaskId());
            }
            databasePerformer.addTask(task);
            EventBus.getDefault().post(new MessageEvent(Constant.SUCCESS_ALERT, R.string.create_success, task));
        } else {
            EventBus.getDefault().post(new MessageEvent(Constant.ERROR_ALERT, R.string.create_fail));
        }
    }

    public void startTask(DownloadTask task, DownloadAdapter downloadAdapter, Context context) {
        int netType = NetUtil.getNetType(context);
        if (netType == Constant.NET_TYPE_UNKNOWN) {
            EventBus.getDefault().post(new MessageEvent(Constant.ERROR_ALERT, R.string.check_net));
        } else if (!AppTools.allowMobileNet() && netType == Constant.NET_TYPE_MOBILE) {
            EventBus.getDefault().post(new MessageEvent(Constant.ERROR_ALERT, R.string.check_mobile_net));
        } else {
            downloadManager.download(task, downloadAdapter);
        }
    }

    public void stopTask(DownloadTask task) {
        task.setTaskStatus(Constant.DOWNLOAD_STOP);
        downloadManager.cancel(task);
    }

    public void deleteTask(DownloadTask task, Boolean deleteFile) {
        if (task.getTaskStatus() == Constant.DOWNLOAD_ING || task.getTaskStatus() == Constant.DOWNLOAD_WAIT) {
            stopTask(task);
        }
        boolean flag = databasePerformer.deleteTask(task);
        if (deleteFile) {
            File file = new File(task.getFilePath(), task.getFileName());
            flag |= file.delete();
        }
        // 注销监听器
        if (flag) {
            EventBus.getDefault().post(new MessageEvent(Constant.SUCCESS_ALERT, R.string.delete_success));
        } else {
            EventBus.getDefault().post(new MessageEvent(Constant.ERROR_ALERT, R.string.delete_fail));
        }
    }
}
