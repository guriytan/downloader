package org.guriytan.downloader.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.guriytan.downloader.Constant;

public class NetUtil {

    // 获取当前网络状态
    public static int getNetType(Context context) {
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
}
