package org.guriytan.downloader.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.guriytan.downloader.Constant;
import org.guriytan.downloader.application.App;

public class NetUtil {

    /**
     * 获取当前网络状态
     *
     * @return 返回网络状态码
     */
    public static int getNetType() {
        Context context = App.getContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        context = null;
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
