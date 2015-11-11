package com.planeteers.blindaid.api;

import com.planeteers.blindaid.models.PictureTag;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import rx.Observable;

public interface AlyienApi {
    @GET("API/v1/image-tags")
    Observable<List<PictureTag>> getTags(@Query("url") String imageUrl, @Header("X-AYLIEN-TextAPI-Application-Key") String key, @Header("X-AYLIEN-TextAPI-Application-ID") String secret);
}
