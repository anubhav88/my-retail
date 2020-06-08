package com.target.myRetail.client;

import com.target.myRetail.client.model.TargetProduct;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TargetProductService {

 
    @GET("/products/{productId}")
    public Call<TargetProduct> getProduct(@Path("productId") Integer productId);
}
