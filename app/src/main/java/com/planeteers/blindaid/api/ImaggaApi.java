package com.planeteers.blindaid.api;

import com.planeteers.blindaid.models.PictureTag;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Jose on 11/10/15.
 */
public interface ImaggaApi {
    @GET("v1/tagging")
    Observable<List<PictureTag>> getTags(@Query("url") String imageUrl, @Header("Authorization") String key);
}
