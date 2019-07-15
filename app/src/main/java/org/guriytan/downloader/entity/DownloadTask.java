package org.guriytan.downloader.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 下载任务对象
 */
@Entity
public class DownloadTask {
    @Id(autoincrement = true)
    private Long id;
    private String taskId;
    private String url; // 下载链接
    private String filePath; // 下载路径
    private String fileName; // 文件名
    private long fileSize; // 文件大小
    private long downloadSize; // 已下载大小
    private int taskStatus; // 任务状态
    private int taskType; // 任务类型
    private long speed; // 下载速度
    private Date date; // 创建日期

    @Generated(hash = 278745444)
    public DownloadTask(Long id, String taskId, String url, String filePath,
            String fileName, long fileSize, long downloadSize, int taskStatus,
            int taskType, long speed, Date date) {
        this.id = id;
        this.taskId = taskId;
        this.url = url;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.downloadSize = downloadSize;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
        this.speed = speed;
        this.date = date;
    }

    @Generated(hash = 1999398913)
    public DownloadTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DownloadTask{" +
                "id=" + id +
                ", taskId='" + taskId + '\'' +
                ", url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", downloadSize=" + downloadSize +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", speed=" + speed +
                ", date=" + date +
                '}';
    }
}
