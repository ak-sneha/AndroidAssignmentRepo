package com.sample.androidsampleapp.models;

/**
 * Model for json data of Bitmap related Information.
 */
public class ImageInfoModel {
    /**
     * Height of bitmap.
     */
    private double mHeight;
    /**
     * Width of bitmap.
     */
    private double mWidth;
    /**
     * Url of bitmap.
     */
    private String mBitmapUrl;
    /**
     * Title of the bitmap.
     */
    private String mTitle;


    public void setBitmapUrl(String source) {
        this.mBitmapUrl = source;
    }

    public String getBitmapUrl() {
        return mBitmapUrl;
    }


    public void setHeight(double height) {
        this.mHeight = height;
    }

    public double getHeight() {
        return mHeight;
    }

    public void setWidth(double width) {
        this.mWidth = width;
    }

    public double getWidth() {
        return mWidth;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}
