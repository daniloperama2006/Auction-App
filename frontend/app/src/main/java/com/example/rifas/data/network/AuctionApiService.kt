package com.example.rifas.data.network

import com.example.rifas.data.model.AuctionSummary
import com.example.rifas.data.model.AuctionDetail
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Represents a bid sent to the backend.
 *
 * @param number The selected number in the auction matrix (0–99).
 * @param amount The bid amount offered by the user.
 */
data class BidRequest(val number: Int, val amount: Long)

/**
 * Retrofit interface for accessing auction-related endpoints.
 */
interface AuctionApiService {

    /**
     * Fetches a list of auction summaries.
     * Supports optional search by name or date substring.
     */
    @GET("auctions")
    suspend fun getAuctions(
        @Query("search") search: String? = null
    ): Response<List<AuctionSummary>>

    /**
     * Retrieves full auction details by ID.
     */
    @GET("auctions/{id}")
    suspend fun getAuctionById(@Path("id") id: Int): Response<AuctionDetail>

    /**
     * Creates a new auction. Requires name, date, minOffer, and optional image.
     * Uses multipart/form-data.
     */
    @Multipart
    @POST("auctions")
    suspend fun createAuction(
        @Part("name") name: RequestBody,
        @Part("date") date: RequestBody,
        @Part("minOffer") minOffer: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<AuctionDetail>

    /**
     * Submits a new bid to a specific auction.
     */
    @POST("auctions/{id}/bids")
    suspend fun postBid(
        @Path("id") id: Int,
        @Body bidRequest: BidRequest
    ): Response<Unit>

    /**
     * Finalizes the auction and marks a winning number.
     */
    @PUT("auctions/{id}/finalize")
    suspend fun finalizeAuction(
        @Path("id") id: Int,
        @Body request: WinnerRequest
    ): Response<Unit>

    /**
     * Deletes an auction by ID.
     */
    @DELETE("auctions/{id}")
    suspend fun deleteAuction(@Path("id") id: Int): Response<Unit>
}
