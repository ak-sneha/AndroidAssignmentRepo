package com.sample.androidsampleapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.sample.androidsampleapp.R;
import com.sample.androidsampleapp.Utils.Constants;
import com.sample.androidsampleapp.adapters.SearchListAdapter;
import com.sample.androidsampleapp.components.PopUP;
import com.sample.androidsampleapp.controllers.AppController;
import com.sample.androidsampleapp.controllers.IJSONParser;
import com.sample.androidsampleapp.controllers.IWebServiceCallBackListener;
import com.sample.androidsampleapp.controllers.IWebServiceCaller;
import com.sample.androidsampleapp.controllers.JSONParser;
import com.sample.androidsampleapp.controllers.WebServiceCaller;
import com.sample.androidsampleapp.models.ImageInfoModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Fragment which provides user interface to do image search and show result of search below the search box.
 */
public class ImageSearchFragments extends Fragment implements TextWatcher, IWebServiceCallBackListener {

    protected RecyclerView.LayoutManager mLayoutManager;

    private static final int SPAN_COUNT = 2;
    /**
     * Constant indicating message to show progress bar.
     */
    private static final int SHOW_PROGRESS = 0;
    /**
     * Constant indicating message to hide progress bar.
     */
    private static final int HIDE_PROGRESS = 1;
    /**
     * Delay time in milliseconds before execution of search request to server.
     */
    private static final long SEARCH_TIMER_DELAY = 600;
    /**
     * TAG used for debugging purpose.
     */
    private static final String TAG = ImageSearchFragments.class.getSimpleName();
    /**
     * List of {@link ImageInfoModel}, holds the searched data to display in the list.
     */
    ArrayList<ImageInfoModel> imageInfoModelList = new ArrayList<>();
    /**
     * Holds reference of {@link EditText} used to enter search text.
     */
    private EditText mSearchEditText;
    /**
     * Holds reference of {@link ListView} used to show the search result.
     */
    private RecyclerView mRecyclerView;
    /**
     * Holds reference of an interface to Web Service Call.
     */
    private IWebServiceCaller mWebServiceCaller;
    /**
     * Previously searched text.
     */
    private String mPreviousSearchText;
    /**
     * Holds reference of {@link PopUP} component used to show dialogs.
     */
    private PopUP mPopUP;
    /**
     * Holds an instance of {@link SearchListAdapter}, an adaptor for search list view.
     */
    private SearchListAdapter mSearchListAdapter;
    /**
     * Holds reference of Resources, used to access application resources.
     */
    private Resources mResources;
    /**
     * Time to schedule the request to server for user entered text.
     */
    private Timer timer;
    /**
     * Holds the reference of progress bar.
     */
    private View mProgressBar;
    /**
     * Holds reference of AlertDialog.
     */
    private AlertDialog mAlertDialog;
    /**
     * Welcome text TextView.
     */
    private TextView mWelcomeText;
    /**
     * Object of {@link MessageHandler} used to communicate with the UI thread from other thread.
     */
    private Handler mHandler = new MessageHandler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image_search, null);
        mResources = getActivity().getResources();

        mSearchEditText = (EditText) view.findViewById(R.id.et_search);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mProgressBar = view.findViewById(R.id.loading_view);
        mWelcomeText = (TextView) view.findViewById(R.id.tv_welcome_text);

        mPopUP = new PopUP(getActivity());

        setListenersToView();

        mSearchListAdapter = new SearchListAdapter(getActivity(), imageInfoModelList);

        // Set SearchListAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mSearchListAdapter);

        mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);

        mWelcomeText.setVisibility(View.VISIBLE);

        hideKeyboard();
        return view;
    }

    /**
     * Hides the keyboard.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
    }

    /**
     * Sets event listener to search box views.
     */
    private void setListenersToView() {
        // Set text change listener to edit text.
        mSearchEditText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        // user is typing: reset already started timer (if existing)
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void afterTextChanged(final Editable s) {
        String searchText = s.toString();
        if (mPreviousSearchText == null) {
            mPreviousSearchText = searchText;
            requestSearch(searchText);
        }else{
            if (!mPreviousSearchText.equals(searchText)) {
                requestSearch(searchText);
            }
        }
    }

    /**
     * Checks if server request is required or not. If required schedules a timer to request the server for searched
     * text.
     *
     * @param searchText search text
     */
    private void requestSearch(final String searchText) {

        if (searchText.isEmpty()) {
            mWelcomeText.setVisibility(View.VISIBLE);
            imageInfoModelList.clear();
            mSearchListAdapter.updateListData(imageInfoModelList);
            mSearchListAdapter.notifyDataSetChanged();
        } else {
            // user typed: start the timer
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message message = mHandler.obtainMessage();
                    message.what = SHOW_PROGRESS;
                    mHandler.sendMessage(message);

                    // do your actual work here
                    mWebServiceCaller = new WebServiceCaller(getActivity(), Constants.APP_WEBSERVICE_URL +
                            searchText.replaceAll(" ", "%20"), new JSONObject());
                    mWebServiceCaller.setWebServiceCallBackListener(ImageSearchFragments.this);
                    mWebServiceCaller.executeAsync();
                }
            }, SEARCH_TIMER_DELAY); // 600ms delay before the timer executes the run method from TimerTask
        }
    }

    @Override
    public void onWebServiceProgress(int progressValue) {

    }

    @Override
    public void onWebServiceStatus(boolean status) {

    }

    @Override
    public void onWebServiceCompleted(String data) {
        IJSONParser parser = new JSONParser();
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        try {
            imageInfoModelList = (ArrayList<ImageInfoModel>) parser.getParsedImageInfoData(data);
            if (imageInfoModelList.size() == 0) {
                mProgressBar.setVisibility(View.GONE);
                mAlertDialog = mPopUP.alertDialog(mResources.getString(R.string.no_data_found));
                return;
            }
            // Check whether user have cleared the edit text before displaying the searched list.
            if (!mSearchEditText.getText().toString().isEmpty()) {
                mSearchListAdapter.updateListData(imageInfoModelList);
                mSearchListAdapter.notifyDataSetChanged();
            } else {
                mWelcomeText.setVisibility(View.VISIBLE);
                imageInfoModelList.clear();
                mSearchListAdapter.updateListData(imageInfoModelList);
                mSearchListAdapter.notifyDataSetChanged();
            }
            mProgressBar.setVisibility(View.GONE);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            mProgressBar.setVisibility(View.GONE);
            mAlertDialog = mPopUP.alertDialog(mResources.getString(R.string.error_while_downloading_images));
        }
        hideKeyboard();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppController.getInstance().getImageLoader().clearCache();
    }

    /**
     * Handler to update UI component from other thread.
     */
    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_PROGRESS:
                    mProgressBar.setVisibility(View.VISIBLE);
                    mWelcomeText.setVisibility(View.GONE);
                    break;
                case HIDE_PROGRESS:
                    mProgressBar.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
