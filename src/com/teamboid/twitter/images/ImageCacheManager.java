package com.teamboid.twitter.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.teamboid.twitter.BoidApp;

/**
 * Implementation of volley's ImageCache interface. This manager tracks the application image loader and cache.
 * <p/>
 * Volley recommends an L1 non-blocking cache which is the default MEMORY CacheType.
 *
 * @author Trey Robinson
 */
public class ImageCacheManager implements ImageCache {

    /**
     * Volley recommends in-memory L1 cache but both a disk and memory cache are provided.
     * Volley includes a L2 disk cache out of the box but you can technically use a disk cache as an L1 cache provided
     * you can live with potential i/o blocking.
     */
    public enum CacheType {
        DISK, MEMORY
    }

    /**
     * Image cache implementation
     */
    private ImageCache mImageCache;
    private Context context;

    /**
     * Initializer for the manager. Must be called prior to use.
     *
     * @param context        application context
     * @param uniqueName     name for the cache location
     * @param cacheSize      max size for the cache
     * @param compressFormat file type compression format.
     * @param quality
     */
    public ImageCacheManager(Context context, String uniqueName, int cacheSize, CompressFormat compressFormat, int quality, CacheType type) {
        this.context = context;
        switch (type) {
            case DISK:
                mImageCache = new DiskLruImageCache(context, uniqueName, cacheSize, compressFormat, quality);
                break;
            default:
                mImageCache = new BitmapLruImageCache(cacheSize);
                break;
        }
    }

    @Override
    public Bitmap getBitmap(String url) {
        try {
            return mImageCache.getBitmap(createKey(url));
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        try {
            mImageCache.putBitmap(createKey(url), bitmap);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }


    /**
     * Executes and image load
     *
     * @param url      location of image
     * @param listener Listener for completion
     */
    public void getImage(String url, ImageListener listener) {
        BoidApp.get(context).getImageLoader().get(url, listener);
    }

    /**
     * Creates a unique cache key based on a url value
     *
     * @param url url to be used in key creation
     * @return cache key value
     */
    private String createKey(String url) {
        return String.valueOf(url.hashCode());
    }


}
