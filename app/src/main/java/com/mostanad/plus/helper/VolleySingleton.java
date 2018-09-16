package com.mostanad.plus.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.util.LruCache;



import java.io.File;

import ir.mono.monolyticsdk.Utils.volley.Network;
import ir.mono.monolyticsdk.Utils.volley.RequestQueue;
import ir.mono.monolyticsdk.Utils.volley.toolbox.BasicNetwork;
import ir.mono.monolyticsdk.Utils.volley.toolbox.HttpStack;
import ir.mono.monolyticsdk.Utils.volley.toolbox.HurlStack;
import ir.mono.monolyticsdk.Utils.volley.toolbox.ImageLoader;

/**
 * از این کلاس استاتیک برای ایجاد صف و کش در  کل نرم افزار استفاده می شود
 */
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private VolleySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(200);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    /**
     * از این تابع برای ایجاد صف درخواست استفاده می شود
     * @return
     * صف درخواست
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            HttpStack stack = new HurlStack();
            Network network = new BasicNetwork(stack);
            final File diskCacheDir = getDiskCacheDir(mCtx, "com.hamavaran.iVote" );
            mRequestQueue= new RequestQueue(new NoExpireDiskBasedCache(diskCacheDir,100*1024*1024), network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    /**
     * یک کلاس بارگزارنده عکس را بر می گرداند
     * @return
     * بارگزارنده عکس
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
    private File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.getExternalStorageDirectory().toString();
        //final String cachePath = context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }
}
