package org.guriytan.downloader.application;

import android.app.Application;
import android.content.SharedPreferences;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.dao.DaoMaster;
import org.guriytan.downloader.dao.DaoSession;
import org.guriytan.downloader.dao.TaskInfoDao;

/**
 * 全局变量
 */
public class App extends Application {
    private static SharedPreferences sharedPreferences;
    private static TaskInfoDao taskInfoDao;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(Constant.APP_SETTING, MODE_PRIVATE);

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, Constant.DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        taskInfoDao = daoSession.getTaskInfoDao();
    }

    /**
     * 获得全局设置
     *
     * @return SharedPreferences 保存有全局设置
     */
    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    /**
     * 返回数据库Dao
     *
     * @return 数据库Dao
     */
    public static TaskInfoDao getDownloadTaskDao() {
        return taskInfoDao;
    }
}
