package com.sample.myapplication.network.service.api;

import com.sample.myapplication.network.service.model.FlickrImages;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FlickrImagesApi {
    @GET("services/rest/?method=flickr.photos.getRecent&api_key=3e7cc266ae2b0e0d78e279ce8e361736&format=json&nojsoncallback=1&safe_search=1")
    Call<FlickrImages> getFlickrImages(@Query("text") String text, @Query("per_page") String per_page, @Query("page") String page);
}
