package com.sample.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sample.myapplication.adapter.FlickrImagesAdapter;
import com.sample.myapplication.base.BaseActivity;
import com.sample.myapplication.display_model.FlickrImagesDisplayModel;
import com.sample.myapplication.network.service.client.FlickrImagesRestClient;
import com.sample.myapplication.network.service.event.FlickrImagesResponseEvent;
import com.sample.myapplication.network.service.model.FlickrImages;
import com.sample.myapplication.network.service.model.Photo;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    @BindView(R.id.searchText)
    EditText searchEditTextView;

    @BindView(R.id.flickr_images_recycler_view)
    android.support.v7.widget.RecyclerView recyclerView;

    @BindView(R.id.noResultsTextView)
    TextView resultTextView;

    private FlickrImagesAdapter flickrImagesAdapter;

    private ArrayList<FlickrImagesDisplayModel> displayList = new ArrayList<>();

    private int page = 1;
    private int perPage = 30;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        handler = new Handler();
        captureDone();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FlickrImagesRestClient.getInstance().cancelFlickrImagesRestCall();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        FlickrImagesRestClient.getInstance().cancelFlickrImagesRestCall();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(flickrImagesAdapter != null) {
            flickrImagesAdapter.refreshAdapter();
        }
    }

    @OnClick(R.id.button)
    public void onClickButton() {
        if(displayList != null) {
            displayList.clear();
        }
        if(flickrImagesAdapter != null) {
            flickrImagesAdapter.notifyDataSetChanged();
        }
        page = 1;
        hideKeyboard(MainActivity.this);
        showProgressDialog(getString(R.string.fetching) + " " + searchEditTextView.getText().toString());
        FlickrImagesRestClient.getInstance().getFlickrImages(searchEditTextView.getText().toString(), perPage, page);
        Log.d("Flickr Images, Text",searchEditTextView.getText().toString());
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void captureDone() {
        searchEditTextView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    onClickButton();
                    return true;
                }
                return false;
            }
        });
    }

    @Subscribe
    public void onFlickrImagesResponseEvent(FlickrImagesResponseEvent event) {
        closeProgressDialog();

        if (!event.isCanceled()) {
            if (event.hasResponse()) {
                if(flickrImagesAdapter == null) {
                    FlickrImages flickrImages = event.getResponse();
                    Log.i("FlickrImages", "Received");
                    resultTextView.setVisibility(View.GONE);
                    resultTextView.setText(flickrImages.toString());
                    recyclerView.setVisibility(View.VISIBLE);
                    setFlickrImagesAdapter(flickrImages);
                    Log.d("FlickrImages", "Received Flickr Images.");
                } else {
                    final FlickrImages flickrImages = event.getResponse();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(displayList.size() > 0) {
                                displayList.remove(displayList.size() - 1);
                                flickrImagesAdapter.notifyItemRemoved(displayList.size());
                            }
                            flickrImagesAdapter.setResultListSize(flickrImages.getPhotos().getPhoto().size());
                            for(int i=0; i<flickrImages.getPhotos().getPhoto().size(); i=i+3) {
                                addItemToList(i, flickrImages);
                                flickrImagesAdapter.notifyItemInserted(displayList.size());
                            }
                            flickrImagesAdapter.setLoaded();
                        }
                    });
                }
            } else {
                resultTextView.setText(getString(R.string.no_data));
                recyclerView.setVisibility(View.GONE);
                Log.d("FlickrImages","Error getting Flickr Images.");
            }
        } else {
            resultTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            Log.d("FlickrImages","Canceled getting Flickr Images.");
        }
    }

    private void setFlickrImagesAdapter(FlickrImages flickrImages) {
        flickrImagesAdapter = new FlickrImagesAdapter(
                MainActivity.this,
                getDisplayList(flickrImages),
                recyclerView,
                flickrImages.getPhotos().getPhoto().size());

        flickrImagesAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                displayList.add(null);
                flickrImagesAdapter.notifyItemInserted(displayList.size() - 1);
                page++;
                makeRequest(page);
            }
        });
        recyclerView.setAdapter(flickrImagesAdapter);
    }

    private void makeRequest(int page) {
        FlickrImagesRestClient.getInstance().getFlickrImages(searchEditTextView.getText().toString(), perPage, page);
    }

    private ArrayList<FlickrImagesDisplayModel> getDisplayList(FlickrImages flickrImages) {
        for(int i=0; i<flickrImages.getPhotos().getPhoto().size(); i=i+3) {
            addItemToList(i, flickrImages);
        }
        return displayList;
    }

    private void addItemToList(int i, FlickrImages flickrImages) {
        int index = i;
        Photo photo1 = null;
        Photo photo2 = null;
        Photo photo3 = null;
        if(index < flickrImages.getPhotos().getPhoto().size()) {
            photo1 = flickrImages.getPhotos().getPhoto().get(index);
            index++;
        }
        if(index < flickrImages.getPhotos().getPhoto().size()) {
            photo2 = flickrImages.getPhotos().getPhoto().get(index);
            index++;
        }
        if(index < flickrImages.getPhotos().getPhoto().size()) {
            photo3 = flickrImages.getPhotos().getPhoto().get(index);
        }
        StringBuilder urlOne = null;
        if(photo1 != null) {
            urlOne = new StringBuilder("http://farm")
                    .append(photo1.getFarm())
                    .append(".static.flickr.com/")
                    .append(photo1.getServer())
                    .append("/")
                    .append(photo1.getId())
                    .append("_")
                    .append(photo1.getSecret())
                    .append(".jpg");
        }
        StringBuilder urlTwo = null;
        if(photo2 != null) {
            urlTwo = new StringBuilder("http://farm")
                    .append(photo2.getFarm())
                    .append(".static.flickr.com/")
                    .append(photo2.getServer())
                    .append("/")
                    .append(photo2.getId())
                    .append("_")
                    .append(photo2.getSecret())
                    .append(".jpg");
        }
        StringBuilder urlThree = null;
        if(photo3 != null) {
            urlThree = new StringBuilder("http://farm")
                    .append(photo3.getFarm())
                    .append(".static.flickr.com/")
                    .append(photo3.getServer())
                    .append("/")
                    .append(photo3.getId())
                    .append("_")
                    .append(photo3.getSecret())
                    .append(".jpg");
        }
        displayList.add(new FlickrImagesDisplayModel(urlOne.toString(), urlTwo.toString(), urlThree.toString()));
    }
}
