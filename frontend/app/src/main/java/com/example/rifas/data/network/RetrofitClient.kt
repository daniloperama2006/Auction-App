package com.example.rifas.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object for configuring and providing the Retrofit API client.
 */
object RetrofitClient {

    // Base URL for local development with Android emulator
    private const val BASE_URL = "http://10.0.2.2:3000/"

    /**
     * Lazily initialized instance of [AuctionApiService] for performing network requests.
     */
    val apiService: AuctionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuctionApiService::class.java)
    }
}
