package org.guriytan.downloader.util;

import android.os.Environment;
import android.util.Log;

import org.guriytan.downloader.R;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件工具类
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    private final static Map<String, Integer> FILE_TYPE_MAP = new HashMap<>();

    static {
        FILE_TYPE_MAP.put("bmp", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("jpg", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("jpeg", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("png", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("tiff", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("gif", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("pcx", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("tga", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("exif", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("fpx", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("svg", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("psd", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("cdr", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("pcd", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("dxf", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("ufo", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("eps", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("ai", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("raw", R.drawable.ic_image_file);
        FILE_TYPE_MAP.put("wmf", R.drawable.ic_image_file);

        FILE_TYPE_MAP.put("txt", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("doc", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("docx", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("xls", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("xlsx", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("htm", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("html", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("jsp", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("rtf", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("wpd", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("pdf", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("ppt", R.drawable.ic_document_file);
        FILE_TYPE_MAP.put("pptx", R.drawable.ic_document_file);

        FILE_TYPE_MAP.put("mp4", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("avi", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("mov", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("wmv", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("asf", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("navi", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("3gp", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("mkv", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("f4v", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("rmvb", R.drawable.ic_video_file);
        FILE_TYPE_MAP.put("webm", R.drawable.ic_video_file);

        FILE_TYPE_MAP.put("mp3", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("wma", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("wav", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("mod", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("ra", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("cd", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("md", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("aac", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("vqf", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("ape", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("mid", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("ogg", R.drawable.ic_audio_file);
        FILE_TYPE_MAP.put("m4a", R.drawable.ic_audio_file);
    }

    /**
     * 根据文件名获取文件类型
     *
     * @param fileName 文件名
     * @return 文件类型代码，对应图标
     */
    public static int getType(String fileName) {
        int doIndex = fileName.lastIndexOf(".");
        if (doIndex != -1) {
            String fileTyle = fileName.substring(doIndex + 1).toLowerCase();
            if (FILE_TYPE_MAP.containsKey(fileTyle)) {
                return FILE_TYPE_MAP.get(fileTyle);
            }
        }
        return R.drawable.ic_normal_file;
    }

    /**
     * 创建文件夹
     *
     * @param file 创建文件夹
     */
    public static void mkdirs(File file) {
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "创建文件夹" + file.getAbsolutePath() + "失败");
            }
        }
    }

    /**
     * 创建文件
     *
     * @param file 文件对象
     */
    public static void createFile(File file) {
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Log.e(TAG, "创建文件" + file.getAbsolutePath() + "失败");
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "创建文件" + file.getAbsolutePath() + "失败");
        }
    }

    /**
     * 获取SD路径
     *
     * @return SD路径
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 删除文件
     *
     * @param files 文件
     */
    public static void cleanFile(File... files) {
        for (File f : files) {
            if (f != null && f.exists()) {
                if (!f.delete()) {
                    Log.e(TAG, "删除文件" + f.getAbsolutePath() + "失败");
                }
            }
        }
    }
}