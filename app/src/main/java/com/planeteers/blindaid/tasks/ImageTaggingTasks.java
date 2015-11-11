package com.planeteers.blindaid.tasks;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.planeteers.blindaid.api.ImaggaApi;
import com.planeteers.blindaid.helpers.Constants;
import com.planeteers.blindaid.models.PictureTag;
import com.planeteers.blindaid.util.LoggingInterceptor;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Jose on 11/10/15.
 */
public class ImageTaggingTasks {

    public static Observable<List<PictureTag>> getImaggaTags(String imageUrl){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new PictureTag.ImaggaDeserializer())
                .create();

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new LoggingInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.IMAGGA.API_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ImaggaApi imagga = retrofit.create(ImaggaApi.class);

        return imagga.getTags(imageUrl, Constants.IMAGGA.AUTHORIZATION)
                .subscribeOn(Schedulers.io());
    }

    public static Observable<List<PictureTag>> getClarifaiTags(final String imageUrl){

        return Observable.create(new Observable.OnSubscribe<List<PictureTag>>() {
            @Override
            public void call(Subscriber<? super List<PictureTag>> subscriber) {
                ClarifaiClient clarifai = new ClarifaiClient(Constants.CLARIFAI.APP_ID, Constants.CLARIFAI.APP_SECRET);

                ArrayList<PictureTag> tags = new ArrayList<>();
                try {
                    List<RecognitionResult> results =
                            clarifai.recognize(new RecognitionRequest(imageUrl));

                    for (Tag tag : results.get(0).getTags()) {
                        tags.add(new PictureTag(tag.getName(), tag.getProbability()));
                    }
                }catch (ClarifaiException e){
                    Timber.e(e, e.getMessage());
                }

                subscriber.onNext(tags);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io());

    }
}
