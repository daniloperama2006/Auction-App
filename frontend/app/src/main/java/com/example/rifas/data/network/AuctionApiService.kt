package com.example.rifas.data.network

import com.example.rifas.data.model.AuctionSummary
import com.example.rifas.data.model.AuctionDetail
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

data class BidRequest(val number: Int, val amount: Long)

interface AuctionApiService {

    @GET("auctions")
    suspend fun getAuctions(
        @Query("search") search: String? = null
    ): Response<List<AuctionSummary>>

    @GET("auctions/{id}")
    suspend fun getAuctionById(@Path("id") id: Int): Response<AuctionDetail>

    @Multipart
    @POST("auctions")
    suspend fun createAuction(
        @Part("name") name: RequestBody,
        @Part("date") date: RequestBody,
        @Part("minOffer") minOffer: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<AuctionDetail>

    @POST("auctions/{id}/bids")
    suspend fun postBid(
        @Path("id") id: Int,
        @Body bidRequest: BidRequest
    ): Response<Unit>

    // Ahora recibe un body con el número ganador
    @PUT("auctions/{id}/finalize")
    suspend fun finalizeAuction(
        @Path("id") id: Int,
        @Body request: WinnerRequest
    ): Response<Unit>

    @DELETE("auctions/{id}")
    suspend fun deleteAuction(@Path("id") id: Int): Response<Unit>
}
