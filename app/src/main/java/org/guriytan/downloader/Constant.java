package org.guriytan.downloader;

import org.guriytan.downloader.util.FileUtil;

public class Constant {
    // APP配置、下载文件父路径
    private static final String SDCARD_PATH = FileUtil.getSDCardPath();
    // SharedPreferences名称
    public static final String APP_SETTING = "app_setting";
    // 数据库名称
    public static final String DB_NAME = "data.db";
    // 下载文件默认存储路径
    public static final String DOWNLOAD_PATH = SDCARD_PATH + "/Download/";
    public static final String DOWNLOAD_PATH_KEY = "DOWNLOAD_PATH";
    // 默认同时下载任务数量
    public static final int MAXIMUM_DOWNLOAD = 3;
    public static final String MAXIMUM_DOWNLOAD_KEY = "MAXIMUM_DOWNLOAD";
    // 默认不允许使用流量下载
    public static final boolean ALLOW_MOBILE_NET = false;
    public static final String ALLOW_MOBILE_NET_KEY = "ALLOW_MOBILE_NET";
    // 任务状态码
    public static final int DOWNLOAD_STOP = 1; // 暂停
    public static final int DOWNLOAD_ING = 2; // 正在下载
    public static final int DOWNLOAD_FAIL = 3; // 失败
    public static final int DOWNLOAD_WAIT = 4; // 等待下载
    public static final int DOWNLOAD_FINISH = 5; // 下载完成
    public static final int DOWNLOAD_DELETE = 6; // 文件已删除
    // 信息状态码
    public static final int SUCCESS_ALERT = 1;
    public static final int ERROR_ALERT = 2;
    public static final int WARNING_ALERT = 3;
    // 网络状态码
    public final static int NET_TYPE_UNKNOWN = 0;
    public final static int NET_TYPE_WIFI = 1;
    public final static int NET_TYPE_MOBILE = 2;
    // 创建状态码
    public static final int CREATE_FAIL = -1; // 失败
    public static final int CREATE_SUCCESS = 1; // 成功
}
