package org.guriytan.downloader.util;

import android.content.SharedPreferences;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.application.App;

/**
 * APP操作辅助类
 */
public class AppTools {

    // 获取全局设置的是否允许移动网络下载
    public static boolean allowMobileNet() {
        SharedPreferences sharedPreferences = App.getSharedPreferences();
        return sharedPreferences.getBoolean(Constant.ALLOW_MOBILE_NET_KEY, Constant.ALLOW_MOBILE_NET);
    }

    // 获取全局设置的同时下载数
    public static int getMaximumDownload() {
        SharedPreferences sharedPreferences = App.getSharedPreferences();
        return sharedPreferences.getInt(Constant.MAXIMUM_DOWNLOAD_KEY, Constant.MAXIMUM_DOWNLOAD);
    }

    // 获取全局设置的同时下载数
    public static String getDownloadPath() {
        SharedPreferences sharedPreferences = App.getSharedPreferences();
        return sharedPreferences.getString(Constant.DOWNLOAD_PATH_KEY, Constant.DOWNLOAD_PATH);
    }
}
