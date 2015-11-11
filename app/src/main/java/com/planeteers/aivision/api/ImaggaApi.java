package com.planeteers.aivision.api;

import com.planeteers.aivision.models.PictureTag;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import rx.Observable;

public interface ImaggaApi {
    @GET("v1/tagging")
    Observable<List<PictureTag>> getTags(@Query("url") String imageUrl, @Header("Authorization") String key);
}
