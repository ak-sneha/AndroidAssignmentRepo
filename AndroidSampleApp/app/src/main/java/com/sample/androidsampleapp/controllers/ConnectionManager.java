package com.sample.androidsampleapp.controllers;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Represents the Connection Manger.
 */
public class ConnectionManager implements IConnectionManager {
    /**
     * Holds the Context.
     */
    private Context mContext;

    /**
     * Connection manager constructor.
     *
     * @param mContext context.
     */
    public ConnectionManager(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean checkInternetConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() &&
                conMgr.getActiveNetworkInfo().isConnected();
    }


}
