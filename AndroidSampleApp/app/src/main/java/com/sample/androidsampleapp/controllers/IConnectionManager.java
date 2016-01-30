package com.sample.androidsampleapp.controllers;

/**
 * Provides an interface for Connection manager.
 */
public interface IConnectionManager {
    /**
     * Checks for internet connection.
     *
     * @return <code>true</code> if internet connection found otherwise returns <code>false</code>.
     */
    boolean checkInternetConnection();
}

