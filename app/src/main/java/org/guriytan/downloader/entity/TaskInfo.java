package org.guriytan.downloader.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

import org.greenrobot.greendao.annotation.Generated;

/**
 * 下载任务信息类
 */
@Entity
public class TaskInfo {
    @Id(autoincrement = true)
    private Long id;
    // 任务的识别码，由下载路径以及文件名的MD5组成
    private String taskId;
    // 下载链接
    private String url;
    // 下载路径
    private String filePath;
    // 文件名
    private String fileName;
    // 文件大小
    private long fileSize;
    // 已下载大小
    private long downloadSize;
    // 任务状态（完成，正常，错误）
    private int taskStatus;
    // 任务类型
    private int taskType;
    // 用于下载的线程数
    private int threadNumber;
    // 回显的下载速度
    private long speed;
    // 创建日期
    private Date date;

    @Generated(hash = 2006739888)
    public TaskInfo(Long id, String taskId, String url, String filePath,
                    String fileName, long fileSize, long downloadSize, int taskStatus,
                    int taskType, int threadNumber, long speed, Date date) {
        this.id = id;
        this.taskId = taskId;
        this.url = url;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.downloadSize = downloadSize;
        this.taskStatus = taskStatus;
        this.taskType = taskType;
        this.threadNumber = threadNumber;
        this.speed = speed;
        this.date = date;
    }

    @Generated(hash = 2022720704)
    public TaskInfo() {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "id=" + id +
                ", taskId='" + taskId + '\'' +
                ", url='" + url + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", downloadSize=" + downloadSize +
                ", taskStatus=" + taskStatus +
                ", taskType=" + taskType +
                ", threadNumber=" + threadNumber +
                ", speed=" + speed +
                ", date=" + date +
                '}';
    }
}
