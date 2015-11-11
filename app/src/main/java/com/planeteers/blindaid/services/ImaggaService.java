package com.planeteers.blindaid.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.models.Imagga;
import com.planeteers.blindaid.util.LoggingInterceptor;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.Query;
import timber.log.Timber;

public class ImaggaService extends IntentService {

    public ImaggaService() {
        super("ImaggaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(List.class, new Imagga.ImaggaDeserializer())
                    .create();

            String imageUrl = intent.getData().toString();
            OkHttpClient client = new OkHttpClient();
            client.interceptors().add(new LoggingInterceptor());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.IMAGGA.API_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            ImaggaApi imagga = retrofit.create(ImaggaApi.class);

            Call<List<Imagga>> call = null;
            call = imagga.getTags(imageUrl, Constants.IMAGGA.AUTHORIZATION);

            call.enqueue(new Callback<List<Imagga>>() {
                @Override
                public void onResponse(Response<List<Imagga>> response, Retrofit retrofit) {
                    List<Imagga> imageTags = response.body();

                    ArrayList<String> stringTags = new ArrayList<>();
                    for (Imagga imageTag : imageTags) {
                        stringTags.add(imageTag.tagName + ":" + imageTag.confidence);
                    }
                    sendDataToReceivers(stringTags);
                }

                @Override
                public void onFailure(Throwable t) {
                    Timber.e(t, t.getMessage());
                }
            });

        }
    }

    public interface ImaggaApi {
        @GET("v1/tagging")
        Call<List<Imagga>> getTags(@Query("url") String imageUrl, @Header("Authorization") String key);
    }

        private void sendDataToReceivers(ArrayList<String> stringTags) {
        Intent intent = new Intent(Constants.FILTER.RECEIVER_INTENT_FILTER);
        intent.putStringArrayListExtra(Constants.KEY.IMAGGA_TAG_LIST_KEY, stringTags);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
