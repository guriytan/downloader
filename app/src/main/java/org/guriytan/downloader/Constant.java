package org.guriytan.downloader;

import org.guriytan.downloader.util.FileUtil;

/**
 * 常用参数类
 */
public class Constant {
    // SharedPreferences名称
    public static final String APP_SETTING = "app_setting";
    // 单个下载任务线程数
    public static final int THREAD_NUMBER = 4;
    // 默认数据库名称
    public static final String DB_NAME = "info.db";
    // 默认存储路径
    public static final String DOWNLOAD_PATH = FileUtil.getSDCardPath() + "/Download/";
    public static final String DOWNLOAD_PATH_KEY = "DOWNLOAD_PATH";
    // 默认最大同时下载任务数
    public static final int MAXIMUM_DOWNLOAD = 3;
    public static final String MAXIMUM_DOWNLOAD_KEY = "MAXIMUM_DOWNLOAD";
    // 默认不允许使用流量下载
    public static final boolean ALLOW_MOBILE_NET = false;
    public static final String ALLOW_MOBILE_NET_KEY = "ALLOW_MOBILE_NET";
    // 种子文件后缀
    public static final String SUFFIX = "torrent";
    // 下载任务信息状态码
    public static final int MSG_PROGRESS = 1; // 进度
    public static final int MSG_FINISH = 2; // 完成下载
    public static final int MSG_PAUSE = 3; // 暂停
    public static final int MSG_RESET = 4; // 重置
    public static final int MSG_DELETE = 5; // 删除
    public static final int MSG_WAIT = 6; // 等待
    public static final int MSG_FAIL = 7; // 失败
    // 数据库操作状态码
    public static final int MSG_SUCCESS = 1; // 成功
    public static final int MSG_ERROR = 2; // 失败
    public static final int MSG_UPDATE_AND_NEW = 3; // 更新并新起任务
    public static final int MSG_UPDATE = 4; // 需要更新
    public static final int MSG_CREATE = 5; // 创建新任务
    // 网络状态码
    public final static int NET_TYPE_UNKNOWN = 0;
    public final static int NET_TYPE_WIFI = 1;
    public final static int NET_TYPE_MOBILE = 2;
}
