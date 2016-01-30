package com.sample.androidsampleapp.controllers;

/**
 * An interface to access {@link WebServiceCaller}.
 */
public interface IWebServiceCaller {

    /**
     * Sets IWebServiceCallBackListener.
     *
     * @param webServiceCallBackListener IWebServiceCallBackListener
     */
    void setWebServiceCallBackListener(IWebServiceCallBackListener webServiceCallBackListener);

    /**
     * Removes IWebServiceCallBackListener.
     */
    void removeWebServiceCallBackListener();

    /**
     * Executes the web service network request.
     */
    void executeAsync();
}
