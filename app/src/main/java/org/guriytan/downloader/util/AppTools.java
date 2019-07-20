package org.guriytan.downloader.util;

import android.content.SharedPreferences;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.application.App;

/**
 * APP操作辅助类
 */
public class AppTools {
    private static SharedPreferences sharedPreferences = App.getSharedPreferences();
    private static SharedPreferences.Editor editor = sharedPreferences.edit();

    /**
     * 获取全局设置的下载路径
     *
     * @return 下载路径
     */
    public static String getDownloadPath() {
        return sharedPreferences.getString(Constant.DOWNLOAD_PATH_KEY, Constant.DOWNLOAD_PATH);
    }

    /**
     * 设置全局设置的下载路径
     *
     * @param path 全局设置的下载路径
     */
    public static void setDownloadPath(String path) {
        editor.putString(Constant.DOWNLOAD_PATH_KEY, path);
        editor.apply();
    }

    /**
     * 获取全局设置的同时下载数
     *
     * @return 同时下载数
     */
    public static int getMaximumDownload() {
        return sharedPreferences.getInt(Constant.MAXIMUM_DOWNLOAD_KEY, Constant.MAXIMUM_DOWNLOAD);
    }

    /**
     * 设置全局设置的同时下载数
     *
     * @param maximumDownload 同时下载数
     */
    public static void setMaximumDownload(int maximumDownload) {
        editor.putInt(Constant.MAXIMUM_DOWNLOAD_KEY, maximumDownload);
        editor.apply();
    }

    /**
     * 获取全局设置的下载任务线程数
     *
     * @return 下载任务线程数
     */
    public static int getThreadNumber() {
        return sharedPreferences.getInt(Constant.THREAD_NUMBER_KEY, Constant.THREAD_NUMBER);
    }

    /**
     * 设置全局设置的下载任务线程数
     *
     * @param threadNumber 下载任务线程数
     */
    public static void setThreadNumber(int threadNumber) {
        editor.putInt(Constant.THREAD_NUMBER_KEY, threadNumber);
        editor.apply();
    }

    /**
     * 获取全局设置的是否允许移动网络下载
     *
     * @return 是否允许移动网络下载
     */
    public static boolean isAllowMobileNet() {
        return sharedPreferences.getBoolean(Constant.ALLOW_MOBILE_NET_KEY, Constant.ALLOW_MOBILE_NET);
    }

    /**
     * 设置是否允许移动网络下载
     *
     * @param isAllow 是否允许移动网络下载
     */
    public static void setAllowMobileNet(boolean isAllow) {
        editor.putBoolean(Constant.ALLOW_MOBILE_NET_KEY, isAllow);
        editor.apply();
    }
}
