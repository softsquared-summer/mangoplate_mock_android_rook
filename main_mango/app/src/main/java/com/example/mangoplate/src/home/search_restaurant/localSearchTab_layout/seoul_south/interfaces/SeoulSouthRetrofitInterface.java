package com.example.mangoplate.src.home.search_restaurant.searchTab_layout.seoul_south.interfaces;

import com.example.mangoplate.src.home.search_restaurant.searchTab_layout.seoul_south.models.ResultList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SeoulSouthRetrofitInterface {
    @GET("/districts/1")
    Call<ResultList> getDistrictsByRock(@Header("x-access-token")String acessToken);
}