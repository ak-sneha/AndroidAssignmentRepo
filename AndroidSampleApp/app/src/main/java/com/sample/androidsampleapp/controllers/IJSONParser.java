package com.sample.androidsampleapp.controllers;

import com.sample.androidsampleapp.models.ImageInfoModel;

import org.json.JSONException;

import java.util.List;


/**
 * Provides an interface to JSONParser.
 */
public interface IJSONParser {

    /**
     * Parse the JSON string and populates the {@link ImageInfoModel} list.
     *
     * @param data json string data to parse.
     * @return List of {@link ImageInfoModel} populated after parsing the json string.
     * @throws JSONException json-exception.
     */
    List<ImageInfoModel> getParsedImageInfoData(String data) throws JSONException;
}
