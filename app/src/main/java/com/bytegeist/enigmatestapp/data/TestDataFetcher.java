package com.bytegeist.enigmatestapp.data;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class TestDataFetcher {
    public static final String FULL_URL = "http://www.randomtext.me/";

    public static TestDataClient createClient() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FULL_URL) // This client's only method accepts a full URL
                .client(new OkHttpClient.Builder().build())
                .build();

        return retrofit.create(TestDataClient.class);
    }


}
