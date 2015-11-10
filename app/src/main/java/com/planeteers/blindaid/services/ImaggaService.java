package com.planeteers.blindaid.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.models.Imagga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

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
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.IMAGGA.API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
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
        @Headers(Constants.IMAGGA.AUTHORIZATION)
        @GET("/tagging")
        Call<List<ImageTag>> getTags(@Query("url") String imageUrl);
    }

    public static class ImageTag {
        public String description;
        public Double confidence;

        public ImageTag(String description, Double confidence) {
            this.description = description;
            this.confidence = confidence;
        }
    }


        private void sendDataToReceivers(ArrayList<String> stringTags) {
        Intent intent = new Intent(Constants.FILTER.RECEIVER_INTENT_FILTER);
        intent.putStringArrayListExtra(Constants.KEY.IMAGGA_TAG_LIST_KEY, stringTags);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
