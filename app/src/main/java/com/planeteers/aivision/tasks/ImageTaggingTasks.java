package com.planeteers.aivision.tasks;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.planeteers.aivision.api.AlyienApi;
import com.planeteers.aivision.api.ImaggaApi;
import com.planeteers.aivision.helpers.Constants;
import com.planeteers.aivision.models.PictureTag;
import com.planeteers.aivision.util.LoggingInterceptor;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;

import retrofit.GsonConverterFactory;
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


    public static Observable<List<PictureTag>> getAlyienTags(String imageUrl){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(List.class, new PictureTag.ImaggaDeserializer())
                .create();

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new LoggingInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ALYIEN.API_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AlyienApi alyien = retrofit.create(AlyienApi.class);

//        return alyien.getTags(imageUrl, Constants.ALYIEN.API_ID, Constants.ALYIEN.API_SECRET)
//                .subscribeOn(Schedulers.io());

        final List<PictureTag> stubList = new ArrayList<>();

        return Observable.create(new Observable.OnSubscribe<List<PictureTag>>() {
            @Override
            public void call(Subscriber<? super List<PictureTag>> subscriber) {
                subscriber.onNext(stubList);
                subscriber.onCompleted();
            }
        })
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
                        tags.add(new PictureTag(tag.getName(), tag.getProbability()*100));
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
