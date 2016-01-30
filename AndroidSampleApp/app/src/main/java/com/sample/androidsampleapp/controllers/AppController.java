package com.sample.androidsampleapp.controllers;

import android.app.Application;
import android.util.Log;

import com.sample.androidsampleapp.image.ImageLoader;


/**
 * An Application class instantiated when the application is loaded in the memory.
 * AppController is to maintain global application state.
 * AppController saves an instance of Application which can be accessed anywhere in the application,
 * while application in running.
 */
public class AppController extends Application {

    /**
     * TAG used for debugging purpose.
     */
    public static final String TAG = AppController.class.getSimpleName();
    /**
     * Holds an instance of ImageLoader.
     */
    private ImageLoader mImageLoader;
    /**
     * AppController saves an instance of Application which can be accessed anywhere in the
     * application, while application in running
     */
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate :: Application instantiated");
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /**
     * Creates an Instance of ImageLoader if it does not exists and returns it.
     *
     * @return Returns an object ImageLoader.
     */
    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader();
        }
        return this.mImageLoader;
    }
}