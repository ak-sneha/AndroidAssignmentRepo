package com.sample.androidsampleapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sample.androidsampleapp.R;
import com.sample.androidsampleapp.controllers.AppController;
import com.sample.androidsampleapp.models.ImageInfoModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to set list of items in List-view.
 */
public class SearchListAdapter extends BaseAdapter {
    /**
     * Holds the data list of ImageInfoModel to show in the ListView.
     */
    private List<ImageInfoModel> mListItems;
    /**
     * Holds the instance of LayoutInflater, responsible to inflate the list item layout.
     */
    private LayoutInflater mInflater;

    /**
     * Constructor.
     *
     * @param context Application Context.
     * @param items   List of ImageInfoModel to display in ListView.
     */
    public SearchListAdapter(Context context, ArrayList<ImageInfoModel> items) {
        this.mListItems = items;
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Updates the list of data.
     *
     * @param items ArrayList<ImageInfoModel>
     */
    public void updateListData(ArrayList<ImageInfoModel> items) {
        this.mListItems = items;
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ImageInfoModel rowItem = (ImageInfoModel) getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_card_view, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.tv_image_title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(rowItem.getTitle());

        String sourceUrl = rowItem.getBitmapUrl();
        if (sourceUrl != null && !sourceUrl.isEmpty()) {
            AppController.getInstance().getImageLoader().displayImage(rowItem.getBitmapUrl(), holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha);
        }
        return convertView;
    }

    /**
     * A ViewHolder to optimize the  performance of ListView.
     */
    private static class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
    }
}
