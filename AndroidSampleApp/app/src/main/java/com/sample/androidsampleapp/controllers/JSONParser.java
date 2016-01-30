package com.sample.androidsampleapp.controllers;

import com.sample.androidsampleapp.Utils.ConstantTags;
import com.sample.androidsampleapp.models.ImageInfoModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Provides APIs to parse the JSON string and populate the  data model.
 */
public class JSONParser implements IJSONParser {

    @Override
    public List<ImageInfoModel> getParsedImageInfoData(String data) throws JSONException {

        // We create out JSONObject from the data
        JSONObject jsonObject = new JSONObject(data);

        //Holds an instance of List of <code>ImageInfoModel</code>.
        List<ImageInfoModel> imageInfoModelList = new ArrayList<>();

        JSONObject queryJsonObject = jsonObject.optJSONObject(ConstantTags.DATA_QUERY_TAG);

        if (queryJsonObject != null) {
            JSONObject pagesJsonObject = queryJsonObject.optJSONObject(ConstantTags.DATA_PAGES_TAG);

            if (pagesJsonObject != null) {
                Iterator<String> iterator = pagesJsonObject.keys();
                while (iterator.hasNext()) {
                    ImageInfoModel imageInfoModel = new ImageInfoModel();
                    String key = iterator.next();
                    JSONObject dataObject = pagesJsonObject.optJSONObject(key);

                    if (dataObject != null) {
                        imageInfoModel.setTitle(dataObject.optString(ConstantTags.DATA_IMAGE_TITLE_TAG));

                        JSONObject thumbnail = dataObject.optJSONObject(ConstantTags.DATA_IMAGE_URL_TAG);

                        if (thumbnail != null) {
                            String string = thumbnail.optString(ConstantTags.DATA_IMAGE_SOURCE_TAG);
                            if (string != null) {
                                string = string.replaceAll("\\\\", "");
                                imageInfoModel.setBitmapUrl(string);
                            }
                            imageInfoModel.setWidth(thumbnail.optInt(ConstantTags.DATA_IMAGE_WIDTH_TAG));
                            imageInfoModel.setHeight(thumbnail.optInt(ConstantTags.DATA_IMAGE_HEIGHT_TAG));
                        }
                        imageInfoModelList.add(imageInfoModel);
                    }
                }
            }
        }
        return imageInfoModelList;
    }
}
