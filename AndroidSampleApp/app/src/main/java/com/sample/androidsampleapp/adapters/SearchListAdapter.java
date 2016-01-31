package com.sample.androidsampleapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
    /**
     * Holds the data list of ImageInfoModel to show in the ListView.
     */
    private List<ImageInfoModel> mListItems;

    /**
     * Constructor.
     *
     * @param context Application Context.
     * @param items   List of ImageInfoModel to display in ListView.
     */
    public SearchListAdapter(Context context, ArrayList<ImageInfoModel> items) {
        this.mListItems = items;
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
    public int getItemCount() {
        return mListItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_card_view, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        ImageInfoModel rowItem = mListItems.get(position);
        viewHolder.txtTitle.setText(rowItem.getTitle());
        String sourceUrl = rowItem.getBitmapUrl();
        if (sourceUrl != null && !sourceUrl.isEmpty()) {
            AppController.getInstance().getImageLoader().displayImage(rowItem.getBitmapUrl(), viewHolder.imageView);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.abc_ab_share_pack_mtrl_alpha);
        }
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTitle;

        public ViewHolder(View v) {
            super(v);
            txtTitle = (TextView) v.findViewById(R.id.tv_image_title);
            imageView = (ImageView) v.findViewById(R.id.icon);
        }
    }

}
