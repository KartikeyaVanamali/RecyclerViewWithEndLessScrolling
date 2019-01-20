package com.sample.myapplication.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sample.myapplication.OnLoadMoreListener;
import com.sample.myapplication.R;
import com.sample.myapplication.display_model.FlickrImagesDisplayModel;
import com.sample.myapplication.utils.CustomImageDownloader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlickrImagesAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<FlickrImagesDisplayModel> data;
    private android.support.v7.widget.RecyclerView recyclerView;
    private OnLoadMoreListener onLoadMoreListener;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    private int visibleThreshold = 3;

    private static int totalResultListCount = 0;

    public FlickrImagesAdapter(
            Context context,
            ArrayList<FlickrImagesDisplayModel> data,
            android.support.v7.widget.RecyclerView recyclerView,
            int totalItemsCount) {
        this.context = context;
        this.data = data;
        this.recyclerView = recyclerView;
        totalResultListCount = totalItemsCount;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager
                            .findLastVisibleItemPosition();
                    if (!loading
                            && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            if(totalResultListCount > 20) {
                                onLoadMoreListener.onLoadMore();
                            }
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public void setResultListSize(int size) {
        totalResultListCount = size;
    }

    public class FlickrImagesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.flickr_image_view_one)
        ImageView flickrImageOne;
        @BindView(R.id.flickr_image_view_two)
        ImageView flickrImageTwo;
        @BindView(R.id.flickr_image_view_three)
        public ImageView flickrImageThree;
        public FlickrImagesViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }


    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.load_more_progress_bar)
        ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if(viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(this.context).inflate(R.layout.flickr_adapter, parent, false);
            holder = new FlickrImagesViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_progress_bar, parent, false);
            holder = new ProgressViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FlickrImagesViewHolder) {
            FlickrImagesDisplayModel displayModel = data.get(position);
            Log.d("Flickr Image",displayModel.getUrlOne());
            CustomImageDownloader imageDownloaderOne = new CustomImageDownloader();
            imageDownloaderOne.downloadImage(displayModel.getUrlOne(), ((FlickrImagesViewHolder) holder).flickrImageOne);
            /*Picasso.get()
                    .load(displayModel.getUrlOne())
                    .placeholder(R.drawable.progress_animation )
                    .error(R.mipmap.flickr)
                    .into(((FlickrImagesViewHolder) holder).flickrImageOne);*/
            Log.d("Flickr Image",displayModel.getUrlTwo());
            CustomImageDownloader imageDownloaderTwo = new CustomImageDownloader();
            imageDownloaderTwo.downloadImage(displayModel.getUrlTwo(), ((FlickrImagesViewHolder) holder).flickrImageTwo);
            /*Picasso.get()
                    .load(displayModel.getUrlTwo())
                    .placeholder(R.drawable.progress_animation )
                    .error(R.mipmap.flickr)
                    .into(((FlickrImagesViewHolder) holder).flickrImageTwo);*/
            Log.d("Flickr Image",displayModel.getUrlThree());
            CustomImageDownloader imageDownloaderThree = new CustomImageDownloader();
            imageDownloaderThree.downloadImage(displayModel.getUrlThree(), ((FlickrImagesViewHolder) holder).flickrImageThree);
            /*Picasso.get()
                    .load(displayModel.getUrlThree())
                    .placeholder(R.drawable.progress_animation )
                    .error(R.mipmap.flickr)
                    .into(((FlickrImagesViewHolder) holder).flickrImageThree);*/
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void refreshAdapter() {
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }
}
