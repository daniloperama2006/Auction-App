package com.example.rifas.data.network

import com.example.rifas.data.model.Auction
import retrofit2.Response
import retrofit2.http.*

interface AuctionApiService {

    @GET("auctions")
    suspend fun getAuctions(): Response<List<Auction>>

    @GET("auctions/{id}")
    suspend fun getAuctionById(@Path("id") id: Int): Response<Auction>

    @POST("auctions")
    suspend fun createAuction(@Body auction: Auction): Response<Auction>

    @PUT("auctions/{id}")
    suspend fun updateAuction(@Path("id") id: Int, @Body auction: Auction): Response<Auction>

    @PUT("auctions/{id}/winner")
    suspend fun saveWinner(@Path("id") id: Int, @Body winner: WinnerRequest): Response<Unit>
}
