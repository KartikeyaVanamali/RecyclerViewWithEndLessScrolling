package com.sample.myapplication.network.service.client;

import com.sample.myapplication.BuildConfig;
import com.sample.myapplication.network.RestCall;
import com.sample.myapplication.network.RestRequest;
import com.sample.myapplication.network.base.BaseRestClient;
import com.sample.myapplication.network.service.api.FlickrImagesApi;
import com.sample.myapplication.network.service.event.FlickrImagesResponseEvent;
import com.sample.myapplication.network.service.interceptor.NetworkSleepInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlickrImagesRestClient extends BaseRestClient {
    private static final String BASE_URL = "https://api.flickr.com";
    private static final String API_URL = BASE_URL + "/";
    private static final int TIMEOUT = 10;
    private static final int SLEEP_TIMEOUT = 5;
    private static final boolean USE_SLEEP_INTERCEPTOR = false;
    private static final FlickrImagesRestClient INSTANCE = new FlickrImagesRestClient();

    private Retrofit mRetrofit;
    private FlickrImagesApi mFlickrImagesApi;
    private RestCall mFlickrImagesApiRestCall;

    /**
     * Singleton instance of {@link FlickrImagesRestClient}.
     *
     * @return instance of {@link FlickrImagesRestClient}.
     */
    public static FlickrImagesRestClient getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton construct.
     */
    private FlickrImagesRestClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            okHttpClientBuilder
                    .addInterceptor(httpLoggingInterceptor);
        }

        //simulate long running request
        if (USE_SLEEP_INTERCEPTOR) {
            NetworkSleepInterceptor networkSleepInterceptor = new NetworkSleepInterceptor(
                    SLEEP_TIMEOUT, TimeUnit.SECONDS);
            okHttpClientBuilder
                    .addInterceptor(networkSleepInterceptor);
        }

        mRetrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();

        mFlickrImagesApi = mRetrofit.create(FlickrImagesApi.class);
    }

    /**
     * Invoke getWeather via {@link Call} request.
     *
     * @param text of Api.
     * @param per_page of Api.
     * @param page of Api.
     */
    public void getFlickrImages(String text, int per_page, int page) {
        Call apiWeatherCall = mFlickrImagesApi.getFlickrImages(
                text,
                String.valueOf(per_page),
                String.valueOf(page));

        RestRequest restRequest = new RestRequest.Builder()
                .call(apiWeatherCall)
                .baseResponseEvent(new FlickrImagesResponseEvent())
                .useStickyIntent(true)
                .build();

        mFlickrImagesApiRestCall = call(restRequest);
    }

    /**
     * Cancel of getWeather {@link Call} request.
     */
    public void cancelFlickrImagesRestCall() {
        cancelCall(mFlickrImagesApiRestCall);
    }
}
