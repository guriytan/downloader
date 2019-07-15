package org.guriytan.downloader.performer;

import org.guriytan.downloader.application.App;
import org.guriytan.downloader.dao.DownloadTaskDao;
import org.guriytan.downloader.entity.DownloadTask;

import java.util.List;

public class DatabasePerformer {
    private DownloadTaskDao taskDao;
    private static DatabasePerformer databasePerformer;

    private DatabasePerformer() {
        taskDao = App.getDownloadTaskDao();
    }

    public static DatabasePerformer getInstance() {
        if (databasePerformer == null) {
            synchronized (DatabasePerformer.class) {
                databasePerformer = new DatabasePerformer();
            }
        }
        return databasePerformer;
    }

    public List<DownloadTask> getAllTasks() {
        taskDao.detachAll();
        return taskDao.loadAll();
    }

    public boolean deleteTask(DownloadTask task) {
        try {
            taskDao.delete(task);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    void updateTask(DownloadTask task) {
        taskDao.update(task);
    }

    public void addTask(DownloadTask task) {
        taskDao.insert(task);
    }

    public DownloadTask getTask(String taskId) {
        return taskDao.queryBuilder().where(DownloadTaskDao.Properties.TaskId.eq(taskId)).unique();
    }
}
