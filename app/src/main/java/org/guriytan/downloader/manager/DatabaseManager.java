package org.guriytan.downloader.manager;

import android.util.Log;

import org.guriytan.downloader.application.App;
import org.guriytan.downloader.dao.TaskInfoDao;
import org.guriytan.downloader.entity.TaskInfo;

import java.util.List;

/**
 * 数据库操作类
 */
@SuppressWarnings("unused")
class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    // 实际数据库操作执行类
    private TaskInfoDao taskDao;
    private static DatabaseManager databaseManager;

    private DatabaseManager() {
        taskDao = App.getDownloadTaskDao();
    }

    /**
     * 单例模式
     *
     * @return DatabaseManager 数据库操作类
     */
    static DatabaseManager getInstance() {
        if (databaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (databaseManager == null) {
                    databaseManager = new DatabaseManager();
                }
            }
        }
        return databaseManager;
    }

    /**
     * 添加下载任务
     *
     * @param info 下载任务
     */
    void addTask(TaskInfo info) {
        Log.e(TAG, "添加任务：" + info.toString());
        try {
            taskDao.insert(info);
        } catch (Exception e) {
            Log.e(TAG, "添加任务失败：" + e.getMessage());
            throw new RuntimeException("添加任务失败");
        }
    }

    /**
     * 删除下载任务
     *
     * @param info 下载任务
     */
    void deleteTask(TaskInfo info) {
        Log.e(TAG, "删除任务：" + info.toString());
        try {
            taskDao.delete(info);
        } catch (Exception e) {
            Log.e(TAG, "删除任务失败：" + e.getMessage());
            throw new RuntimeException("删除任务失败");
        }
    }

    /**
     * 更新下载任务
     *
     * @param info 下载任务
     */
    void updateTask(TaskInfo info) {
        Log.e(TAG, "保存任务：" + String.valueOf(info));
        try {
            taskDao.update(info);
        } catch (Exception e) {
            Log.e(TAG, "更新任务失败：" + e.getMessage());
            throw new RuntimeException("更新任务失败");
        }
    }

    /**
     * 获取所有下载任务
     */
    List<TaskInfo> getAllTasks() {
        try {
            taskDao.detachAll();
            return taskDao.queryBuilder().orderDesc(TaskInfoDao.Properties.Id).list();
        } catch (Exception e) {
            Log.e(TAG, "查询所有任务失败：" + e.getMessage());
            throw new RuntimeException("查询所有任务失败");
        }
    }

    /**
     * 根据任务识别码获取任务信息
     *
     * @param taskId 任务识别码
     * @return 任务信息
     */
    TaskInfo getTask(String taskId) {
        return taskDao.queryBuilder().where(TaskInfoDao.Properties.TaskId.eq(taskId)).unique();
    }
}
