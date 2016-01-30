package com.sample.androidsampleapp.image;


import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * MemoryCache is responsible for caching the Bitmap to optimize the performance of ListView.
 */
public class MemoryCache {
    private Map<String, SoftReference<Bitmap>> cache = Collections.synchronizedMap(new HashMap<String,
            SoftReference<Bitmap>>(10));

    /**
     * Returns the Bitmap associated with given key url.
     *
     * @param id url of bitmap.
     * @return Bitmap associated with the url.
     */
    public Bitmap get(String id) {
        if (!cache.containsKey(id)) {
            return null;
        }
        SoftReference<Bitmap> ref = cache.get(id);
        return ref.get();
    }

    /**
     * Put the bitmap to cache with url as a key.
     *
     * @param id     url of bitmap.
     * @param bitmap Bitmap downloaded from url.
     */
    public void put(String id, Bitmap bitmap) {
        cache.put(id, new SoftReference<>(bitmap));
    }

    /**
     * Clears the bitmap cache.
     */
    public void clear() {
        cache.clear();
    }
}