package com.sample.androidsampleapp.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Provides APIs to send request and to get response from web service asynchronously. Handles server request,
 * response and provides progress of the request.
 */
public class WebServiceCaller implements IWebServiceCaller {

    /**
     * Constant for connection timeout of server reuest.
     */
    private static final int CONNECTION_TIMEOUT = 10000;
    /**
     * Constant TAG used for debugging purpose.
     */
    private static final String TAG = WebServiceCaller.class.getSimpleName();
    /**
     * Holds reference of IWebServiceCallBackListener.
     */
    public IWebServiceCallBackListener mWebServiceCallBackListener;
    /**
     * Holds Json object used to provide request parameter.
     */
    private JSONObject mPostData;
    /**
     * Holds application Context.
     */
    private Context mContext;
    /**
     * Url of web service.
     */
    private String mURL;

    public WebServiceCaller(Context context, String url, JSONObject postData) {
        this.mContext = context;
        this.mURL = url;
        this.mPostData = postData;
    }

    @Override
    public void setWebServiceCallBackListener(IWebServiceCallBackListener webServiceCallBackListener) {
        if (this.mWebServiceCallBackListener == null) {
            this.mWebServiceCallBackListener = webServiceCallBackListener;
        }
    }

    @Override
    public void removeWebServiceCallBackListener() {
        if (this.mWebServiceCallBackListener != null) {
            this.mWebServiceCallBackListener = null;
        }
    }

    @Override
    public void executeAsync() {
        if(mURL == null){
            Log.d(TAG,"executeAsync :: url is null");
            return;
        }
        HitNetwork hitNetwork = new HitNetwork(mURL, mPostData, mContext);
        hitNetwork.execute();
    }

    /**
     * An AsyncTask to perform server request in a background thread.
     */
    private class HitNetwork extends AsyncTask<String, Integer, String> {
        /**
         * URL to request data from.
         */
        String url;
        /**
         * Holds reference Context.
         */
        Context mContext;
        /**
         * Holds Json object used to provide request parameter.
         */
        JSONObject postData;
        /**
         * Holds the response retrieved from the server.
         */
        String mResponse = "";

        public HitNetwork(String url, JSONObject postData, Context mContext) {
            this.url = url;
            this.postData = postData;
            this.mContext = mContext;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public String doInBackground(String... params) {
            String response;
            response = postJSONData(url, postData);
            mWebServiceCallBackListener.onWebServiceStatus(response != null);
            return response;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mWebServiceCallBackListener.onWebServiceProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mResponse = result;
            mWebServiceCallBackListener.onWebServiceCompleted(result);
        }


        /**
         * Requests server to get the JSON Response. Uses basic HTTP Rest API to request the server.
         *
         * @param URL        url to get response from.
         * @param jsonObject Json object provides request parameters.
         * @return Response string value.
         */
        public String postJSONData(String URL, JSONObject jsonObject) {
            InputStream inputStream;
            String result = "";

            try {
                HttpParams httpParameters = new BasicHttpParams();
                // Set the timeout in milliseconds until a connection is established.
                // The default value is zero, that means the timeout is not used.
                int timeoutConnection = CONNECTION_TIMEOUT;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT)
                // in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 15000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                // 1. create HttpClient
                DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(URL);

                String json;
                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json);

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the
                // content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                } else {
                    result = "Did not work!";
                }
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            // Log.e("res",""+result);

            // 11. return result
            return result;
        }

        /**
         * Reads the input stream and create a String out of it.
         *
         * @param inputStream <code>InputStream</code> to read from.
         * @return complete String created on reading the <code>InputStream</code>
         * @throws IOException error while reading the <code>InputStream</code>
         */
        private String convertInputStreamToString(InputStream inputStream)
                throws IOException {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            return result;
        }
    }
}
