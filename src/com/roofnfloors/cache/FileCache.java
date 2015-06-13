package com.roofnfloors.cache;

import java.io.File;

import com.roofnfloors.util.Constants;

import android.content.Context;

public class FileCache {
    private static File cacheDir;

    private static class CacheInstanceHolder {
        private static final FileCache INSTANCE = new FileCache();

        private static FileCache getFileCache(Context context) {
            INSTANCE.init(context);
            return INSTANCE;
        }
    }

    public static FileCache getInstance(Context context) {
        return CacheInstanceHolder.getFileCache(context);
    }

    private FileCache() {

    }

    private void init(Context _context) {
        // Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            cacheDir = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    Constants.CACHE_DIR_NAME);
        else
            cacheDir = _context.getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
    }

    public File getFile(String url) {
        // String filename = String.valueOf(url.hashCode());
        String filename = String.valueOf(url);
        File f = new File(cacheDir, filename);
        return f;

    }

    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null)
            return;
        for (File f : files)
            f.delete();
    }
}
