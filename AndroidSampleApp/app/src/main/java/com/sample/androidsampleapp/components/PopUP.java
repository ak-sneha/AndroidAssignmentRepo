package com.sample.androidsampleapp.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;

import com.sample.androidsampleapp.R;

/**
 * Provides API to display a pop-up from anywhere in the application.
 */
public class PopUP {

    /**
     * Holds the Context.
     */
    private Context mContext;
    /**
     * Holds the reference of Resources used to access an application's resources.
     */
    private Resources mResources;

    /**
     * Popup constructor.
     *
     * @param context context.
     */
    public PopUP(Context context) {
        this.mContext = context;
        mResources = mContext.getResources();
    }

    /**
     * Displays an Alert Dialog with the given message.
     *
     * @param message alert message.
     * @return alert dialog.
     */
    public AlertDialog alertDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(mResources.getString(R.string.ok_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return alertDialog.show();

    }
}
