package org.guriytan.downloader.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.irozon.sneaker.Sneaker;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.R;
import org.guriytan.downloader.application.App;
import org.guriytan.downloader.entity.DownloadTask;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件操作辅助类
 */
public class AppTools {
    // 文件名不能存在的字符
    private static Pattern pattern = Pattern.compile("[\\\\/:\\*\\?\\\"<>\\|]");

    /**
     * 过滤文件名不能存在的字符
     */
    public static String filterString(String filename) {
        Matcher matcher = pattern.matcher(filename);
        filename = matcher.replaceAll("");
        return filename;
    }

    /**
     * 获取SD路径
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 将long转换为储存单位
     */
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f M" : "%.1f M", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f K" : "%.1f K", f);
        } else
            return String.format("%d B", size);
    }

    // 转换秒为时间
    public static String formatFromSecond(int totalSecond) {
        int hour = totalSecond / 3600;
        int min = (totalSecond % 3600) / 60;
        int second = totalSecond % 60;

        String fortmatStr = "";
        if (hour > 0) {
            fortmatStr = String.format("%02d:%02d:%02d", hour, min, second);
        } else {
            fortmatStr = String.format("%02d:%02d", min, second);
        }

        return fortmatStr;
    }

    // 创建文件夹
    public static void mkdirs(String configPath) {
        File file = new File(configPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    // 判断文件夹是否存在
    public static boolean exists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    // 提示弹窗
    public static void alert(Activity activity, String msg, int msgType) {
        if (Constant.ERROR_ALERT == msgType) {
            Sneaker.with(activity)
                    .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_error, R.color.white, false)
                    .sneak(R.color.colorAccent);
        } else if (Constant.SUCCESS_ALERT == msgType) {
            Sneaker.with(activity)
                    .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_success, R.color.white, false)
                    .sneak(R.color.success);
        } else if (Constant.WARNING_ALERT == msgType) {
            Sneaker.with(activity)
                    .setTitle(activity.getResources().getString(R.string.title_dialog), R.color.white)
                    .setMessage(msg, R.color.white)
                    .setDuration(2000)
                    .autoHide(true)
                    .setIcon(R.drawable.ic_warning, R.color.white, false)
                    .sneak(R.color.warning);
        }
    }

    // 获取当前网络状态
    public static int getNetType() {
        Context context = App.getContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return Constant.NET_TYPE_UNKNOWN;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return Constant.NET_TYPE_UNKNOWN;
        }
        int type = activeNetworkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return Constant.NET_TYPE_WIFI;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            return Constant.NET_TYPE_MOBILE;
        }
        return Constant.NET_TYPE_UNKNOWN;
    }

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

    // 生成任务id
    public static String generateTaskId(DownloadTask task) {
        String absolutePath = task.getFilePath() + task.getFileName();
        return MD5Util.MD5Encode(absolutePath);
    }
}
