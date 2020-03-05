package com.example.google_place_picker;

import androidx.annotation.NonNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface API {
    @NonNull
    @FormUrlEncoded
    @POST("getGeoLocation")
    Call<ResponseBody> findLocation(
            @Field("latitude") @NonNull String lati,
            @Field("longitude") @NonNull String longi
    );
}
