package com.example.dkdk6.yogicamera.Retrofit;

import com.example.dkdk6.yogicamera.Model.NearBySearchResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by dkdk6 on 2017-05-22.
 */

public interface GoogleApiCall {
    @GET("maps/api/place/nearbysearch/json")
    Call<NearBySearchResult> getNearBySearchResult(@QueryMap Map<String,String>queryValue);
}
