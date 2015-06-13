package com.roofnfloors.loader;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import com.example.mapslist.R;
import com.roofnfloors.cache.FileCache;
import com.roofnfloors.cache.MemoryCache;
import com.roofnfloors.util.Utility;

public class ImageLoader {
    private Context mContext;
    private Handler mHandler;

    private Map<ImageView, String> imageViews = Collections
            .synchronizedMap(new WeakHashMap<ImageView, String>());
    private ExecutorService executorService;

    public ImageLoader(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        executorService = Executors.newFixedThreadPool(5);
    }

    public void stopImageLoader() {
        if (null != executorService) {
            executorService.shutdown();
            executorService = null;
        }
    }

    private final int view_pager_default_image = R.drawable.roofnfloor_default;

    public void displayImage(String url, final ImageView imageView) {
        imageViews.put(imageView, url);
        final Bitmap bitmap = MemoryCache.getInstance().get(url);
        if (bitmap != null) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    imageView.setImageBitmap(bitmap);

                }
            });
        } else {
            queuePhoto(url, imageView);
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    imageView.setImageResource(view_pager_default_image);

                }
            });
            // Activity a = (Activity) imageView.getContext();
            // a.runOnUiThread(new Runnable() {
            //
            // @Override
            // public void run() {
            // imageView.setImageResource(stub_id);
            // }
            // });
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        File f = FileCache.getInstance(mContext).getFile(url);

        // from SD cache
        Bitmap b = Utility.decodeFile(f);
        if (b != null)
            return b;

        // from web
        try {
            return Utility.downloadImage(url, mContext);
        } catch (Throwable ex) {
            ex.printStackTrace();
            if (ex instanceof OutOfMemoryError)
                MemoryCache.getInstance().clear();
            return null;
        }
    }

    // Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    private class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            Bitmap bmp = getBitmap(photoToLoad.url);
            MemoryCache.getInstance().put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad))
                return;
            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            mHandler.post(bd);
            // Activity a = (Activity) photoToLoad.imageView.getContext();
            // a.runOnUiThread(bd);
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    // Used to display bitmap in the UI thread
    private class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            if (bitmap != null) {
                // photoToLoad.imageView.setImageBitmap(null);
                photoToLoad.imageView.setImageBitmap(bitmap);
            } else {
                photoToLoad.imageView.setImageResource(view_pager_default_image);
            }
        }
    }

    public void clearCache() {
        MemoryCache.getInstance().clear();
        FileCache.getInstance(mContext).clear();
    }

    public static FileCache getFileCache(Context context) {
        return FileCache.getInstance(context);
    }
}
