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
    private final static Map<String, String> FILE_MIME_TYPE_MAP = new HashMap<>();

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

    static {
        FILE_MIME_TYPE_MAP.put("application/msaccess", ".mdb");
        FILE_MIME_TYPE_MAP.put("application/msword", ".doc");
        FILE_MIME_TYPE_MAP.put("application/pdf", ".pdf");
        FILE_MIME_TYPE_MAP.put("application/postscript", ".ai");
        FILE_MIME_TYPE_MAP.put("application/vnd.android.package-archive", ".apk");
        FILE_MIME_TYPE_MAP.put("application/vnd.iphone", ".ipa");
        FILE_MIME_TYPE_MAP.put("application/vnd.ms-excel", ".xls");
        FILE_MIME_TYPE_MAP.put("application/vnd.ms-pki.seccat", ".cat");
        FILE_MIME_TYPE_MAP.put("application/vnd.ms-powerpoint", ".ppt");
        FILE_MIME_TYPE_MAP.put("application/vnd.rn-realmedia", ".rm");
        FILE_MIME_TYPE_MAP.put("application/vnd.rn-realmedia-vbr", ".rmvb");
        FILE_MIME_TYPE_MAP.put("application/vnd.visio", ".vst");
        FILE_MIME_TYPE_MAP.put("application/x-bittorrent", ".torrent");
        FILE_MIME_TYPE_MAP.put("application/x-bmp", ".bmp");
        FILE_MIME_TYPE_MAP.put("application/x-cdr", ".cdr");
        FILE_MIME_TYPE_MAP.put("application/x-dwf", ".dwf");
        FILE_MIME_TYPE_MAP.put("application/x-dwg", ".dwg");
        FILE_MIME_TYPE_MAP.put("application/x-dxf", ".dxf");
        FILE_MIME_TYPE_MAP.put("application/x-emf", ".emf");
        FILE_MIME_TYPE_MAP.put("application/x-epi", ".epi");
        FILE_MIME_TYPE_MAP.put("application/x-ico", ".ico");
        FILE_MIME_TYPE_MAP.put("application/x-igs", ".igs");
        FILE_MIME_TYPE_MAP.put("application/x-img", ".img");
        FILE_MIME_TYPE_MAP.put("application/x-javascript", ".js");
        FILE_MIME_TYPE_MAP.put("application/x-jpg", ".jpg");
        FILE_MIME_TYPE_MAP.put("application/x-latex", ".latex");
        FILE_MIME_TYPE_MAP.put("application/x-mdb", ".mdb");
        FILE_MIME_TYPE_MAP.put("application/x-msdownload", ".exe");
        FILE_MIME_TYPE_MAP.put("application/x-netcdf", ".cdf");
        FILE_MIME_TYPE_MAP.put("application/x-png", ".png");
        FILE_MIME_TYPE_MAP.put("application/x-ppt", ".ppt");
        FILE_MIME_TYPE_MAP.put("application/x-ps", ".eps");
        FILE_MIME_TYPE_MAP.put("application/x-sam", ".sam");
        FILE_MIME_TYPE_MAP.put("application/x-sat", ".sat");
        FILE_MIME_TYPE_MAP.put("application/x-shockwave-flash", ".swf");
        FILE_MIME_TYPE_MAP.put("application/x-tif", ".tif");
        FILE_MIME_TYPE_MAP.put("application/x-vsd", ".vsd");
        FILE_MIME_TYPE_MAP.put("application/x-vst", ".vst");
        FILE_MIME_TYPE_MAP.put("application/x-xls", ".xls");
        FILE_MIME_TYPE_MAP.put("audio/aiff", ".aiff");
        FILE_MIME_TYPE_MAP.put("audio/mid", ".mid");
        FILE_MIME_TYPE_MAP.put("audio/mp3", ".mp3");
        FILE_MIME_TYPE_MAP.put("audio/wav", ".wav");
        FILE_MIME_TYPE_MAP.put("audio/x-ms-wma", ".wma");
        FILE_MIME_TYPE_MAP.put("audio/x-pn-realaudio-plugin", ".rpm");
        FILE_MIME_TYPE_MAP.put("image/gif", ".gif");
        FILE_MIME_TYPE_MAP.put("image/jpeg", ".jpg");
        FILE_MIME_TYPE_MAP.put("image/png", ".png");
        FILE_MIME_TYPE_MAP.put("image/tiff", ".tiff");
        FILE_MIME_TYPE_MAP.put("image/x-icon", ".ico");
        FILE_MIME_TYPE_MAP.put("java/*", ".java");
        FILE_MIME_TYPE_MAP.put("Model/vnd.dwf", ".dwf");
        FILE_MIME_TYPE_MAP.put("text/css", ".css");
        FILE_MIME_TYPE_MAP.put("text/html", ".html");
        FILE_MIME_TYPE_MAP.put("text/plain", ".txt");
        FILE_MIME_TYPE_MAP.put("text/xml", ".svg");
        FILE_MIME_TYPE_MAP.put("video/avi", ".avi");
        FILE_MIME_TYPE_MAP.put("video/mpeg4", ".mp4");
        FILE_MIME_TYPE_MAP.put("video/mpg", ".mpeg");
        FILE_MIME_TYPE_MAP.put("video/x-mpg", ".mpa");
        FILE_MIME_TYPE_MAP.put("video/x-ms-asf", ".asf");
        FILE_MIME_TYPE_MAP.put("video/x-ms-wm", ".wm");
        FILE_MIME_TYPE_MAP.put("video/x-ms-wmv", ".wmv");
        FILE_MIME_TYPE_MAP.put("video/x-ms-wmx", ".wmx");
        FILE_MIME_TYPE_MAP.put("video/x-sgi-movie", ".movie");
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
     * 根据Content-Type返回文件类型
     *
     * @param type Content-Type
     * @return 文件后缀名
     */
    public static String getSuffix(String type) {
        if (FILE_MIME_TYPE_MAP.containsKey(type)) {
            return FILE_MIME_TYPE_MAP.get(type);
        }
        return null;
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

    public static String getMIMEType(File file) {
        return "application/vnd.android.package-archive";
    }
}