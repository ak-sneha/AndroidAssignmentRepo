package com.sample.androidsampleapp.controllers;

/**
 * An interface to get callback event from <code>WebServiceCaller</code>.
 */
public interface IWebServiceCallBackListener {

    /**
     * Gives the progress of Web Service request.
     *
     * @param progressValue progress value.
     */
    void onWebServiceProgress(int progressValue);

    /**
     * Gives the status whether web request is successful or not.
     *
     * @param status <code>true</code> if response is success else <code>false</code>
     */
    void onWebServiceStatus(boolean status);

    /**
     * Gives the response from web service.
     *
     * @param data Web service response.
     */
    void onWebServiceCompleted(String data);

}
