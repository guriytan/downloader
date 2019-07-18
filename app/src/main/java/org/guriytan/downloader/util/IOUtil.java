package org.guriytan.downloader.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO工具类
 */
public class IOUtil {
    /**
     * 关闭IO流
     *
     * @param closeables 可关闭对象
     */
    public static void close(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        int length = closeables.length;
        try {
            for (Closeable c : closeables) {
                if (c != null) {
                    c.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < length; i++) {
                closeables[i] = null;
            }
        }
    }
}
