package com.planeteers.blindaid.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.helpers.ImageTag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

public class ImaggaService extends IntentService {
    private static final String APP_KEY = "acc_342b660b5ee8703";
    private static final String APP_SECRET = "68d3c33a96913081c869d6e4be8b02f";
    private static final String API_URL = "http://api.imagga.com";

    public ImaggaService() {
        super("ImaggaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            String imageUrl = intent.getData().toString();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ImaggaApi imagga = retrofit.create(ImaggaApi.class);
            Call<List<ImageTag>> call = imagga.getTags(imageUrl);

            List<ImageTag> imageTags = new ArrayList<>();
            try { imageTags = call.execute().body();}
            catch (IOException e) { e.printStackTrace(); }

            ArrayList<String> stringTags = new ArrayList<>();
            for (ImageTag imageTag : imageTags) {
                stringTags.add(imageTag.description + ":" + imageTag.confidence);
            }

            sendDataToReceivers(stringTags);
        }
    }

    public interface ImaggaApi {
        @GET("/tagging")
        Call<List<ImageTag>> getTags(@Query("url") String imageUrl);
    }


    private void sendDataToReceivers(ArrayList<String> stringTags) {
        Intent intent = new Intent(Constants.FILTER.RECEIVER_INTENT_FILTER);
        intent.putStringArrayListExtra(Constants.KEY.IMAGGA_TAG_LIST_KEY, stringTags);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
