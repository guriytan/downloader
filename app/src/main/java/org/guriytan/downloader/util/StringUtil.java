package org.guriytan.downloader.util;

import org.guriytan.downloader.entity.DownloadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    // 文件名不能存在的字符
    private static Pattern pattern = Pattern.compile("[\\\\/:\\*\\?\\\"<>\\|]");

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

    // 生成任务id
    public static String generateTaskId(DownloadTask task) {
        String absolutePath = task.getFilePath() + task.getFileName();
        return MD5Util.MD5Encode(absolutePath);
    }

    /**
     * 过滤文件名不能存在的字符
     */
    public static String filterString(String filename) {
        Matcher matcher = pattern.matcher(filename);
        filename = matcher.replaceAll("");
        return filename;
    }
}
