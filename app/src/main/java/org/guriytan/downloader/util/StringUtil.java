package org.guriytan.downloader.util;

import org.guriytan.downloader.entity.DownloadTask;
import org.guriytan.downloader.entity.TaskInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 判断字符窗是否为空
     *
     * @param str 字符串
     * @return true表示不为空
     */
    public static boolean isValid(String str) {
        return str != null && !"".equals(str);
    }

    /**
     * 将long转换为储存单位
     *
     * @param size long型存储大小
     * @return 以B为单位的存储单位
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

    /**
     * 转换秒为时间
     *
     * @param totalSecond 秒
     * @return 格式化时间表示
     */
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

    /**
     * 生成任务识别码
     *
     * @param info 任务信息
     * @return 任务识别码
     */
    public static String generateTaskId(TaskInfo info) {
        String absolutePath = info.getFilePath() + info.getFileName();
        return MD5Util.MD5Encode(absolutePath);
    }

    /**
     * 判断字符串是否为URL
     *
     * @param urls 需要判断的String类型url
     * @return true:是URL；false:不是URL
     */
    public static boolean isHttpUrl(String urls) {
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";//设置正则表达式

        Pattern pat = Pattern.compile(regex.trim());//对比
        Matcher mat = pat.matcher(urls.trim());

        return mat.matches();
    }
}
