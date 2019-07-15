package org.guriytan.downloader.application;

import android.app.Application;
import android.content.SharedPreferences;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.dao.DaoMaster;
import org.guriytan.downloader.dao.DaoSession;
import org.guriytan.downloader.dao.DownloadTaskDao;

public class App extends Application {
    private static SharedPreferences sharedPreferences;
    private static DownloadTaskDao downloadTaskDao;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(Constant.APP_SETTING, MODE_PRIVATE);

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, Constant.DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        downloadTaskDao = daoSession.getDownloadTaskDao();

    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static DownloadTaskDao getDownloadTaskDao() {
        return downloadTaskDao;
    }
}
