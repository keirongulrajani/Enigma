package com.bytegeist.enigmatestapp.data;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface TestDataClient {

    @GET("download/txt/gibberish/p-1/10-20")
    Call<ResponseBody> getTestData();
}
