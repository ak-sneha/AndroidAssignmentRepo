package com.sample.androidsampleapp.image;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.sample.androidsampleapp.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An Image Loader provides APIs to download a Bitmap from remote location.
 * It also caches the bitmaps for optimization purpose.
 */
public class ImageLoader {

    /**
     * TAG used for debugging purpose.
     */
    private static final String TAG = ImageLoader.class.getSimpleName();
    /**
     * A {@link MemoryCache} used to cache the bitmap.
     */
    private MemoryCache memoryCache = new MemoryCache();
    /**
     * A map of ImageView and associated URL used to identify whether view is reused or not. Used for memory
     * optimization and performance purpose.
     */
    private Map<ImageView, String> mImageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    /**
     * An ExecutorService to download multiple bitmaps asynchronously.
     * An Executor that provides methods to manage termination and methods that can produce a Future for tracking
     * progress of one or more asynchronous tasks.
     */
    private ExecutorService mExecutorService;

    /**
     * Constructor.
     */
    public ImageLoader() {
        mExecutorService = Executors.newFixedThreadPool(5);
    }

    /**
     * Downloads a Bitmap from given url and displays it on the given ImageView.
     *
     * @param url       URL from where to download the Bitmap.
     * @param imageView ImageView on which to display the downloaded Bitmap.
     */
    public void displayImage(String url, ImageView imageView) {
        mImageViews.put(imageView, url);
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha);
            queuePhoto(url, imageView);
        }
    }

    /**
     * Queues the request to download the Bitmap from server to ExecutorService.
     *
     * @param url       URL from where to download the Bitmap.
     * @param imageView ImageView on which to display the downloaded Bitmap.
     */
    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        mExecutorService.submit(new PhotosLoader(p));
    }

    /**
     * Downloads a bitmap from url. If a bitmap associated with the given url is present in cache, this will returned
     * the cached bitmap.
     *
     * @param url URL from where to download the Bitmap.
     * @return A <code>Bitmap</code> downloaded from server or from a cache.
     */
    private Bitmap getBitmap(String url) {

        //Download from server
        try {
            Bitmap bitmap;
            URL imageUrl = new URL(url);

            HttpGet httpRequest = null;

            try {
                httpRequest = new HttpGet(imageUrl.toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse response = httpclient.execute(httpRequest);

            HttpEntity entity = response.getEntity();

            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);

            InputStream inputStream = bufHttpEntity.getContent();

            bitmap = BitmapFactory.decodeStream(inputStream);

            return bitmap;

        }  catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "getBitmap :: "+ e.toString());
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e(TAG, "getBitmap :: " + e.toString());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "getBitmap :: " + e.toString());
            return null;
        }
    }

    /**
     * A private class used to manipulate the loading of bitmap to ImageView.
     * Contains URL of bitmap and ImageView on which to display the bitmap.
     */
    private class PhotoToLoad {
        /**
         * Url of which the  bitmap should be displayed on the <code>imageView</code>.
         */
        public String url;
        /**
         * ImageView to display the bitmap downloaded from <code>url</code>.
         */
        public ImageView imageView;

        public PhotoToLoad(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }
    }

    /**
     * A Runnable to download the Bitmap from the URL.
     * <Code>PhotosLoader</Code> also  have responsibility to show the downloaded bitmap on the <Code>ImageView</Code>.
     */
    class PhotosLoader implements Runnable {
        /**
         * Holds reference of PhotoToLoad object.
         */
        PhotoToLoad photoToLoad;

        /**
         * Constructor.
         *
         * @param photoToLoad PhotoToLoad
         */
        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            Bitmap bmp = getBitmap(photoToLoad.url);
            memoryCache.put(photoToLoad.url, bmp);

            BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
            Activity a = (Activity) photoToLoad.imageView.getContext();
            a.runOnUiThread(bd);
        }
    }

    /**
     * Checks whether the view is reused or not.
     *
     * @param photoToLoad PhotoToLoad
     * @return <code>true</code> if the view is reused else returns <code>false</code>.
     */
    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = mImageViews.get(photoToLoad.imageView);
        return tag == null || !tag.equals(photoToLoad.url);
    }

    /**
     * Displays a bitmap on the ImageView in the UI thread.
     */
    class BitmapDisplayer implements Runnable {
        /**
         * Holds reference of Bitmap.
         */
        Bitmap bitmap;
        /**
         * Holds reference of {@link PhotoToLoad} object.
         */
        PhotoToLoad photoToLoad;

        public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
            bitmap = b;
            photoToLoad = p;
        }

        public void run() {
            if (imageViewReused(photoToLoad)) {
                return;
            }
            if (bitmap != null) {
                photoToLoad.imageView.setImageBitmap(memoryCache.get(photoToLoad.url));
            } else {
                photoToLoad.imageView.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha);
            }
        }
    }

    /**
     * Clears the memory cache anf file cache.
     */
    public void clearCache() {
        memoryCache.clear();
    }

}